/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.adessaigne.cameldemo.basic.common;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.google.common.io.Resources;
import com.sun.istack.Nullable;
import org.h2.jdbcx.JdbcConnectionPool;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;

import static java.lang.String.format;
import static java.lang.System.exit;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempDirectory;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractExercise implements Runnable {
    protected final Logger log = getLogger(getClass());

    private final Object workingDirectoryToken = new Object();
    private volatile Path workingDirectory;
    private final Object databaseToken = new Object();
    private volatile JdbcConnectionPool database;

    /**
     * Starts the exercise
     */
    @Override
    public final void run() {
        log.info("Welcome to the " + getClass().getSimpleName() + ".");
        log.info("You working directory is " + getWorkingDirectory());

        final Simulator simulator = new Simulator(getWorkingDirectory());
        final ConcurrentMap<String, Object> registry = new ConcurrentHashMap<>();
        final DefaultCamelContext context = new DefaultCamelContext(new MapBasedRegistry(registry));
        final ShutdownHandler shutdownHandler = new ExerciseShutdownHandler(log, simulator, context);

        configureCamel(context, registry);

        Future<Void> simulation = startExercise(context, simulator);

        waitForSimulationToComplete(simulation);

        waitForCamelProcessing();

        if (isDatabaseUsed()) {
            displayDatabaseContent();
        }

        if (isWebServiceUsed()) {
            callWebService();
        }

        log.info("Exercise complete, stopping the platform.");
        shutdownHandler.shutdown();
    }

    /**
     * Configures the map used by the Camel registry.
     *
     * @param registry Registry to configure
     */
    protected void configureRegistry(ConcurrentMap<String, Object> registry) {
        // No-op, must be override
    }

    /**
     * Configures the camel routes.
     *
     * @return RouteBuilder that configures the Camel routes
     * @see org.apache.camel.builder.RouteBuilder
     */
    protected abstract RouteBuilder configureCamelRoutes();

    /**
     * Gets the working directory.
     *
     * @return The working directory
     */
    protected Path getWorkingDirectory() {
        if (workingDirectory == null) {
            synchronized (workingDirectoryToken) {
                if (workingDirectory == null) {
                    try {
                        workingDirectory = createTempDirectory("exercise");
                    } catch (IOException e) {
                        handleFatalError("Cannot generate working directory.", e);
                    }
                }
            }
        }
        return workingDirectory;
    }

    /**
     * Gets the database. It returns {@code null} if the database is not configured on this exercise.
     *
     * @return Database
     */
    @Nullable
    protected final DataSource getDatabase() {
        if (!isDatabaseUsed()) {
            return null;
        }

        if (database == null) {
            synchronized (databaseToken) {
                if (database == null) {
                    try {
                        database = createDatabase();
                    } catch (SQLException | IOException e) {
                        handleFatalError("Cannot initialize the database", e);
                    }
                }
            }
        }
        return database;
    }

    /**
     * Configures the Camel routes and the registry
     *
     * @param context Camel context to configure
     * @param registry Registry to configure
     */
    private void configureCamel(DefaultCamelContext context, ConcurrentMap<String, Object> registry) {
        try {
            configureRegistry(registry);
            context.addRoutes(configureCamelRoutes());
        } catch (Exception e) {
            handleFatalError("Cannot configure the Camel routes.", e);
        }
    }

    /**
     * Starts the exercise. It starts the context and the simulator.
     *
     * @param context Context to start
     * @param simulator Simulator to start
     * @return A future of the simulation task
     */
    private Future<Void> startExercise(DefaultCamelContext context, Simulator simulator) {
        try {
            context.start();
        } catch (Exception e) {
            handleFatalError("Cannot start the Camel context.", e);
        }
        return simulator.generate(2, SECONDS);
    }

    /**
     * Waits for the simulation task to complete
     *
     * @param simulation Simulation task to wait for
     */
    private void waitForSimulationToComplete(Future<Void> simulation) {
        try {
            simulation.get();
        } catch (InterruptedException e) {
            // Propagate interruption
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            handleFatalError("Something went wrong during the simulation.", e.getCause());
        }
    }

    /**
     * Wait 5 seconds in order to ensure that camel has processed everything
     */
    private void waitForCamelProcessing() {
        try {
            SECONDS.sleep(5);
        } catch (InterruptedException e) {
            // Propagate interruption
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Displays the database content.
     */
    private void displayDatabaseContent() {
        try (Connection connection = getDatabase().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT YEAR, ACTOR, MOVIE FROM JAMES_BOND ORDER BY YEAR ASC");
                if (!resultSet.next()) {
                    log.error("The database is empty !");
                } else {
                    log.info("Content of the database: ");
                    do {
                        int year = resultSet.getInt("YEAR");
                        String actor = resultSet.getString("ACTOR");
                        String movie = resultSet.getString("MOVIE");
                        log.info(format("\tYear=%d, Actor=%s, Movie=%s", year, actor, movie));
                    } while (resultSet.next());
                }
            }
        } catch (SQLException e) {
            handleFatalError("Cannot display the content of the database", e);
        }
    }

    /**
     * Handles a fatal error. It stops the platform immediately after logging the error.
     *
     * @param message Log message
     * @param e error
     */
    private void handleFatalError(String message, Throwable e) {
        log.error(message, e);
        exit(-1);
    }

    /**
     * Indicates whether or not the database is used for this exercise.
     *
     * @return {@code true} if the database is used for this exercise
     */
    private boolean isDatabaseUsed() {
        return getClass().getAnnotation(WithDatabase.class) != null;
    }

    /**
     * Creates and initializes the database.
     *
     * @return Database
     * @throws SQLException
     * @throws IOException
     */
    private JdbcConnectionPool createDatabase() throws SQLException, IOException {
        JdbcConnectionPool db = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "sa");
        try (Connection connection = db.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(Resources.toString(AbstractExercise.class.getResource("database.sql"), UTF_8));
            }
        }
        return db;
    }

    private boolean isWebServiceUsed() {
        return getClass().getAnnotation(WithWebService.class) != null;
    }

    private void callWebService() {
        try {
            int port = getClass().getAnnotation(WithWebService.class).port();
            String uri = "http://localhost:" + port + "/bond/1965/title";
            String text = new ClientResource(uri).get().getText();
            log.info("Web service result: " + text);
        } catch (IOException e) {
            handleFatalError("Cannot call the web service", e);
        }
    }

    private static final class ExerciseShutdownHandler implements ShutdownHandler {
        private final Logger log;
        private final Simulator simulator;
        private final DefaultCamelContext context;

        public ExerciseShutdownHandler(Logger log, Simulator simulator, DefaultCamelContext context) {
            this.log = log;
            this.simulator = simulator;
            this.context = context;
        }

        @Override
        public void shutdown() {
            // Stopping simulator
            simulator.stop();

            // Stopping camel
            context.getShutdownStrategy().setTimeout(1);
            context.getShutdownStrategy().setTimeUnit(MILLISECONDS);
            try {
                context.stop();
            } catch (Exception e) {
                log.error("Cannot stop the context.", e);
            }

            exit(0);
        }
    }
}

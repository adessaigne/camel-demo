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

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.slf4j.Logger;

import static java.lang.System.exit;
import static java.nio.file.Files.createTempDirectory;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractExcercise implements Runnable {
    protected final Logger log = getLogger(getClass());

    private final Object workingDirectoryToken = new Object();
    private volatile Path workingDirectory;

    /**
     * Starts the excercise
     */
    @Override
    public final void run() {
        log.info("Welcome to the " + getClass().getSimpleName() + ".");
        log.info("You working directory is " + getWorkingDirectory());

        final Simulator simulator = new Simulator(getWorkingDirectory());
        final DefaultCamelContext context = new DefaultCamelContext();
        final ShutdownHandler shutdownHandler = new ExerciseShutdownHandler(log, simulator, context);

        // Configure the Camel context
        try {
            context.addRoutes(configureCamelRoutes());
        } catch (Exception e) {
            log.error("Cannot configure the Camel routes.", e);
            exit(-1);
        }

        // Start the simulator and Camel
        Future<Void> simulation = simulator.generate(2, SECONDS);
        try {
            context.start();
        } catch (Exception e) {
            log.error("Cannot start the Camel context.", e);
            exit(-1);
        }

        // Automatically stop the platform once the simulation is complete
        try {
            // Wait for simulation to complete
            simulation.get();
            // Wait 5 seconds before automatic shutdown
            SECONDS.sleep(5);
            log.info("Simulation complete, stopping the platform.");
            shutdownHandler.shutdown();
        } catch (InterruptedException e) {
            // Propagate interruption
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("Something went wrong during the simulation.", e.getCause());
        }
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
                        log.error("Cannot generate working directory.", e);
                        exit(-1);
                    }
                }
            }
        }
        return workingDirectory;
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

            System.exit(0);
        }
    }
}

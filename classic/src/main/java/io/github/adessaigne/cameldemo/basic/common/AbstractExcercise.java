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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.slf4j.Logger;

import static java.lang.System.exit;
import static java.nio.file.Files.createTempDirectory;
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
        final Simulator simulator = new Simulator(getWorkingDirectory());
        final DefaultCamelContext context = new DefaultCamelContext();

        // Start the console
        new Thread(new CommandLineReader(simulator, context), "console").start();

        // Create and configure the Camel context
        try {
            context.addRoutes(configureCamelRoutes());
        } catch (Exception e) {
            log.error("Cannot configure the Camel routes.", e);
            exit(-1);
        }

        // Start the simulator and Camel
        simulator.generate(1, SECONDS);
        try {
            context.start();
        } catch (Exception e) {
            log.error("Cannot start the Camel context.", e);
            exit(-1);
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
}

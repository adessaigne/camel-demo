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
package io.github.adessaigne.cameldemo.basic.simulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.slf4j.LoggerFactory.getLogger;

public final class Simulator {
    private static final Logger LOG = getLogger(Simulator.class);
    private static final String[] FILES = {"007-SC.xml", "007-GL.xml", "007-RM.xml", "007-TD.xml", "007-PB.xml", "007-DC.xml"};

    private final Path directory;
    private final ExecutorService executorService = newSingleThreadExecutor();

    /**
     * Creates a new instance of the simulator.
     *
     * @param directory Directory for simulated files.
     */
    public Simulator(Path directory) {
        checkNotNull(directory, "The directory must be defined.");
        checkPath(directory);

        this.directory = directory;
    }

    /**
     * Generates the simulated files.
     *
     * @param delay Delay between 2 file generation
     * @param unit  Time unit of the delay between 2 file generation
     */
    public Future<Void> generate(long delay, TimeUnit unit) {
        checkNotNull(unit, "The time unit must be defined");

        return executorService.submit(new SimulationTask(directory, delay, unit));
    }

    private static void checkPath(Path directory) {
        File file = directory.toFile();
        checkArgument(file.exists(), "The directory doesn't exist (%s).", directory);
        checkArgument(file.isDirectory(), "The provided directory is not a directory (%s).", directory);
        checkArgument(file.canWrite(), "Cannot write in the provided directory (%s).", directory);
        checkArgument(file.list().length == 0, "The provided directory must be empty (%s).", directory);
    }

    private static final class SimulationTask implements Callable<Void> {
        private final Path directory;
        private final long delay;
        private final TimeUnit unit;

        private SimulationTask(Path directory, long delay, TimeUnit unit) {
            this.directory = directory;
            this.delay = delay;
            this.unit = unit;
        }

        @Override
        public Void call() throws Exception {
            for (String file : FILES) {
                unit.sleep(delay);
                LOG.info("Generating file: " + file);

                // We're creating the file in another directory and then move it atomically.
                try {
                    Path temp = createTempFile("tmp-simulator-", ".xml");
                    copy(Simulator.class.getResourceAsStream(file), temp, REPLACE_EXISTING);
                    move(temp, directory.resolve(file), ATOMIC_MOVE);
                } catch (IOException e) {
                    LOG.error("Cannot generate simulated file", e);
                    throw e;
                }
            }

            return null;
        }
    }
}

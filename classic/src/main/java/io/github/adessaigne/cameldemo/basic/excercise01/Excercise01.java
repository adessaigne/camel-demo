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
package io.github.adessaigne.cameldemo.basic.excercise01;

import java.nio.file.Path;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import io.github.adessaigne.cameldemo.basic.simulator.Simulator;

import static java.nio.file.Files.createTempDirectory;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Your mission: print the processed file name into the log.
 * <p/>
 * Useful links:
 * http://camel.apache.org/file2.html
 * http://camel.apache.org/logeip.html
 * http://camel.apache.org/simple.html
 */
public final class Excercise01 {
    public static void main(String... args) throws Exception {
        // Create a simulator
        final Path directory = createTempDirectory("Excercise01-");
        final Simulator simulator = new Simulator(directory);

        // Configure Camel
        final DefaultCamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //TODO Replace "xxxx" by something useful
                from("xxxx")
                        .log("Processing xxxx.");
            }
        });

        // Start simulation and Camel
        simulator.generate(2, SECONDS);
        context.start();
    }
}

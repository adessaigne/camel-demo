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
package io.github.adessaigne.cameldemo.basic.excercise02;

import java.nio.file.Path;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import io.github.adessaigne.cameldemo.basic.simulator.Simulator;

import static java.nio.file.Files.createTempDirectory;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Your mission: read the file XML content and print the name of each James Bond actor
 */
public final class Excercise02 {
    public static void main(String... args) throws Exception {
        // Create a simulator
        final Path directory = createTempDirectory("Excercise01-");
        final Simulator simulator = new Simulator(directory);

        // Configure Camel
        final DefaultCamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //TODO: write your route here
            }
        });

        // Start simulation and Camel
        simulator.generate(2, SECONDS);
        context.start();
    }
}
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
package io.github.adessaigne.cameldemo.basic.solution03;

import java.nio.file.Path;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import io.github.adessaigne.cameldemo.basic.simulator.Simulator;
import org.w3c.dom.Document;

import static java.nio.file.Files.createTempDirectory;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Your mission: print the title of each james bond movie along with its actor
 *
 * @link http://camel.apache.org/enterprise-integration-patterns.html
 */
public final class Solution03 {
    public static void main(String... args) throws Exception {
        // Create a simulator
        final Path directory = createTempDirectory("Excercise01-");
        final Simulator simulator = new Simulator(directory);

        // Configure Camel
        final DefaultCamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:" + directory)
                        .convertBodyTo(Document.class)
                        .setHeader("Actor", xpath("/bond/actor/name/text()"))
                        .split(xpath("/bond/movies/movie"))
                        .setHeader("Movie", xpath("movie/title/text()"))
                        .log("${header.Actor} is James Bond in ${header.Movie}.");
            }
        });

        // Start simulation and Camel
        simulator.generate(2, SECONDS);
        context.start();
    }
}

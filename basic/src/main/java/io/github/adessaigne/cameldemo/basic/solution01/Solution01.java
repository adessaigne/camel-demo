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
package io.github.adessaigne.cameldemo.basic.solution01;

import org.apache.camel.builder.RouteBuilder;

import io.github.adessaigne.cameldemo.basic.common.AbstractExercise;

/**
 * Your mission: log the name of the file that has been created into the working directory.
 * <p/>
 * Every 2 seconds, one new file is created into the working directory (that can be accessed using the
 * {@link #getWorkingDirectory()} method).
 * <p/>
 * All you need to do is to complete the Camel route definition in the {@link #configureCamelRoutes()} method.
 * <p/>
 * The Camel documentation mostly uses the following format:
 * - A basic introduction,
 * - The list of all options available for this component,
 * - Sample code usages
 *
 * @link http://camel.apache.org/file2.html
 * @link http://camel.apache.org/logeip.html
 * @link http://camel.apache.org/simple.html
 */
final class Solution01 extends AbstractExercise {
    public static void main(String... args) {
        new Solution01().run();
    }

    @Override
    protected RouteBuilder configureCamelRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:" + getWorkingDirectory())
                        .log("Processing ${header.CamelFileNameOnly}.");
            }
        };
    }
}

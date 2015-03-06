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

import org.apache.camel.builder.RouteBuilder;

import io.github.adessaigne.cameldemo.basic.common.AbstractExercise;
import org.w3c.dom.Document;

/**
 * Your mission: print the title of each james bond movie along with its actor
 *
 * @link http://camel.apache.org/enterprise-integration-patterns.html
 */
final class Solution03 extends AbstractExercise {
    public static void main(String... args) {
        new Solution03().run();
    }

    @Override
    protected RouteBuilder configureCamelRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:" + getWorkingDirectory())
                        .convertBodyTo(Document.class)
                        .setHeader("Actor", xpath("/bond/actor/name/text()", String.class))
                        .split(xpath("/bond/movies/movie"))
                        .setHeader("Movie", xpath("movie/title/text()", String.class))
                        .log("${header.Actor} is James Bond in ${header.Movie}.");
            }
        };
    }
}

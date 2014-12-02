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
package io.github.adessaigne.cameldemo.basic.solution02;

import org.apache.camel.builder.RouteBuilder;

import io.github.adessaigne.cameldemo.basic.common.AbstractExcercise;
import org.w3c.dom.Document;

/**
 * Your mission: read the file XML content and log the name of each James Bond actor.
 * <p/>
 * Like for the first exercise, the files are automatically copied into the working directory. You can see their content
 * in the {@code resources} folder under the {@code io.github.adessaigne.cameldemo.basic.common} package.
 */
final class Solution02 extends AbstractExcercise {
    public static void main(String... args) {
        new Solution02().run();
    }

    @Override
    protected RouteBuilder configureCamelRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:" + getWorkingDirectory())
                        .convertBodyTo(Document.class)
                        .setHeader("Actor", xpath("/bond/actor/name/text()"))
                        .log("James Bond is ${header.Actor}.");
            }
        };
    }
}

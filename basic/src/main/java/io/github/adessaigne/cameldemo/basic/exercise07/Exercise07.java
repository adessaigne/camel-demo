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
package io.github.adessaigne.cameldemo.basic.exercise07;

import java.util.concurrent.ConcurrentMap;

import org.apache.camel.builder.RouteBuilder;

import io.github.adessaigne.cameldemo.basic.common.AbstractExercise;
import io.github.adessaigne.cameldemo.basic.common.WithDatabase;
import io.github.adessaigne.cameldemo.basic.common.WithWebService;

/**
 * Your mission: provide a REST web service for accessing James Bond movie titles
 * <p/>
 * The REST request will be "/bond/{year}/title" and is automatically executed at the end of the test.
 */
@WithDatabase
@WithWebService(port = 1234)
final class Exercise07 extends AbstractExercise {
    public static void main(String... args) {
        new Exercise07().run();
    }

    @Override
    protected void configureRegistry(ConcurrentMap<String, Object> registry) {
        //TODO: maybe there's something to configure here
    }

    @Override
    protected RouteBuilder configureCamelRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //TODO: write your route here.
                // Yes, you can write a REST web service in Camel :)
            }
        };
    }
}

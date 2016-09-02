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
package io.github.adessaigne.cameldemo.basic.exercise05;

import java.util.concurrent.*;
import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import io.github.adessaigne.cameldemo.basic.common.AbstractExercise;

/**
 * Your mission: use a bean stored in the registry in order to transform data
 */
public class Exercise05 extends AbstractExercise {
    public static void main(String... args) {
        new Exercise05().run();
    }

    @Override
    protected void configureRegistry(ConcurrentMap<String, Object> registry) {
        //TODO: replace with proper values
        registry.put("xxxx", "xxxx");
    }

    @Override
    protected RouteBuilder configureCamelRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //TODO: configure your route here, remember you need to use the registry
            }
        };
    }

    // Used by Camel using reflection
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static final class SomeBean {
        //TODO: replace with proper values
        public final String transform(@Header("xxxx") String actor, @Header("xxxx") String movie) {
            return actor + " played 007 in " + movie + ".";
        }
    }
}

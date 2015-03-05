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
package io.github.adessaigne.cameldemo.basic.solution07;

import java.util.concurrent.ConcurrentMap;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import io.github.adessaigne.cameldemo.basic.common.AbstractExcercise;
import io.github.adessaigne.cameldemo.basic.common.WithDatabase;
import io.github.adessaigne.cameldemo.basic.common.WithWebService;
import org.w3c.dom.Document;

/**
 * Your mission: provide a REST web service for accessing James Bond movie titles
 * <p/>
 * The REST request will be "/bond/{year}/title" and is automatically executed at the end of the test.
 */
@WithDatabase
@WithWebService(port = 1234)
final class Solution07 extends AbstractExcercise {
    public static void main(String... args) {
        new Solution07().run();
    }

    @Override
    protected void configureRegistry(ConcurrentMap<String, Object> registry) {
        registry.put("db", getDatabase());
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
                        .setHeader("Year", xpath("movie/@year", Integer.class))
                        .setHeader("Movie", xpath("movie/title/text()", String.class))
                        .to("sql:insert into JAMES_BOND (YEAR, ACTOR, MOVIE) VALUES (:#${header.Year}, :#${header.Actor}, :#${header.Movie})?dataSource=#db");

                restConfiguration()
                        .component("restlet")
                        .host("localhost")
                        .port(1234)
                        .bindingMode(RestBindingMode.auto);

                rest("/bond")
                        .get("{Year}/title")
                        .to("sql:select MOVIE from JAMES_BOND where YEAR = :#${header.Year}?dataSource=#db&outputType=SelectOne");
            }
        };
    }
}

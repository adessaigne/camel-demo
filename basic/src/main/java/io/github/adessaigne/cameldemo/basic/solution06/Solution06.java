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
package io.github.adessaigne.cameldemo.basic.solution06;

import java.util.concurrent.ConcurrentMap;

import org.apache.camel.builder.RouteBuilder;

import io.github.adessaigne.cameldemo.basic.common.AbstractExcercise;
import io.github.adessaigne.cameldemo.basic.common.WithDatabase;
import org.w3c.dom.Document;

/**
 * Your mission: insert the data into a database.
 * <p/>
 * The database {@link javax.sql.DataSource} can be accessed with the {@link #getDatabase()} method.
 * <p/>
 * The content of the database is automatically displayed at the end of the excercise.
 * <p/>
 * Here is the database schema:<pre>
 * CREATE TABLE JAMES_BOND (
 *   YEAR INT NOT NULL,
 *   ACTOR VARCHAR NOT NULL,
 *   MOVIE VARCHAR  NOT NULL,
 *   PRIMARY KEY (YEAR)
 * )
 * </pre>
 */
@WithDatabase
final class Solution06 extends AbstractExcercise {
    public static void main(String... args) {
        new Solution06().run();
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
            }
        };
    }
}

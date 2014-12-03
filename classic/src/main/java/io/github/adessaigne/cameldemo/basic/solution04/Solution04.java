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
package io.github.adessaigne.cameldemo.basic.solution04;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;

import io.github.adessaigne.cameldemo.basic.common.AbstractExcercise;
import org.w3c.dom.Document;

/**
 * Your mission: create a CSV file with all the james bond movies with their actor
 *
 * @link http://camel.apache.org/processor.html
 */
final class Solution04 extends AbstractExcercise {
    public static void main(String... args) {
        new Solution04().run();
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
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Map<String, String> data = new LinkedHashMap<>();
                                data.put("Movie", exchange.getIn().getHeader("Movie", String.class));
                                data.put("Actor", exchange.getIn().getHeader("Actor", String.class));
                                exchange.getIn().setBody(data);
                            }
                        })
                        .marshal(new CsvDataFormat())
                        .to("file:?fileName=007.csv&fileExist=Append");
            }
        };
    }
}

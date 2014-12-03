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
package io.github.adessaigne.cameldemo.basic.common;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.camel.spi.Registry;

import com.google.common.collect.ImmutableMap;

import static com.google.common.collect.ImmutableSet.copyOf;

final class MapBasedRegistry implements Registry {
    private final ConcurrentMap<String, Object> map;

    public MapBasedRegistry(ConcurrentMap<String, Object> map) {
        this.map = map;
    }

    @Override
    public Object lookupByName(String name) {
        return map.get(name);
    }

    @Override
    public <T> T lookupByNameAndType(String name, Class<T> type) {
        Object value = lookupByName(name);
        return value != null && type.isInstance(value) ? type.cast(value) : null;
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        ImmutableMap.Builder<String, T> result = ImmutableMap.builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (type.isInstance(entry.getValue())) {
                result.put(entry.getKey(), type.cast(entry.getValue()));
            }
        }
        return result.build();
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        return copyOf(findByTypeWithName(type).values());
    }

    @Override
    @Deprecated
    public Object lookup(String name) {
        return lookupByName(name);
    }

    @Override
    @Deprecated
    public <T> T lookup(String name, Class<T> type) {
        return lookupByNameAndType(name, type);
    }

    @Override
    @Deprecated
    public <T> Map<String, T> lookupByType(Class<T> type) {
        return findByTypeWithName(type);
    }
}

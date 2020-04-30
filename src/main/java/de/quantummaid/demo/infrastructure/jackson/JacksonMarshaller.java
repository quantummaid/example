/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.demo.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.quantummaid.httpmaid.marshalling.Marshaller;
import de.quantummaid.httpmaid.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JacksonMarshaller implements Unmarshaller, Marshaller {
    private final ObjectMapper objectMapper;

    public static JacksonMarshaller jacksonMarshaller() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.setDeserializerModifier(new AlwaysStringValueJacksonDeserializerModifier());
        objectMapper.setSerializationInclusion(NON_NULL);
        objectMapper.registerModule(simpleModule);
        return new JacksonMarshaller(objectMapper);
    }

    @Override
    public String marshall(final Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(format("Error during marshalling of '%s'", map), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> unmarshall(final String string) {
        if (string.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(string, Map.class);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(format("Error during unmarshalling of '%s'", string), e);
        }
    }
}

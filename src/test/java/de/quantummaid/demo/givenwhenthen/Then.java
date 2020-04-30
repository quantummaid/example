/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.demo.givenwhenthen;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static java.util.Optional.ofNullable;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "then")
public final class Then {
    private final Context context;
    private final When when;

    public When andWhen() {
        return when;
    }

    public Then theStatusCodeWas(final int expectedStatusCode) {
        assertThat("unexpected status code", context.statusCode(), is(expectedStatusCode));
        return this;
    }

    public Then theJwtHasBeenSet() {
        assertThat(context.jwt(), not(nullValue()));
        return this;
    }

    public Then theResponseBodyWas(final String body) {
        final String actualBody = ofNullable(context.body()).orElse("");
        assertThat(actualBody, is(body));
        return this;
    }
}

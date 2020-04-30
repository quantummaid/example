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

package de.quantummaid.demo.givenwhenthen;

import de.quantummaid.demo.Application;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.demo.givenwhenthen.Context.emptyContext;
import static de.quantummaid.demo.givenwhenthen.DatabaseBackdoor.databaseBackdoor;
import static de.quantummaid.demo.givenwhenthen.PortProvider.provideFreePort;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientForTheHost;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Given {
    //private static final String basePath = "/quantummaid-demo-lambda";
    private static final String BASE_PATH = "";

    private final HttpMaidClient client;

    public static Given givenTheApplication() {
        final Given given = new Given(SingletonHolder.CLIENT);
        SingletonHolder.DATABASE_BACKDOOR.reset();
        return given;
    }

    public When when() {
        return When.when(client, BASE_PATH, emptyContext());
    }

    private static final class SingletonHolder {
        private static final HttpMaidClient CLIENT;
        private static final DatabaseBackdoor DATABASE_BACKDOOR;

        static {
            final int port = provideFreePort();
            Application.startApplication(port);
            CLIENT = aHttpMaidClientForTheHost("localhost").withThePort(port).viaHttp().build();
            /*
            client = aHttpMaidClientForTheHost("wgcpl5vjkf.execute-api.eu-central-1.amazonaws.com")
                    .withThePort(443).viaHttps().withBasePath("/quantummaid-demo-lambda").build();
             */
            DATABASE_BACKDOOR = databaseBackdoor();
        }
    }
}

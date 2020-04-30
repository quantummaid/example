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

import de.quantummaid.httpmaid.client.HttpClientRequestBuilder;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.httpmaid.client.SimpleHttpResponseObject;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpClientRequest.aPostRequestToThePath;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "when")
public final class When {
    private final HttpMaidClient client;
    private final String basePath;
    private final Context context;

    public Then aLoginIsDone(final String username, final String password) {
        final SimpleHttpResponseObject response = client.issue(aPostRequestToThePath(basePath + "/public/login")
                .withTheBody(format("{ \"username\": \"%s\", \"password\": \"%s\" }", username, password)));
        context.setJwt(response.getBody());
        context.setStatusCode(response.getStatusCode());
        return Then.then(context, this);
    }

    public Then anUnauthenticatedRequestIsDoneTo(final HttpClientRequestBuilder<SimpleHttpResponseObject> requestBuilder) {
        final SimpleHttpResponseObject response = client.issue(requestBuilder);
        context.setJwt(response.getBody());
        context.setStatusCode(response.getStatusCode());
        return Then.then(context, this);
    }

    public Then anAuthenticatedRequestIsDoneTo(final HttpClientRequestBuilder<SimpleHttpResponseObject> requestBuilder) {
        final String authHeader = context.optionalJwt().orElse("xyz");
        requestBuilder.withHeader("Authorization", authHeader);
        return anUnauthenticatedRequestIsDoneTo(requestBuilder);
    }

    public Then aLoginIsDoneAsAdmin() {
        return aLoginIsDone("admin", "admin");
    }

    public Then aLoginIsDoneAsUser() {
        return aLoginIsDone("user", "user");
    }

    public Then aUserIsAdded(final String id, final String displayName) {
        final String authHeader = context.optionalJwt().orElse("xyz");
        final SimpleHttpResponseObject response = client.issue(aPostRequestToThePath(basePath + "/admin/add").withTheBody("" +
                "{ \"user\": {\n" +
                "       \"userIdentifier\": \"" + id + "\",\n" +
                "       \"displayName\": \"" + displayName + "\"\n" +
                "   }\n" +
                "}")
                .withHeader("Authorization", authHeader));
        context.setStatusCode(response.getStatusCode());
        return Then.then(context, this);
    }

    public Then aUserIsDeleted(final String id) {
        final String authHeader = context.optionalJwt().orElse("xyz");
        final SimpleHttpResponseObject response = client.issue(aPostRequestToThePath(basePath + "/admin/delete").withTheBody("" +
                "{\n" +
                "   \"userIdentifier\": \"" + id + "\"" +
                "}")
                .withHeader("Authorization", authHeader));
        context.setStatusCode(response.getStatusCode());
        return Then.then(context, this);
    }

    public Then theUsersAreListed() {
        final String authHeader = context.optionalJwt().orElse("xyz");
        final SimpleHttpResponseObject response = client.issue(aGetRequestToThePath(basePath + "/user/list")
                .withHeader("Authorization", authHeader));
        context.setStatusCode(response.getStatusCode());
        context.setBody(response.getBody());
        return Then.then(context, this);
    }
}

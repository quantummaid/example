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

package de.quantummaid.demo.infrastructure;

import de.quantummaid.demo.infrastructure.jackson.JacksonMarshaller;
import de.quantummaid.demo.usecases.AddUserUseCase;
import de.quantummaid.demo.usecases.AuthenticatedUser;
import de.quantummaid.demo.usecases.DeleteUserUseCase;
import de.quantummaid.demo.usecases.ListUsersUseCase;
import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.handler.PageNotFoundException;
import de.quantummaid.httpmaid.handler.http.HttpHandler;
import de.quantummaid.httpmaid.path.PathTemplate;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;

import java.security.Key;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.demo.infrastructure.jackson.JacksonMarshaller.jacksonMarshaller;
import static de.quantummaid.demo.usecases.AuthenticatedUser.deserialize;
import static de.quantummaid.httpmaid.HttpMaid.anHttpMaid;
import static de.quantummaid.httpmaid.events.EventConfigurators.mappingAuthenticationInformation;
import static de.quantummaid.httpmaid.exceptions.ExceptionConfigurators.toMapExceptionsOfType;
import static de.quantummaid.httpmaid.http.Http.StatusCodes.NOT_FOUND;
import static de.quantummaid.httpmaid.http.Http.StatusCodes.UNAUTHORIZED;
import static de.quantummaid.httpmaid.http.HttpRequestMethod.POST;
import static de.quantummaid.httpmaid.http.headers.ContentType.json;
import static de.quantummaid.httpmaid.marshalling.MarshallingConfigurators.toMarshallContentType;
import static de.quantummaid.httpmaid.path.PathTemplate.pathTemplate;
import static de.quantummaid.httpmaid.security.SecurityConfigurators.toAuthenticateUsingHeader;
import static de.quantummaid.httpmaid.security.SecurityConfigurators.toAuthorizeRequestsUsing;
import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parser;
import static java.lang.Boolean.parseBoolean;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class HttpMaidFactory {
    private static final PathTemplate USER_PATH = pathTemplate("/user/*");
    private static final Map<String, String> USERS = Map.of(
            "admin", "admin",
            "user", "user");

    private HttpMaidFactory() {
    }

    public static HttpMaid httpMaid() {
        final Key key = JwtFactory.jwtKey();
        final JwtParser jwtParser = parser().setSigningKey(key);
        final JacksonMarshaller jacksonMarshaller = jacksonMarshaller();
        return anHttpMaid()
                .post("/public/login", loginHandler(key))
                .get("/user/list", ListUsersUseCase.class)
                .serving(AddUserUseCase.class, mappingAuthenticationInformation()).forRequestPath("/admin/add").andRequestMethod(POST)
                .serving(DeleteUserUseCase.class, mappingAuthenticationInformation()).forRequestPath("/admin/delete").andRequestMethod(POST)
                .configured(toAuthenticateUsingHeader("Authorization", challenge -> authenticatedUserFromJwt(challenge, jwtParser))
                        .notFailingOnMissingAuthenticationForRequestsTo("/public/*")
                        .rejectingUnauthenticatedRequestsUsing((request, response) -> response.setStatus(UNAUTHORIZED)))
                .configured(toAuthorizeRequestsUsing(
                        (authenticationInformation, request) -> authenticationInformation
                                .map(object -> (AuthenticatedUser) object)
                                .map(user -> USER_PATH.matches(request.path()) || user.isAdmin())
                                .orElse(false))
                        .exceptRequestsTo("/public/*")
                        .rejectingUnauthorizedRequestsUsing((request, response) -> response.setStatus(UNAUTHORIZED)))

                .configured(toMapExceptionsOfType(PageNotFoundException.class, (exception, response) -> response.setStatus(NOT_FOUND)))
                .configured(toMarshallContentType(json(), jacksonMarshaller, jacksonMarshaller))
                .build();
    }

    private static HttpHandler loginHandler(final Key key) {
        return (request, response) -> {
            final String username = (String) request.bodyMap().get("username");
            final String password = (String) request.bodyMap().get("password");

            if (!USERS.containsKey(username) || !USERS.get(username).equals(password)) {
                throw new RuntimeException("Login failed");
            }

            final String adminClaim;
            if ("admin".equals(username)) {
                adminClaim = "true";
            } else {
                adminClaim = "false";
            }
            final String jwt = builder()
                    .setSubject(username)
                    .claim("admin", adminClaim)
                    .signWith(key).compact();
            response.setBody(jwt);
        };
    }

    private static Optional<AuthenticatedUser> authenticatedUserFromJwt(final String jwt, final JwtParser parser) {
        try {
            final Claims claims = parser.parseClaimsJws(jwt).getBody();
            final String username = claims.getSubject();
            final boolean admin = parseBoolean((String) claims.get("admin"));
            return of(deserialize(username, String.valueOf(admin)));
        } catch (final JwtException e) {
            return empty();
        }
    }
}

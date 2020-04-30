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

package de.quantummaid.demo;

import org.junit.jupiter.api.Test;

import static de.quantummaid.demo.givenwhenthen.Given.givenTheApplication;
import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpClientRequest.aPostRequestToThePath;

public final class DemoTests {

    @Test
    public void theAddRouteIsProtectedWithoutLogin() {
        givenTheApplication()
                .when().aUserIsAdded("foo", "Foo Bar")
                .theStatusCodeWas(401);
    }

    @Test
    public void theDeleteRouteIsProtectedWithoutLogin() {
        givenTheApplication()
                .when().aUserIsDeleted("foo")
                .theStatusCodeWas(401);
    }

    @Test
    public void loginAsAdminIsPossible() {
        givenTheApplication()
                .when().aLoginIsDoneAsAdmin()
                .theStatusCodeWas(200)
                .theJwtHasBeenSet();
    }

    @Test
    public void loginAsUserIsPossible() {
        givenTheApplication()
                .when().aLoginIsDoneAsUser()
                .theStatusCodeWas(200)
                .theJwtHasBeenSet();
    }

    @Test
    public void userCanBeAdded() {
        givenTheApplication()
                .when().aLoginIsDoneAsAdmin()
                .andWhen().aUserIsAdded("foo", "Foo Bar")
                .theStatusCodeWas(200);
    }

    @Test
    public void nonAdminUserCannotAddUsers() {
        givenTheApplication()
                .when().aLoginIsDoneAsUser()
                .andWhen().aUserIsAdded("foo", "Foo Bar")
                .theStatusCodeWas(401);
        // TODO
    }

    @Test
    public void userCanBeDeleted() {
        givenTheApplication()
                .when().aLoginIsDoneAsAdmin()
                .andWhen().aUserIsAdded("foo", "Foo Bar")
                .theStatusCodeWas(200)
                .andWhen().aUserIsDeleted("foo")
                .theStatusCodeWas(200);
    }

    @Test
    public void usersAreEmptyInTheBeginning() {
        givenTheApplication()
                .when().aLoginIsDoneAsUser()
                .andWhen().theUsersAreListed()
                .theStatusCodeWas(200)
                .theResponseBodyWas("{\"users\":[]}");
    }

    @Test
    public void unauthenticatedRequestToUnmappedPath() {
        givenTheApplication()
                .when().anUnauthenticatedRequestIsDoneTo(aGetRequestToThePath("/unmapped"))
                .theStatusCodeWas(401)
                .theResponseBodyWas("");
    }

    @Test
    public void authenticatedRequestToUnmappedPath() {
        givenTheApplication()
                .when().aLoginIsDoneAsAdmin()
                .andWhen().anAuthenticatedRequestIsDoneTo(aGetRequestToThePath("/unmapped"))
                .theStatusCodeWas(404)
                .theResponseBodyWas("");
    }

    @Test
    public void unauthenticatedInjectionAttack() {
        givenTheApplication()
                .when().anUnauthenticatedRequestIsDoneTo(aPostRequestToThePath("/admin/add")
                .withTheBody("" +
                        "{  \"currentUser\": {\n" +
                        "       \"userName\": \"admin\"," +
                        "       \"admin\": true" +
                        "   }," +
                        "   \"user\": {\n" +
                        "       \"userIdentifier\": \"foo\",\n" +
                        "       \"displayName\": \"Foo\"\n" +
                        "   }\n" +
                        "}"))
                .theStatusCodeWas(401)
                .theResponseBodyWas("");
    }

    @Test
    public void authenticatedInjectionAttack() {
        givenTheApplication()
                .when().aLoginIsDoneAsUser()
                .andWhen().anAuthenticatedRequestIsDoneTo(aPostRequestToThePath("/admin/add")
                .withTheBody("" +
                        "{  \"currentUser\": {\n" +
                        "       \"userName\": \"admin\"," +
                        "       \"admin\": true" +
                        "   }," +
                        "   \"user\": {\n" +
                        "       \"userIdentifier\": \"foo\",\n" +
                        "       \"displayName\": \"Foo\"\n" +
                        "   }\n" +
                        "}"))
                .theStatusCodeWas(401)
                .theResponseBodyWas("");
    }
}

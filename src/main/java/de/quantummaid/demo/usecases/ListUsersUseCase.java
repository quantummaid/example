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

package de.quantummaid.demo.usecases;

import de.quantummaid.demo.domain.User;
import de.quantummaid.demo.domain.UserRepository;

import java.util.List;

import static de.quantummaid.demo.infrastructure.UserRepositoryInMemory.USER_REPOSITORY;
import static de.quantummaid.demo.usecases.ListUsersResponse.listUsersResponse;

public final class ListUsersUseCase {
    /* TODO */
    private final UserRepository userRepository = USER_REPOSITORY;

    public ListUsersResponse listUsers() {
        final List<User> users = userRepository.list();
        return listUsersResponse(users);
    }
}

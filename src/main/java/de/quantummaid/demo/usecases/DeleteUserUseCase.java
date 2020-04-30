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

import de.quantummaid.demo.domain.UnknownUserException;
import de.quantummaid.demo.domain.UserIdentifier;
import de.quantummaid.demo.domain.UserRepository;

import static de.quantummaid.demo.infrastructure.UserRepositoryInMemory.USER_REPOSITORY;

public final class DeleteUserUseCase {
    /* TODO */
    private final UserRepository userRepository = USER_REPOSITORY;

    public void deleteUser(final DeleteUserRequest deleteUserRequest) throws UnknownUserException {
        System.out.println("currentUser = " + deleteUserRequest.currentUser);
        final UserIdentifier userIdentifier = deleteUserRequest.userIdentifier;
        userRepository.delete(userIdentifier);
    }
}

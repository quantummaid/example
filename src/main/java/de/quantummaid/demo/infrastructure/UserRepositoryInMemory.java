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

import de.quantummaid.demo.domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.quantummaid.demo.domain.UnknownUserException.unknownUserException;
import static de.quantummaid.demo.domain.UserAlreadyExistsException.userAlreadyExistsException;

public final class UserRepositoryInMemory implements de.quantummaid.demo.domain.UserRepository {
    /* TODO */
    public static final UserRepository USER_REPOSITORY = userRepository();

    private final Map<UserIdentifier, User> users = new ConcurrentHashMap<>();

    public static UserRepository userRepository() {
        return new UserRepositoryInMemory();
    }

    @Override
    public void add(final User user) throws UserAlreadyExistsException {
        if (users.containsKey(user.userIdentifier)) {
            throw userAlreadyExistsException(user.userIdentifier);
        }
        users.put(user.userIdentifier, user);
    }

    @Override
    public void delete(final UserIdentifier identifier) throws UnknownUserException {
        if (!users.containsKey(identifier)) {
            throw unknownUserException(identifier);
        }
        users.remove(identifier);
    }

    @Override
    public List<User> list() {
        return new LinkedList<>(users.values());
    }
}

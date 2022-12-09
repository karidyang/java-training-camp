/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.karidyang.service;

import io.karidyang.exception.UserException;
import io.karidyang.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存实现 UserRegistrationService
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Service("userRegistrationService")
public class InMemoryUserRegistrationService implements UserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserRegistrationService.class);

    private Map<Long, User> usersCache = new ConcurrentHashMap<>();

    @Override
    public Boolean registerUser(User user) throws UserException {
        Long id = user.getId();

        Boolean success = usersCache.putIfAbsent(id, user) == null;
        logger.info("registerUser() == {}", success);
        return success;
    }
}

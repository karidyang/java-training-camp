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
package io.karidyang;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


import static java.util.Arrays.asList;

/**
 * Micrometer 配置
 *
 * @author karidyang
 * @since 1.0.0
 */
@Configuration
public class MicrometerConfiguration implements MeterRegistryCustomizer {


    @Value("${spring.application.name:default}")
    private String applicationName;

    @Autowired
    private Environment environment;

    @Override
    public void customize(MeterRegistry registry) {

        registry.config().commonTags(asList(
                // 应用维度的 Tag
                Tag.of("application", applicationName),
                // 环境变量
                Tag.of("env", environment.getActiveProfiles()[0])

        ));
    }
}

package io.karidyang.micrometer.binder.redis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-11-20
 * @since TODO
 */
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisTemplateWrapper<String, Object> redisTemplateWrapper(RedisTemplate redisTemplate, ApplicationContext context) {
        return new RedisTemplateWrapper<String, Object>(redisTemplate, context);
    }
}

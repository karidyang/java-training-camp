package io.karidyang.micrometer.binder.redis;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;

/**
 * RedisTemplate 包装类
 * @author karidyang
 * @since 1.0.0
 */
public class RedisTemplateWrapper<K, V> extends RedisTemplate<K, V> implements MeterBinder {
    private final static Class<?>[] REDIS_CONNECTION_TYPES = new Class<?>[] {RedisConnection.class};
    private MeterRegistry meterRegistry;
    private final ApplicationContext context;


    public RedisTemplateWrapper(RedisTemplate<K, V> redisTemplate, ApplicationContext context) {
        this.context = context;
        initSettings(redisTemplate);
    }

    private void initSettings(RedisTemplate<K, V> redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate can't be null");
        // Set the connection
        setConnectionFactory(redisTemplate.getConnectionFactory());
        setExposeConnection(redisTemplate.isExposeConnection());

        // Set the RedisSerializers
        setEnableDefaultSerializer(redisTemplate.isEnableDefaultSerializer());
        setDefaultSerializer(redisTemplate.getDefaultSerializer());
        setKeySerializer(redisTemplate.getKeySerializer());
        setValueSerializer(redisTemplate.getValueSerializer());
        setHashKeySerializer(redisTemplate.getHashKeySerializer());
        setHashValueSerializer(redisTemplate.getHashValueSerializer());
        setStringSerializer(redisTemplate.getStringSerializer());
    }


    @Override
    protected RedisConnection preProcessConnection(RedisConnection redisConnection, boolean existingConnection) {
        Assert.notNull(redisConnection, "redisConnection can't be null");

        ClassLoader classLoader = this.context.getClassLoader();

        return (RedisConnection) Proxy.newProxyInstance(classLoader, REDIS_CONNECTION_TYPES,
                new RedisConnectionInvocationHandler(redisConnection, meterRegistry));

    }

    public void bindTo(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
}

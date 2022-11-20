package io.karidyang.micrometer.binder.redis;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.redis.connection.RedisConnection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RedisConnection 动态代理
 * @author karidyang
 * @since 1.0.0
 */
public class RedisConnectionInvocationHandler implements InvocationHandler {
    private final static List<String> meterMethods = Arrays.asList("set");
    private RedisConnection redisConnection;
    private final MeterRegistry meterRegistry;
    private final AtomicLong totalRequest = new AtomicLong(0);
    private final AtomicLong successRequest = new AtomicLong(0);

    public RedisConnectionInvocationHandler(RedisConnection redisConnection, MeterRegistry meterRegistry) {
        this.redisConnection = redisConnection;

        this.meterRegistry = meterRegistry;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(method, args);

        long start = System.currentTimeMillis();
        try {
            Object returnVal = method.invoke(redisConnection, args);
            long responseTime = System.currentTimeMillis() - start;
            after(method, returnVal, responseTime);
            long success = successRequest.incrementAndGet();
            long total = totalRequest.incrementAndGet();
            Gauge.builder(buildMeterName(method.getName(), "successRate"), () -> success / total).register(meterRegistry);
            return returnVal;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void before(Method method, Object[] args) {
        if (meterMethods.contains(method.getName())) {
            Counter counter = Counter.builder(buildMeterName(method.getName(), "count")).register(meterRegistry);
            counter.increment();
        }
    }

    private void after(Method method, Object returnVal, long responseTime) {
        if (meterMethods.contains(method.getName())) {
            Timer timer = Timer.builder(buildMeterName(method.getName(), "responseTime")).register(meterRegistry);
            timer.record(responseTime, TimeUnit.MILLISECONDS);
        }
    }

    private String buildMeterName(String methodName, String type) {
        return "redis." + methodName + "." + type + ".value";
    }
}

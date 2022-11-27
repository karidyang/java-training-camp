## 要求
Redis Spring API监控指标注册到MeterRegister

## 核心功能点
### RedisConnection
RedisConnection是Spring对于Redis各种命令的一个抽象，是Spring Redis命令的入口，因此，对RedisConnection进行拦截，那么就可以实现将Redis Api监控指标注册到MeterRegister上了。
## 实现
Demo地址：[https://github.com/karidyang/java-training-camp/tree/master/homework-6](https://github.com/karidyang/java-training-camp/tree/master/homework-6)

1. 通过继承RedisTemplate的复写preProcessConnection方法
```java
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

        return (RedisConnection) Proxy.newProxyInstance(classLoader, REDIS_CONNECTION_TYPES, new RedisConnectionInvocationHandler(redisConnection, meterRegistry));

    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
}

```

2. 利用动态代理 RedisConnection的方式，拦截想统计的Meter方法，实现了Counter、Timer、Gauge的监控指标
- Counter - 统计命令的执行次数
- Timer - 统计命令的执行时间
- Gauge - 统计命令的执行成功率
```java
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

    @Override
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


```

3. 在应用中配置 RedisTemplate Bean
```java
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisTemplateWrapper<String, Object> redisTemplateWrapper(RedisTemplate redisTemplate, ApplicationContext context) {
        return new RedisTemplateWrapper<String, Object>(redisTemplate, context);
    }
}
    
```

4. 应用注入 RedisTemplateWrapper，并和使用RedisTemplate一样调用相关方法

比如： redisTemplateWrapper.opsForValue().set("echo", number);

## 验证

- 访问  [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) ，看到相关的metrics已经注册上了

![image.png](https://cdn.nlark.com/yuque/0/2022/png/21482149/1668951775003-0c9667d4-bb17-41b1-9ae7-7b81bd219196.png#averageHue=%23fef7f6&clientId=u241e7934-1c97-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=162&id=u8c87d2cd&margin=%5Bobject%20Object%5D&name=image.png&originHeight=324&originWidth=870&originalType=binary&ratio=1&rotation=0&showTitle=false&size=40791&status=done&style=none&taskId=u5f0d855c-5352-4182-a97c-4ba20d7b951&title=&width=435)

- 访问 [http://localhost:8080/actuator/metrics/redis.set.count.value](http://localhost:8080/actuator/metrics/redis.set.count.value)， 能够看到指标数据

![image.png](https://cdn.nlark.com/yuque/0/2022/png/21482149/1668951807119-c47539ca-1d5e-449f-8662-90e9fb73b343.png#averageHue=%23fdfcfb&clientId=u241e7934-1c97-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=329&id=u56f47fc9&margin=%5Bobject%20Object%5D&name=image.png&originHeight=658&originWidth=628&originalType=binary&ratio=1&rotation=0&showTitle=false&size=44296&status=done&style=none&taskId=uef2d294c-b179-4be8-9652-5376aaabef6&title=&width=314)

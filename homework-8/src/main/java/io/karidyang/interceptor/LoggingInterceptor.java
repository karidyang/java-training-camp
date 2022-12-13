package io.karidyang.interceptor;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-13
 * @since TODO
 */
public class LoggingInterceptor {

    private final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @RuntimeType
    public Object doLog(@Origin Method method, @SuperCall Callable<?> callable) {
        logger.info("before method ["+method.getName()+"] invoke ...");

        try {
            return callable.call();
        } catch (Exception e) {
            logger.error("error occupied", e);
            throw new RuntimeException(e);
        } finally {
            logger.info("after method ["+method.getName()+"] invoke ...");
        }
    }

}

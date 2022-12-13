package io.karidyang.configuration;

import io.karidyang.annotation.Logger;
import io.karidyang.interceptor.LoggingInterceptor;
import io.karidyang.service.InMemoryUserRegistrationService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.context.annotation.Configuration;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * {@link Logger } BeanPostProcessor
 *
 * @author karidyang
 * @date 2022-12-13
 * @since 1.0.0
 */
@Configuration
public class LoggingBeanPostProcessor extends AbstractAnnotationByteBuddyBeanPostProcessor<Logger> {


    private final LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

    @Override
    protected DynamicType.Unloaded<?> doIntercept(Class<?> beanType) {
        return new ByteBuddy()
                .subclass(beanType)
                .name(beanType.getSimpleName() + "$ByteBuddy")
//                .method(named("registerUser"))
                .method(ElementMatchers.isAnnotatedWith(Logger.class))
                .intercept(MethodDelegation.to(this.loggingInterceptor))
                .make();
    }

    @Override
    protected Class<Logger> getAnnotationClass() {
        return Logger.class;
    }
}

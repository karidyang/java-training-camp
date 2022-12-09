package io.karidyang.service;

import io.karidyang.model.User;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-09
 * @since TODO
 */
public class UserRegistrationServiceTest {

    @Test
    public void test_implements_UserRegistrationService() throws Exception {
        Class<? extends InMemoryUserRegistrationService> loaded = new ByteBuddy()
                .subclass(InMemoryUserRegistrationService.class)
                .name(InMemoryUserRegistrationService.class.getName() + "Impl")
                .method(named("registerUser"))
                .intercept(MethodDelegation.to(ByteBuddyUserRegistrationService.class))
                .make()
                .load(InMemoryUserRegistrationService.class.getClassLoader())
                .getLoaded();

        UserRegistrationService instance = (UserRegistrationService)loaded.getDeclaredConstructors()[0].newInstance();
        User user = new User();
        user.setId(1L);
        user.setName("karidyang");
        Boolean result = instance.registerUser(user);
        Assertions.assertTrue(result);
    }
}

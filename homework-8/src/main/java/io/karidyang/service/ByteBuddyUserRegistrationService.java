package io.karidyang.service;

import io.karidyang.exception.UserException;
import io.karidyang.model.User;
import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-09
 * @since TODO
 */
public class ByteBuddyUserRegistrationService {

    public Boolean registerUser(User user, @Super InMemoryUserRegistrationService zuper) throws UserException {
        System.out.println("this is ByteBuddyUserRegistrationService");
        return zuper.registerUser(user);
    }
}

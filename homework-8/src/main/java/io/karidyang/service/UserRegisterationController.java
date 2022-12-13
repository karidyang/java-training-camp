package io.karidyang.service;

import io.karidyang.annotation.Logger;
import io.karidyang.exception.UserException;
import io.karidyang.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @date 2022-12-13
 * @since 1.0.0
 */
@RestController
public class UserRegisterationController implements UserRegistrationService {
    @Autowired
    private UserRegistrationService userRegistrationService;

    @Logger
    @ResponseBody
    @Override
    public Boolean registerUser(User user) throws UserException {
        return userRegistrationService.registerUser(user);
    }
}

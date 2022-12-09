package io.karidyang.service;

import io.karidyang.exception.UserException;
import io.karidyang.model.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-09
 * @since TODO
 */
public interface UserRegistrationService {

    @PostMapping(value = "/user/register", produces = "application/json;v=3") // V3
    Boolean registerUser(@RequestBody @Validated User user) throws UserException;

}

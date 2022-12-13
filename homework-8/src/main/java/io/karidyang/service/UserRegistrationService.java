package io.karidyang.service;

import io.karidyang.exception.UserException;
import io.karidyang.model.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户注册服务接口（Open Feign、Dubbo 等公用）
 * <p>
 * JAX-RS
 * Dubbo
 * OpenFeign
 * RMI -> 动态代理（基于接口编程）
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface UserRegistrationService {

    @PostMapping(value = "/user/register", produces = "application/json;v=3")
    Boolean registerUser(@RequestBody @Validated User user) throws UserException;

}

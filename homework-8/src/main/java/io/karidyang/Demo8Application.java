package io.karidyang;

import io.karidyang.model.User;
import io.karidyang.service.UserRegisterationController;
import io.karidyang.service.UserRegistrationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-13
 * @since TODO
 */
@SpringBootApplication
public class Demo8Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Demo8Application.class);
        UserRegisterationController service = context.getBean(UserRegisterationController.class);
        User user = new User();
        user.setId(1L);
        user.setName("zhangsan");
        Boolean registerUser = service.registerUser(user);
        System.out.println(registerUser);
    }
}

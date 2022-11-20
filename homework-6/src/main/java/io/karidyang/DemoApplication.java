package io.karidyang;

import io.karidyang.micrometer.binder.redis.RedisConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-11-20
 * @since TODO
 */
@SpringBootApplication
@Import(value = {
        RedisConfiguration.class
})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

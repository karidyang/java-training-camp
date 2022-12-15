package io.karidyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-13
 * @since TODO
 */
@SpringBootApplication
@Import(MetricsGatewayAutoConfiguration.class)
public class Demo9Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo9Application.class);
    }
}

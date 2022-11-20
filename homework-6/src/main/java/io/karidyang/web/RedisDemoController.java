package io.karidyang.web;

import io.karidyang.micrometer.binder.redis.RedisTemplateWrapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-11-20
 * @since TODO
 */
@RestController
@RequestMapping("/redis")
public class RedisDemoController {

    @Autowired
    private RedisTemplateWrapper<String, Object> redisTemplateWrapper;

    @GetMapping("/echo")
    public String echo() {
        try {
            String number = RandomStringUtils.random(10, true, true);
            redisTemplateWrapper.opsForValue().set("echo", number);
            return number;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }
}

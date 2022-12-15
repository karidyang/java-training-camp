package io.karidyang;

import io.karidyang.filter.ResponseTimeMeterGatewayFilterFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.context.annotation.Bean;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-15
 * @since TODO
 */
@ConditionalOnClass(MeterRegistry.class)
public class MetricsGatewayAutoConfiguration {

    @Bean
    @ConditionalOnEnabledFilter
    public ResponseTimeMeterGatewayFilterFactory responseTimeMeterGatewayFilterFactory() {
        return new ResponseTimeMeterGatewayFilterFactory();
    }
}

package io.karidyang;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-11-27
 * @since TODO
 */
@Configuration
public class MicrometerInfluxConfiguration {
    @Value("${management.metrics.export.influx.org}")
    private String org;
    @Value("${management.metrics.export.influx.bucket}")
    private String bucket;
    @Value("${management.metrics.export.influx.token}")
    private String token;

    @Bean
    @ConditionalOnMissingBean(MeterRegistry.class)
    public MeterRegistry meterRegistry() {
        InfluxConfig config = new InfluxConfig() {

            @Override
            public String org() {
                return org;
            }

            @Override
            public String bucket() {
                return bucket;
            }

            @Override
            public String token() {
                return token;
            }

            @Override
            public String get(String k) {
                return null; // accept the rest of the defaults
            }
        };
        return new InfluxMeterRegistry(config, Clock.SYSTEM);
    }


}

package io.karidyang.influx;

import io.micrometer.core.instrument.Clock;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * TODO
 *
 * @author karidyang
 * @date 2022-11-29
 * @since 1.0
 **/
@AutoConfiguration(
        before = { CompositeMeterRegistryAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class },
        after = MetricsAutoConfiguration.class)
@ConditionalOnBean(Clock.class)
@ConditionalOnClass(InfluxMeterRegistry.class)
@EnableConfigurationProperties(LocalFileInfluxProperties.class)
public class InfluxMetricsExportConfiguration {
    private final LocalFileInfluxProperties properties;

    public InfluxMetricsExportConfiguration(LocalFileInfluxProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalFileInfluxConfig influxConfig() {
        return new LocalFileInfluxConfig(properties);
    }

    @Bean
    @ConditionalOnMissingBean(InfluxMeterRegistry.class)
    public InfluxMeterRegistry influxMeterRegistry(LocalFileInfluxConfig config,  Clock clock) {
        return new LocalFileInfluxMeterRegistry(config, clock);

    }
}

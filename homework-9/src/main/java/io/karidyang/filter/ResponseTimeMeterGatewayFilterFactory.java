package io.karidyang.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-13
 * @since TODO
 */
public class ResponseTimeMeterGatewayFilterFactory extends AbstractGatewayFilterFactory<ResponseTimeMeterConfig> implements  MeterBinder {
    private MeterRegistry meterRegistry;

    @Override
    public GatewayFilter apply(ResponseTimeMeterConfig config) {
        return new ResponseTimeMeterGatewayFilter(config, meterRegistry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.meterRegistry = registry;
    }

}

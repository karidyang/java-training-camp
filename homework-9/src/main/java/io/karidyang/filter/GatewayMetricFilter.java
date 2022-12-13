package io.karidyang.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-13
 * @since TODO
 */
public class GatewayMetricFilter implements GlobalFilter, MeterBinder {
    private MeterRegistry meterRegistry;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();

        Mono<Void> mono = chain.filter(exchange);
        long responseTime = System.currentTimeMillis() - start;

        Timer timer = Timer.builder(buildMeterName(exchange.getRequest().getURI().getPath(), "responseTime")).register(meterRegistry);
        timer.record(responseTime, TimeUnit.MILLISECONDS);

        return mono;
    }

    private String buildMeterName(String path, String type) {
        return "gateway." + path.replaceAll("/", "_") + "." + type + ".value";
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.meterRegistry = registry;
    }
}

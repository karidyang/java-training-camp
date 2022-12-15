package io.karidyang.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
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
public class ResponseTimeMeterGatewayFilter implements GatewayFilter {
    private final MeterRegistry meterRegistry;
    private final ResponseTimeMeterConfig config;

    public ResponseTimeMeterGatewayFilter(ResponseTimeMeterConfig config, MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.config = config;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        System.out.println(path);
        if (!matchUrl(path)) {
            return Mono.empty();
        }

        long start = System.currentTimeMillis();

        Mono<Void> mono = chain.filter(exchange);
        long responseTime = System.currentTimeMillis() - start;

        Timer timer = Timer.builder(buildMeterName(path, "responseTime"))
                .tag("host", exchange.getRequest().getURI().getHost())
                .register(meterRegistry);
        timer.record(responseTime, TimeUnit.MILLISECONDS);

        return mono;
    }

    private String buildMeterName(String path, String type) {
        return "gateway." + path.replaceAll("/", "_") + "." + type + ".value";
    }

    public boolean matchUrl(String path) {
        return config.getMonitorUrl().contains(path);
    }
}

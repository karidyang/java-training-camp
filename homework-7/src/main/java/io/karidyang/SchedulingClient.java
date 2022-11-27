package io.karidyang;

import cn.hutool.core.util.RandomUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-11-27
 * @since TODO
 */
@Component
@Slf4j
class SchedulingClient implements MeterBinder {

    private MeterRegistry meterRegistry;
    SchedulingClient() {
    }

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 3, initialDelay = 10)
    public void run() throws Exception {
        long start = System.currentTimeMillis();
        Counter counter = Counter.builder("request.pv").tag("url", "/demo").register(meterRegistry);
        counter.increment(RandomUtil.randomDouble(0, RoundingMode.HALF_EVEN));
        log.info("pv...{}", counter.count());
        Gauge.builder("request.rate", RandomUtil::randomDouble).register(meterRegistry);
        Thread.sleep(RandomUtil.randomLong(10000));
        long costTime = System.currentTimeMillis() - start;
        Timer timer = Timer.builder("request.time").register(meterRegistry);
        timer.record(costTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.meterRegistry = registry;

    }
}

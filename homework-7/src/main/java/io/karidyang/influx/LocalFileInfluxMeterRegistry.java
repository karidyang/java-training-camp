package io.karidyang.influx;

import cn.hutool.core.io.FileUtil;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.instrument.util.MeterPartition;
import io.micrometer.core.instrument.util.StringUtils;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * 本地文件的Influx注册
 *
 * @author karidyang
 * @date 2022-11-29
 * @since 1.0
 **/
public class LocalFileInfluxMeterRegistry extends InfluxMeterRegistry {

    private LocalFileInfluxProperties properties;

    private LocalFileInfluxConfig config;
    private boolean initialized = false;

    private File dataFile;
    private File indexFile;


    public LocalFileInfluxMeterRegistry(LocalFileInfluxConfig config, Clock clock) {
        super(config, clock);
        this.config = config;
        initialDataFile();
        initialIndexFile();
        this.initialized = true;
    }


    private void initialDataFile() {
        this.dataFile = initialFile(this.properties.getDataFile());
    }
    private void initialIndexFile() {
        this.indexFile = initialFile(this.properties.getIndexFile());
    }

    private File initialFile(String file) {
        try {
            final ClassPathResource classPathResource = new ClassPathResource(file);
            if (!classPathResource.exists()) {
                final String path = classPathResource.getFile().getAbsolutePath();
                Files.createFile(Path.of(path));
            }
            return classPathResource.getFile();
        } catch (IOException e) {
            throw new RuntimeException("初始化文件" + file + "失败", e);
        }
    }

    @Override
    protected void publish() {
        if (!initialized) {
            super.publish();
        }

        doPublish();
    }

    private void doPublish() {
        List<String> lines = new ArrayList<>();
        for (List<Meter> batch : MeterPartition.partition(this, config.batchSize())) {
            final String line = batch.stream()
                    .flatMap(m -> m.match(
                            gauge -> writeGauge(gauge.getId(), gauge.value()),
                            counter -> writeCounter(counter.getId(), counter.count()),
                            this::writeTimer,
                            this::writeSummary,
                            this::writeLongTaskTimer,
                            gauge -> writeGauge(gauge.getId(), gauge.value(getBaseTimeUnit())),
                            counter -> writeCounter(counter.getId(), counter.count()),
                            this::writeFunctionTimer,
                            this::writeMeter))
                    .collect(joining("\n"));
            lines.add(line);
        }
        FileUtil.appendLines(lines, this.dataFile, Charset.defaultCharset());
    }

    Stream<String> writeMeter(Meter m) {
        List<Field> fields = new ArrayList<>();
        for (Measurement measurement : m.measure()) {
            double value = measurement.getValue();
            if (!Double.isFinite(value)) {
                continue;
            }
            String fieldKey = measurement.getStatistic().getTagValueRepresentation()
                    .replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            fields.add(new Field(fieldKey, value));
        }
        if (fields.isEmpty()) {
            return Stream.empty();
        }
        Meter.Id id = m.getId();
        return Stream.of(influxLineProtocol(id, id.getType().name().toLowerCase(), fields.stream()));
    }

    private Stream<String> writeLongTaskTimer(LongTaskTimer timer) {
        Stream<Field> fields = Stream.of(new Field("active_tasks", timer.activeTasks()),
                new Field("duration", timer.duration(getBaseTimeUnit())));
        return Stream.of(influxLineProtocol(timer.getId(), "long_task_timer", fields));
    }

    // VisibleForTesting
    Stream<String> writeCounter(Meter.Id id, double count) {
        if (Double.isFinite(count)) {
            return Stream.of(influxLineProtocol(id, "counter", Stream.of(new Field("value", count))));
        }
        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeGauge(Meter.Id id, Double value) {
        if (Double.isFinite(value)) {
            return Stream.of(influxLineProtocol(id, "gauge", Stream.of(new Field("value", value))));
        }
        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeFunctionTimer(FunctionTimer timer) {
        double sum = timer.totalTime(getBaseTimeUnit());
        if (Double.isFinite(sum)) {
            Stream.Builder<Field> builder = Stream.builder();
            builder.add(new Field("sum", sum));
            builder.add(new Field("count", timer.count()));
            double mean = timer.mean(getBaseTimeUnit());
            if (Double.isFinite(mean)) {
                builder.add(new Field("mean", mean));
            }
            return Stream.of(influxLineProtocol(timer.getId(), "histogram", builder.build()));
        }
        return Stream.empty();
    }

    private Stream<String> writeTimer(Timer timer) {
        final Stream<Field> fields = Stream.of(new Field("sum", timer.totalTime(getBaseTimeUnit())),
                new Field("count", timer.count()), new Field("mean", timer.mean(getBaseTimeUnit())),
                new Field("upper", timer.max(getBaseTimeUnit())));

        return Stream.of(influxLineProtocol(timer.getId(), "histogram", fields));
    }

    private Stream<String> writeSummary(DistributionSummary summary) {
        final Stream<Field> fields = Stream.of(new Field("sum", summary.totalAmount()),
                new Field("count", summary.count()), new Field("mean", summary.mean()),
                new Field("upper", summary.max()));

        return Stream.of(influxLineProtocol(summary.getId(), "histogram", fields));
    }

    private String influxLineProtocol(Meter.Id id, String metricType, Stream<Field> fields) {
        String tags = getConventionTags(id).stream().filter(t -> StringUtils.isNotBlank(t.getValue()))
                .map(t -> "," + t.getKey() + "=" + t.getValue()).collect(joining(""));

        return getConventionName(id) + tags + ",metric_type=" + metricType + " "
                + fields.map(Field::toString).collect(joining(",")) + " " + clock.wallTime();
    }

    static class Field {

        final String key;

        final double value;

        Field(String key, double value) {
            // `time` cannot be a field key or tag key
            if (key.equals("time")) {
                throw new IllegalArgumentException("'time' is an invalid field key in InfluxDB");
            }
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + DoubleFormat.decimalOrNan(value);
        }

    }
}

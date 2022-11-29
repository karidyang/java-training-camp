package io.karidyang.influx;

import io.micrometer.influx.InfluxApiVersion;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxConsistency;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryPropertiesConfigAdapter;

/**
 * 本地文件的Influx配置
 *
 * @author karidyang
 * @date 2022-11-29
 * @since 1.0
 **/
public class LocalFileInfluxConfig extends StepRegistryPropertiesConfigAdapter<LocalFileInfluxProperties> implements InfluxConfig {

    private final LocalFileInfluxProperties properties;

    LocalFileInfluxConfig(LocalFileInfluxProperties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    public String prefix() {
        return "management.metrics.export.influx";
    }

    @Override
    public String db() {
        return get(InfluxProperties::getDb, InfluxConfig.super::db);
    }

    @Override
    public InfluxConsistency consistency() {
        return get(InfluxProperties::getConsistency, InfluxConfig.super::consistency);
    }

    @Override
    public String userName() {
        return get(InfluxProperties::getUserName, InfluxConfig.super::userName);
    }

    @Override
    public String password() {
        return get(InfluxProperties::getPassword, InfluxConfig.super::password);
    }

    @Override
    public String retentionPolicy() {
        return get(InfluxProperties::getRetentionPolicy, InfluxConfig.super::retentionPolicy);
    }

    @Override
    public Integer retentionReplicationFactor() {
        return get(InfluxProperties::getRetentionReplicationFactor, InfluxConfig.super::retentionReplicationFactor);
    }

    @Override
    public String retentionDuration() {
        return get(InfluxProperties::getRetentionDuration, InfluxConfig.super::retentionDuration);
    }

    @Override
    public String retentionShardDuration() {
        return get(InfluxProperties::getRetentionShardDuration, InfluxConfig.super::retentionShardDuration);
    }

    @Override
    public String uri() {
        return get(InfluxProperties::getUri, InfluxConfig.super::uri);
    }

    @Override
    public boolean compressed() {
        return get(InfluxProperties::isCompressed, InfluxConfig.super::compressed);
    }

    @Override
    public boolean autoCreateDb() {
        return get(InfluxProperties::isAutoCreateDb, InfluxConfig.super::autoCreateDb);
    }

    @Override
    public InfluxApiVersion apiVersion() {
        return get(InfluxProperties::getApiVersion, InfluxConfig.super::apiVersion);
    }

    @Override
    public String org() {
        return get(InfluxProperties::getOrg, InfluxConfig.super::org);
    }

    @Override
    public String bucket() {
        return get(InfluxProperties::getBucket, InfluxConfig.super::bucket);
    }

    @Override
    public String token() {
        return get(InfluxProperties::getToken, InfluxConfig.super::token);
    }

    public String dataFile() {
        return this.properties.getDataFile();
    }

    public String indexFile() {
        return this.properties.getIndexFile();
    }

}

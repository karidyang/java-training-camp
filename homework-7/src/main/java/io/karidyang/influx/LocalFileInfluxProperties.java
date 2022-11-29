package io.karidyang.influx;

import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支持本地文件存储Influx数据的配置
 *
 * @author karidyang
 * @date 2022-11-29
 * @since 1.0
 **/
@ConfigurationProperties(prefix = "management.metrics.export.influx")
public class LocalFileInfluxProperties extends InfluxProperties {

    private String dataFile;
    private String indexFile;

    public LocalFileInfluxProperties() {
        super();
        this.dataFile = "influx.data";
        this.indexFile = "influx.index";
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getIndexFile() {
        return indexFile;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }
}

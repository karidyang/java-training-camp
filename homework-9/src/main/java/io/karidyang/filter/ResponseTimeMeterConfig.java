package io.karidyang.filter;

import java.util.List;

/**
 * TODO COMMENT
 *
 * @author karidyang
 * @date 2022-12-15
 * @since TODO
 */
public class ResponseTimeMeterConfig {

    public List<String> monitorUrl;

    public List<String> getMonitorUrl() {
        return monitorUrl;
    }

    public void setMonitorUrl(List<String> monitorUrl) {
        this.monitorUrl = monitorUrl;
    }
}

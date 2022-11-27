## 要求
### 1. 创建自定义的 Metrics
Metrics 名称（自由发挥）
必须 Tags：
- 应用
- 服务实例 ID
- 应用环境：TEST、STAGING、PROD

可选 Tags 自己决定

### 2. 将 Metrics 导出到 InfluxDB
搭建 InfluxDB 环境
创建 InfluxDB 数据库
通过 Spring Boot Influx Client 导出

### 3. 将 Grafana 添加 InfluxDB 数据源

### 4. 在 Grafana 展示自定义 Metrics
1. 熟悉 InfluxDB SQL 语法
2. 理解不同 Metrics 类型在图形化界面的展示
3. 导出模板 JSON 文件

### 5. 高阶：可选实现
替代 Spring Boot InfluxMetricsExportAutoConfiguration 实现，提供断点续传的能力的 InfluxDB 的推送，具体而言：

- 将 Metrics 导出到以本地文件的 MeterRegistry 实现（类似于日志实现）
- 独立 Metrics 上报进程
    - 将本地文件 Metrics 数据同步到 InfluxDB（可参考独立进程）
    - 实现调度任务，可记录上次成功提交的数据的偏移量（类似 Kafka Offset）
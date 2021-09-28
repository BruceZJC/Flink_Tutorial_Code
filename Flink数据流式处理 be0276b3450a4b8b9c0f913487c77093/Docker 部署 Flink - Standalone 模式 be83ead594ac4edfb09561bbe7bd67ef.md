# Docker 部署 Flink - Standalone 模式

[Docker](https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/deployment/resource-providers/standalone/docker/)

取自Flink官网

To deploy a Flink Session cluster with Docker, you need to start a JobManager container.

To enable communication between the containers, we first set a required Flink configuration property and create a network:

```bash
$FLINK_PROPERTIES="jobmanager.rpc.address: jobmanager` #使用`或\在powershell或shell中换行
taskmanager.numberOfTaskSlots: 3" 
docker network create flink-network
```

taskmanager容器的参数要在创建的时候通过设置环境变量“env” 的方式来设定好，后续好像无法再修改

```docker
docker run \
--rm \ #这个参数使这个容器停止的时候会自动删除，是可选项 
--name jobmanager \
--publish 8081:8081 \
--env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
flink:latest jobmanager
```

网页界面的地址就在 [localhost](http://localhost)：8081

之后启动 一个 或 多个 TaskManager 容器（container）

```docker
docker run \
--rm \
--name=taskmanager \
--network flink-network\ 
--env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
flink:latest taskmanager
```

taskmanager容器会自动与jobmanager连接

**在默认情况下taskmanager的standard output会显示在 Docker容器的 Log 里，而不能再Web UI中查看
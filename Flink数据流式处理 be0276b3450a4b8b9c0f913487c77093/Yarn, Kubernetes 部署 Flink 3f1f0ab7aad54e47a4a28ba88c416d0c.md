# Yarn, Kubernetes 部署 Flink

## 在 Yarn 部署 Flink

在 Yarn 部署 Flink任务时，要求 Flink 有 Hadoop 支持。（支持的Jar包放在lib目录下）Hadoop环境需要保证版本在 2.2 以上，并且集群中安装有 HDFS 服务。

### Flink On Yarn的模式

Flink 提供了两种在 yarn 上运行的模式，分为 Session-Cluster 和 Per-Job-Cluster 模式

![Untitled](Yarn,%20Kubernetes%20%E9%83%A8%E7%BD%B2%20Flink%203f1f0ab7aad54e47a4a28ba88c416d0c/Untitled.png)

Session 模式需要先启动集群，然后再提交作业。预先在 Yarn 中起一个会话，预定一些资源，之后提交的 Job 都运行在这个会话内部。

如果资源满了下一个 Job 则无法提交，需要等到其他 Job(s) 完成，释放资源后下一个 Job 才会提交。所有 Job 共享 Dispatcher 和 ResourceManager，适合小规模，执行时间短的作业。 

1. 启动 Hadoop 集群
2. 启动 Yarn session 

```bash
./bin/yarn-session.sh -n 2 -s 2 -jm 1024 -tm 1024 -nm test -d
```

- -n Taskmanager 的数量（不再使用，转为使用自动分配）
- -s 每一个 Taskmanager 的 slot 数量，默认一个 slot 一个 core
- -jm Jobmanager 的内存（MB）
- -tm 每一个 Taskmanager 的内存（MB）
- -nm Yarn 的 appName（现在 Yarn UI 上的名字）
- -d 后台模式
1. 提交任务和 standalone 模式一样
2. 取消 Yarn-session

```bash
yarn application --kill XXXXXXXXXXXXXXXXXXX
```

![Untitled](Yarn,%20Kubernetes%20%E9%83%A8%E7%BD%B2%20Flink%203f1f0ab7aad54e47a4a28ba88c416d0c/Untitled%201.png)

Per-Job 模式每次提交都会创建一个新的 Flink 集群，任务之间互相独立，互不影响，方便管理，Job执行完之后创建的集群也会消失。

每一个 Job 会向 Yarn 申请资源，独享 Dispatcher 和 ResourceManager。适合规模大长时间运行的作业

1. 不启动 yarn -session，直接执行 Job

```bash
./bin/flink run -m yarn-cluster -c xxxxxxxxxx  Jar包Path --参数 --参数
```

![Untitled](Yarn,%20Kubernetes%20%E9%83%A8%E7%BD%B2%20Flink%203f1f0ab7aad54e47a4a28ba88c416d0c/Untitled%202.png)

- 2 为提交给 Yarn 的资源管理器
- 以上的流程图为 Per-Job Cluster 模式

## Kubernetes 部署 Flink

1. 搭建 Kubernetes 集群
2. 分别配置 Jobmanager，Taskmanager，JobmanagerService 三个镜像的 yaml 文件
3. 启动 Flink Session Cluster 

```bash
kubectl create -f jobmanager-service.yaml

kubectl create -f jobmanager-deployment.yaml

kubectl create -f taskmanager-deployment.yaml
```

1. 集群启动后，就可以通过JobManagerServicers中配置的WebUI端口，用浏览器输入以下url来访问Flink UI页面了：

http://{JobManagerHost:Port}/api/v1/namespaces/default/services/flink-jobmanager:ui/proxy

### Flink on Kubernetes 不支持 Per-Job Cluster 模式
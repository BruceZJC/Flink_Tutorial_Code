# Flink_Tuturial_Code
 Bilibili尚硅谷Java版Flink教程
# Flink数据流式处理

[Docker 部署 Flink - Standalone 模式](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Docker%20%E9%83%A8%E7%BD%B2%20Flink%20-%20Standalone%20%E6%A8%A1%E5%BC%8F%20be83ead594ac4edfb09561bbe7bd67ef.md)

[Yarn, Kubernetes 部署 Flink](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Yarn,%20Kubernetes%20%E9%83%A8%E7%BD%B2%20Flink%203f1f0ab7aad54e47a4a28ba88c416d0c.md)

## 什么是Flink

Apache Flink is a Framework and distributed processing engine for stateful computations over unbounded and bounded data streams

- 一切皆有流组成，实时数据是无界限的流，离线数据是有界限的流
- 分层API，顶层越抽象，表达含义越简明，使用越简单；底层越具体，表达能力越丰富，使用越灵活

## Flink 对比 Spark Streaming，流(stream) 和 微批(micro-batching)

- Spark Streaming 无法省去生成微批的过程，速度比真正的流处理要慢
- Spark Streaming 采用 RDD 模型，其 DStream 实际上也就是一组组小批数据 RDD 的集合；   Flink 基本的数据模型是数据流，以及事件（Event）序列
- Spark 是批计算，将 DAG（有向无环图） 划分为不同的 stage，一个完成之后才能计算下一个                                            Flink 是标准的流执行模式，一个事件在一个节点处理完后可以直接发往下一个节点进行处理

[独立笔记-DEMO-Flink实现 批/流 处理WordCount](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/%E7%8B%AC%E7%AB%8B%E7%AC%94%E8%AE%B0-DEMO-Flink%E5%AE%9E%E7%8E%B0%20%E6%89%B9%20%E6%B5%81%20%E5%A4%84%E7%90%86WordCount%20456a6ac7f942484a9844933908e27d20.md)

### 为了实现更好的数据类型管理，Flink支持简单的 Java 类 — POJO，规范类似 Java 的 Bean（私有参数，拥有空参构造器，拥有 getter 和 setter）。

## Flink 的一些配置信息

- taskmanager.numberOfTaskSlots：一个 TaskManager 可以有几个“工位”，有几个 slot 就代表可以同时运行几个线程
- parallelism.default：默认并行度，默认为 1；taskmanager.numberOfTaskSlots 定义的是最大的并行能力，而 parallelism.default 是实际运行时的默认并行能力（无其他配置的情况下）

## Flink 各个组成部分

### Jobmanager 作业管理器

- 控制一个应用程序（Job）执行的主进程，每一个应用程序（Job）会被不同的Jobmanager所控制执行
- Jobmanager 会先接收到要执行的应用程序，包括：作业图（JobGraph），逻辑数据流图（Logic Dataflow Graph）和打包了所有类和其他资源的 Jar 包
- Jobmanager 会把 JobGraph 转换成一个物理层面的数据流图—“执行图”（Execution Graph），包含了所有可以并发执行的任务
- 向资源管理器（Resourcemanager）请求执行任务必要的资源，也就是 Taskmanager 上的 Slots，一旦获得了足够的资源，就会将 Execution Graph 发到真正运行他们的 Taskmanager 上。
- 在运行过程中，Jobmanager 会负责所有需要中央协调的操作，比如 checkpoint 的协调

### Taskmanager 任务管理器

- Flink 中的工作进程，slot 的数量限制了 Taskmanager 能够执行的任务数量
- 启动后，Taskmanager 会向 Resourcemanager 注册它的 slots，收到其指令后便会提供 slots 给 Jobmanager 调用
- Taskmanager 之间可以交换数据

### Resourcemanager 资源管理器

- 负责管理 slots，插槽是最小的处理单元
- Flink 提供了不同的资源管理器，比如 Yarn，Mesos，Kubernetes，以及 standalone 部署

### Dispatcher 分发器

- 可以跨作业运行，为应用（Job）提交提供了 REST 接口
- 当一个应用被提交的时候，分发器就会启动并将应用移交给一个 Jobmanager
- 启动 WEB UI
- Dispatcher 不是必需的

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled.png)

具体的任务调度原理：

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%201.png)

### WebUI / 命令行提交job

一个Flink Job拥有以下参数:

- Entry Class，即程序的入口，指定入口类
- Program Arguements，程序启动参数
- Parallelism，设置此Job的并行度
- Savepoint Path，通过checkpoint机制为streaming job创建的快照

一个Job的并行度 应该 ≤ 集群的总 task slots 数量

```bash
./bin/start-cluster.sh 
#通过 ./bin/start-cluster/sh 来启动 Flink

./bin/flink run -c "入口类" -p "并行度" "要运行的jar包路径"  --arg1 --arg2 #启动参数
#提交任务

./bin/flink list -a
#显示所有的job，包括正在运行的和已经取消的
```

## Flink 任务调度原理

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%202.png)

- Flink 中每一个 Taskmanager 都是一个 JVM 进程，它可能会在独立的线程上执行一个或多个子任务
- 为了控制一个 Taskmanager 能接收多少个 task，Taskmanager 通过 task slots 来进行控制（至少一个）

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%203.png)

- 先后发生的子任务（如最左列）可以共享一个 slots。这样的结果是某一个 slot 会有完整的 作业pipline

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%204.png)

- 建议的 Slots 数量是 CPU 本机核心的数量

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%205.png)

**这里通过 Sink 任务写到同一个文件里

## Flink 程序数据流（Dataflow）

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%206.png)

- 所有 Flink 程序都是由三部分组成，Source，Transformation 和 Sink
- Transformation 利用各种算子进行处理加工，Sink 负责输出
- 大部分情况下，程序中的 Transformation 和 dataflow 中的算子（operator）一一对应

### 数据传输形式

- 一个程序中，不同的算子可能具有不同的并行度；算子之间的传输数据的形式可以是 one-to-one 的模式也可以是 redistributing 的模式
- one-to-one stream维护着分区以及元素的顺序，这意味着 map 算子的子任务看到的元素的个数以及顺序跟 source 算子的子任务生产的元素的个数，顺序相同。map，filter，flatMap 等算子都是 one-to-one的对应关系
- Redistribution stream的分区会发生改变。例如 keyBy 基于 hashCode 重新分区，而broadCast 和 rebalance 会随机重新分区，这些算子都会引起 redistribute 的过程，而 redistribute 的过程就类似于 Spark 的 shuffle 过程

### 任务链 Operator Chains

- Flink 采用了一种称之为任务链的优化技术，可以在特定条件下减少本地通信的开销。为了满足任务链的要求，“必须将两个或多个算子设为相同的并行度，并通过本地转发的方式进行连接”
- 对于并行度相同的 one-to-one操作，Flink将这样的

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%207.png)

```java
算子.disableChaining( );
```

- 在代码中使用  可以强制使得算子不合并成 operator chaining（保持在第二行的状态）

```java
算子.startNewChain();
```

- 开始一个新的任务链合并，前后分别形成 operator chain

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.disableOperatorChaining();
```

- 在当前执行环境进行全局设置不进行算子合并

[独立笔记 - Flink 流处理 API](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/%E7%8B%AC%E7%AB%8B%E7%AC%94%E8%AE%B0%20-%20Flink%20%E6%B5%81%E5%A4%84%E7%90%86%20API%20903b78e1da6e473896b6f14d497365f9.md)

![Untitled](Flink%E6%95%B0%E6%8D%AE%E6%B5%81%E5%BC%8F%E5%A4%84%E7%90%86%20be0276b3450a4b8b9c0f913487c77093/Untitled%208.png)

## Flink支持的数据类型

- Java 和 Scala 的基础数据类型
- Java 和 Scala 元组 （Tuples）
- Scala 样例类 （case class）
- Java 简单对象（POJO，类似 Bean 但是有不同）
- Java ArrayList, HashMap, Enum 等

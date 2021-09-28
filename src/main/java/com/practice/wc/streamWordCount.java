package com.practice.wc;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class streamWordCount {
    public static void main(String[] args) throws Exception{
        //创建流处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//      env.setParallelism(1);
//        //从文件中读取数据
//        String inputPath = "C:\\Users\\86173\\IdeaProjects\\practice\\src\\main\\resources\\hello.txt";
//        DataStream<String> inputDataStream = env.readTextFile(inputPath);

        //用 Parameter tool 工具从程序启动参数中提取配置项
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String host = parameterTool.get("host");
        int port = parameterTool.getInt("port");

        //从 Socket 文本流读取数据
        DataStream<String> inputDataStream = env.socketTextStream(host, port);


        //基于数据流进行转换计算
        DataStream<Tuple2<String, Integer>> resultStream = inputDataStream.flatMap(new wordCount.myFlatMapper())
                .keyBy( new myKeySelector() )
                .sum(1);

        System.out.println(resultStream.print());

        //执行任务
        //这里env.execute();之前的代码，可以理解为是在定义任务，
        // 只有执行env.execute()后，Flink才把前面的代码片段当作一个任务整体（每个线程根据这个任务操作，并行处理流数据）。
        env.execute();
    }

    public static class myKeySelector implements KeySelector<Tuple2<String,Integer>, String>{

        @Override
        public String getKey(Tuple2<String, Integer> value) throws Exception {
            return value.f0.toString();
        }
    }
}


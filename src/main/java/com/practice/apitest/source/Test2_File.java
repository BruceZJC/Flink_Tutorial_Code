package com.practice.apitest.source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Test2_File {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //从文件读取数据
        DataStream<String> dataStream = env.readTextFile("C:\\Users\\86173\\IdeaProjects\\practice\\src\\main\\resources\\sensor.txt");
        //打印输出
        dataStream.print();
        env.execute();

    }
}

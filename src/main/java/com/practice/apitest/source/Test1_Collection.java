package com.practice.apitest.source;

import com.practice.apitest.beans.SensorReading;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

public class Test1_Collection {
    public static void main(String[] args) throws Exception{
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //从集合中读取数据
        DataStream<SensorReading> dataStream = env.fromCollection(
                Arrays.asList(
                        new SensorReading("sensor_1", 153411411L, 15.1),
                        new SensorReading("sensor_2", 153411572L, 15.2),
                        new SensorReading("sensor_3", 153418123L, 15.3),
                        new SensorReading("sensor_4", 150938434L, 15.4)
                )
        );
        DataStream<Integer> integerDataStreamSource = env.fromElements(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        //打印输出，
        dataStream.print("Data");
        integerDataStreamSource.print("int");

        env.execute("Test1_Collection");


    }
}

package com.practice.apitest.tansform;

import com.practice.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import javax.xml.crypto.Data;

public class TransformTest2_Aggregation {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        String inputPath = "C:\\Users\\86173\\IdeaProjects\\practice\\src\\main\\resources\\sensor.txt";
        DataStream<String> inputStream = env.readTextFile(inputPath);
        //map，将String 转换成 SensorReading
//        DataStream<SensorReading> mapStream = inputStream.map(new MapFunction<String, SensorReading>() {
//            @Override
//            public SensorReading map(String value) throws Exception {
//                String[] fields = value.split(",");
//                return new SensorReading(fields[0] , Long.valueOf(fields[1]), Double.valueOf(fields[2]));
//            }
//        });
        //以上部分也可以用 Lambda表达式的方式写
        DataStream<SensorReading> dataStream = inputStream.map( line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], Long.valueOf(fields[1]), Double.valueOf(fields[2]));
        });

        //分组
        KeyedStream<SensorReading, String> keyedStream = dataStream.keyBy(SensorReading::getId);
        SingleOutputStreamOperator<SensorReading> resultStream = keyedStream.maxBy("temperature");
        //滚动聚合，取当前最大的温度值
        resultStream.print();
        env.execute();
    }
}

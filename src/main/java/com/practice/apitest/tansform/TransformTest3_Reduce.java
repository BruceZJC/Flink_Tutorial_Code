package com.practice.apitest.tansform;

import com.practice.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformTest3_Reduce {
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
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], Long.valueOf(fields[1]), Double.valueOf(fields[2]));
        });

        //分组
        KeyedStream<SensorReading, String> keyedStream = dataStream.keyBy(SensorReading::getId);

        //reduce 聚合， 取最大的温度值，以及最新的时间戳
        //value1 是之前聚合后的结果，value2 是最近需要reduce的数据
        keyedStream.reduce( (v1, v2) ->{
            return new SensorReading(v1.getId(), v2.getTimeStamp(), Math.max(v1.getTemperature(), v2.getTemperature()));
        });
//        keyedStream.reduce(new ReduceFunction<SensorReading>() {
//            @Override
//            public SensorReading reduce(SensorReading value1, SensorReading value2) throws Exception {
//                return new SensorReading(value1.getId(), value2.getTimeStamp(), Math.max(value1.getTemperature(), value2.getTemperature()));
//            }
//        });
        keyedStream.print();
        env.execute();

    }
}

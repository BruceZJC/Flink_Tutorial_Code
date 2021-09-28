package com.practice.apitest.tansform;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.protocol.types.Field;

public class TansformTest1_Base {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        String inputPath = "C:\\Users\\86173\\IdeaProjects\\practice\\src\\main\\resources\\sensor.txt";
        DataStream<String> inputStream = env.readTextFile(inputPath);
        //map，将String 转换成 其字符长度
        DataStream<Integer> mapStream = inputStream.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String value) throws Exception {
                return value.length();
            }
        });
        //flatmap,按逗号分字段
        DataStream<String> flatmapStream = inputStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] fields = value.split(",");
                for( String field: fields){
                    out.collect(field);
                }
            }
        });

        //filter, 筛选sensor_1开头的数据
        DataStream<String> filterStream = inputStream.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return value.startsWith("sensor_1");
            }
        });


        mapStream.print("map");
        flatmapStream.print("flatmap");
        filterStream.print("filter");
        env.execute();
    }
}

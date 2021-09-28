package com.practice.apitest.tansform;

import com.practice.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import scala.Tuple2;

import java.util.Map;

public class TransformTest5_RichFunction {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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

        DataStream<Tuple2<String, Integer>> resultStream = dataStream.map( new MyMapper());

        resultStream.print();

        env.execute();
    }

//    public static class MyMapper0 implements MapFunction<SensorReading, Tuple2<String, Integer>> {
//
//        @Override
//        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
//            return new Tuple2<>(value.getId());
//        }
//    }

    //实现自定义的 富 函数
    public static class MyMapper extends RichMapFunction<SensorReading, Tuple2<String, Integer>> {

        @Override
        public void close() throws Exception {
            //关闭连接和清空状态操作
            System.out.println("Close");
        }

        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {

            return new Tuple2<>(value.getId(), getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            //初始化工作，一般是定义状态或者和外部数据库连接
            System.out.println("Open");

        }


    }
}

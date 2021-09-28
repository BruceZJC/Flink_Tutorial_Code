package com.practice.apitest.source;

import com.practice.apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.HashMap;
import java.util.Random;

public class Test4_UDF {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<SensorReading> dataStream = env.addSource(new MySensorSource());

        dataStream.print();
        env.execute();
    }

    //实现自定义的 Source Function
    public static class MySensorSource implements SourceFunction<SensorReading>{
        //定义一个标志位，用来控制数据的产生
        private boolean running = true;

        @Override
        public void run(SourceContext<SensorReading> sourceContext) throws Exception {
            //定义一个随机数发生器
            Random random = new Random();

            //设置十个传感器的初始温度值
            HashMap<String, Double> sensorHashmap = new HashMap<>();
            for(int i =0; i<10 ; i++){
                sensorHashmap.put("sensor_"+(i+1), 50 + random.nextGaussian()*10);
            }

            while(running){
                for(String sensor_id:sensorHashmap.keySet()){
                    Double newTemp = sensorHashmap.get(sensor_id) + random.nextGaussian() * 0.6;
                    sensorHashmap.put(sensor_id, newTemp );
                    sourceContext.collect(new SensorReading(sensor_id, System.currentTimeMillis(),newTemp));
                }
                //过一秒钟更新一次
                Thread.sleep(1000L);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}

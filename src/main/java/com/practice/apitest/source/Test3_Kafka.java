package com.practice.apitest.source;

import com.practice.wc.streamWordCount;
import com.practice.wc.wordCount;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class Test3_Kafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","host.docker.internal:9092");
        //properties.setProperty("group.id","consumer-group");
        //properties.setProperty("auto.offset.rest","latest");

        DataStream<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("test",new SimpleStringSchema(),properties));
        DataStream<Tuple2<String, Integer>> resultStream = dataStream.flatMap(new wordCount.myFlatMapper())
                .keyBy( new streamWordCount.myKeySelector() )
                .sum(1);
        resultStream.print();
        env.execute();
    }
}

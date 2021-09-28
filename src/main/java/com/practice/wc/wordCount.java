package com.practice.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

// 批处理 word count
public class wordCount {

    public static void main(String[] args) throws Exception {
        // 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //从文件中读取数据
        String inputPath = "C:\\Users\\86173\\IdeaProjects\\practice\\src\\main\\resources\\hello.txt";
        DataSource<String> inputDataset = env.readTextFile(inputPath);

        //对数据集进行处理，按照空格分词展开，转换成（word，1）二元组进行统计
        DataSet<Tuple2<String, Integer>> resultSet = inputDataset.flatMap( new myFlatMapper() )
                .groupBy(0) //按照第一个位置的word分组
                .sum(1); //将第二个位置上的数据求和
        resultSet.print();
    }

    // 自定义类，实现的是 FlatMap Function 结果
    // 类似Hadoop的Mapper功能，生成 K，V 对儿
    public static class myFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>>{

        @Override
        public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
            // 按空格分词
            String[] words = s.split(" ");

            //遍历所有word，包成二元组输出
            for (String w : words){
                collector.collect(new Tuple2<>(w, 1));
            }
        }
    }
}

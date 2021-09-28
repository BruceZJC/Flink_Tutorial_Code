package com.practice.apitest.beans;

// 传感器温度度数的数据类型
public class SensorReading {
    //属性： ID，时间戳，温度值
    private String id;
    private Long timeStamp;
    private Double temperature;

    //Flink POJO类型的要求，要有空参的构造器
    public SensorReading(){

    }
    public SensorReading(String id, Long timeStamp, Double temperature) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "SensorReading{" +
                "id='" + id + '\'' +
                ", timeStamp=" + timeStamp +
                ", temperature=" + temperature +
                '}';
    }
}

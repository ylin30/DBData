package com.cloudmon.agent;

import java.util.HashMap;
import java.util.Map;

public class Metric {
    private String metric;
    private Map<String, String> tags = new HashMap();
    private long timestamp;
    private double value;

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(double value) {
        this.value = value;
    }


    public Metric(String metric, long timestamp, double value, Map<String,String> appendTags) {
        this.metric = metric;
        this.timestamp = timestamp/1000;
        this.tags.put("method", "http");
        this.tags.putAll(appendTags);
        this.value = value;

    }

    public Metric(){

    }

    public String getMetric() {
        return metric;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    @Override
    public String toString() {
        return this.metric+" "+ this.timestamp +" "+ this.value+" <"+this.tags +">";
    }
}
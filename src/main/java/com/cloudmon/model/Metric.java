package com.cloudmon.model;

import java.util.HashMap;
import java.util.Map;

public class Metric {
    private String metric;
    private Map<String,String> tags = new HashMap();
    private long timestamp;
    private String value;

    public Metric(String name, String value, long timeStamp, Map<String, String> tags) {
        this.metric = name;
        this.tags = tags;
        this.timestamp = timeStamp;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

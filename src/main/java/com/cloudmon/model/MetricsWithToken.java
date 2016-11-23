package com.cloudmon.model;

import java.util.List;


public class MetricsWithToken {
    private List<Metric> metrics;
    private String token;

    public MetricsWithToken(List<Metric> metrics, String token) {
        this.metrics = metrics;
        this.token = token;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

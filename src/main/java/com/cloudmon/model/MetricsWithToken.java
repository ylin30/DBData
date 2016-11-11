package com.cloudmon.model;

import java.util.List;


public class MetricsWithToken {
    private List<Metrics> metrics;
    private String token;

    public List<Metrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metrics> metrics) {
        this.metrics = metrics;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

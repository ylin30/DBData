package com.cloudmon.oneapm;

import java.util.Map;

/**
 * Created by hehaiyuan on 1/31/18.
 */
public class Point{
    Map<String, Object> data;
    Map<String, Object> time;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getTime() {
        return time;
    }

    public void setTime(Map<String, Object> time) {
        this.time = time;
    }
}


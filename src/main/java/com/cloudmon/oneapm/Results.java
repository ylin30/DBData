package com.cloudmon.oneapm;

import java.util.List;

/**
 * Created by hehaiyuan on 1/31/18.
 */
public class Results {
    String name;
    List<Point> Points;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Point> getPoints() {
        return Points;
    }

    public void setPoints(List<Point> points) {
        Points = points;
    }
}
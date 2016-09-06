package com.cloudmon;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String now() {
        //e.x: 2016-08-31T19%3A22%3A46.000Z
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
        return sdf.format(new Date()).replace("@","T").replace(":","%3A")+".000Z";
    }

    public static String beforTime(int second) {
        long time = System.currentTimeMillis() - 1000*second;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
        return sdf.format(new Date(time)).replace("@","T").replace(":","%3A")+".000Z";
    }
}

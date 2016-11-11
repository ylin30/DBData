package com.cloudmon;

import java.text.DateFormat;
import java.text.ParseException;
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

    public static long getDistanceTimeWithFormat(String str1, String str2, DateFormat format) {
        DateFormat df = format;
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                return -1;
            }
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sec;
    }

}

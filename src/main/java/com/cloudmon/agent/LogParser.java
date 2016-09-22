package com.cloudmon.agent;


import com.cloudmon.RequestUtil;
import com.cloudmon.TimeUtil;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class LogParser {
    private static final String FORMAT_DATE = "dd/MMM/yyyy:HH:mm:ss";
    private Map<String, Tag> action2Value = new HashMap<String, Tag>();
    private String host = "";
    private String time1 = "01/Aug/2016:00:00:00";

    public void processLog(String filename) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    //System.out.println("line:" + line);
                    // 10.1.16.183 - - [07/Sep/2016:09:24:43 +0800] "GET /dell/images/top_bg1.jpg HTTP/1.1" 304 0
                    if (line.contains("GET /") || line.contains("POST /") || line.contains("HEAD /")) {
                        String[] components = line.split(" ");
                        //System.out.println("Components:" + Arrays.toString(components));

                        while (getDistanceTime(time1, components[3].substring(1)) > 60) {
                            pushMetric();
                            time1 = plusTime(time1, 60);
                        }

                        String host = components[0];
                        //System.out.println("host:" + host);

                        String date = components[3].substring(1);
                        //System.out.println("date:" + date);
                        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
                        Date parsedDate = dateFormat.parse(date);
                        long ts = parsedDate.getTime();
                        System.out.println("date:" + parsedDate + " ts:" + ts);

                        String action = components[5].substring(1);
                        // System.out.println("action:" + action);

                        String path = components[6];
                        //System.out.println("path:" + path);

                        String respCode = components[8];
                        //System.out.println("respCode:" + respCode);

                        String respByteNum = components[9];
                        //System.out.println("respByteNum:" + respByteNum);


                        counter(action,host,respCode);
                      //  counter(host + "/" + action + "/" + respCode);
                    }
                } catch (Exception e) {
                    System.out.println("error parsing log entry. " + e.toString());
                }
            }
            pushMetric();
        }
    }

    public void pushMetric() throws ParseException, IOException, InterruptedException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
        List<Metric> metrics = new ArrayList<>();
        for (Map.Entry<String, Tag> entry : action2Value.entrySet()) {
            Map<String,String> tag = new HashMap<String,String>();
            tag.put("host",host);
            tag.put("client",entry.getValue().getHost());
            tag.put("code",entry.getValue().getCode());
            Metric m = new Metric(entry.getKey(),dateFormat.parse(time1).getTime(),entry.getValue().getValue(),tag);
            metrics.add(m);
            System.out.println(m);
        }
        if(metrics.size()>0){
            RequestUtil.writeToMetricServer(metrics);
        }
        action2Value.clear();
    }

    public static void main(String[] args) throws Exception {
        LogParser log = new LogParser();
        log.setHost(args[1]);
        for (File f : listFilesMatching(new File(args[0]), "acc\\w+\\.\\w+")) {
            log.processLog(f.getAbsolutePath());
            log.reset();
            System.out.println(f.getAbsoluteFile());
        }
    }


    public void counter(String metricName,String host, String respCode) {
        Tag t = new Tag();
        t.setHost(host);
        t.setCode(respCode);
        if (action2Value.containsKey(metricName)) {
            t = action2Value.get(metricName);
            t.setValue(t.getValue()+1);
            return;
        }
        t.setValue(1);
        action2Value.put(metricName, t);
    }


    public long getDistanceTime(String time1, String time2) {
        return TimeUtil.getDistanceTimeWithFormat(time1, time2, new SimpleDateFormat(FORMAT_DATE));
    }

    public String plusTime(String date, long sec) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
        return dateFormat.format(new Date(dateFormat.parse(date).getTime() + sec * 1000));
    }

    public static File[] listFilesMatching(File root, String regex) {
        if (!root.isDirectory()) {
            throw new IllegalArgumentException(root + " is no directory.");
        }
        final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
        return root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return p.matcher(file.getName()).matches();
            }
        });
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void reset(){
        time1 = "01/Aug/2016:00:00:00";
    }

    class Tag{
        private int value;
        private String host;
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }
}
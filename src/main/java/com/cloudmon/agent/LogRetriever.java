package com.cloudmon.agent;

import com.cloudmon.TimeUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogRetriever implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(LogRetriever.class);
    //history_dump
    private static final String PREFIXS_LOGURL = "http://10.1.130.101:12900/search/universal/relative?query=*&range=20&limit=999";
    //timeline
    private String PREFIXS_TIMELINE_LOGURL = "http://10.1.130.101:12900/search/universal/absolute?query=*&limit=999&from=%s&to=%s";

    @Override
    public void run() {
        try {
//            String q = queryOneAPMApi(PREFIXS_LOGURL);
//            String q = queryOneAPMApi(PREFIXS_TIMELINE_LOGURL, TimeUtil.beforTime(10),TimeUtil.now());
//            List<String> logs = parseJSON(q);
//            for(String log : logs){
//                logger.info(log);
//            }
            int timeCheckPoint = 0;
            while(timeCheckPoint < 60*60*24*5){
                int shift = timeCheckPoint + (60*10);

                String q = queryOneAPMApi(String.format(PREFIXS_TIMELINE_LOGURL, TimeUtil.beforTime(shift),TimeUtil.beforTime(timeCheckPoint)));
                List<LogInfo> logs = parseJSON(q);
                for(LogInfo log : logs){
                    //System.out.println("["+log.getTimestamp()+" "+log.getSource()+"] "+log.message);
                    logger.info("["+log.getTimestamp()+" "+log.getSource()+"] "+log.message);
                }
                timeCheckPoint += (60*10);
                Thread.sleep(1000);
            }

            while(true){
                try {
                    String q = queryOneAPMApi(String.format(PREFIXS_TIMELINE_LOGURL, TimeUtil.beforTime(60*300+10000),TimeUtil.now()));
                    List<LogInfo> logs = parseJSON(q);
                    for(LogInfo log : logs){
                        logger.info("["+log.getTimestamp()+" "+log.getSource()+"] "+log.message);
                    }

                    TimeUnit.MINUTES.sleep(10);
                } catch (Exception e){
                    TimeUnit.MINUTES.sleep(10);
                }
            }
        }  catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //while(true)
        //query
        //parse
        //write into log File
        //write into openTSDB
    }


    public String queryOneAPMApi(String logAccess) throws IOException {
        logger.debug("connect to ONEAPM server " + logAccess);
        BufferedReader in = null;
        try {
            URL url = new URL(logAccess);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization","Basic YWRtaW46bG9nNDEyMzA2");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            final String content = response.toString();
            System.out.println(content);

            return content;
            //return parseJSON(content,serverUrl);
        } catch(Exception e) {
            System.out.println(String.format("error: failed to query metric %s, exception %s", logAccess, e));
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ee) {
                    System.out.println("warning: failed to close BufferedReader. Exception " + ee);
                }
            }
        }
    }


    public List<LogInfo> parseJSON(String content){
        JSONObject obj = new JSONObject(content);
        List<LogInfo> logs = new ArrayList<LogInfo>();
        JSONArray resultArray = obj.getJSONArray("messages");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject message = resultArray.getJSONObject(i);
            String strMessage = message.getJSONObject("message").getString("message");
            String timestamp = message.getJSONObject("message").getString("timestamp");
            String source = message.getJSONObject("message").getString("source");

            logs.add(new LogInfo(timestamp,strMessage,source));
        }
        return logs;
    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.execute(new LogRetriever());
    }




    class LogInfo {
        private String timestamp;
        private String message;
        private String source;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
        public LogInfo(){

        }
        public LogInfo(String timestamp, String message, String source){
            this.timestamp = timestamp;
            this.message = message;
            this.source = source;
        }
    }
}

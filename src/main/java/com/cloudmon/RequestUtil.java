package com.cloudmon;

import com.cloudmon.agent.Metric;
import com.cloudmon.agent.Metrics;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
/**
 * Created by hehaiyuan on 9/7/16.
 */
public class RequestUtil {

    public static void writeToMetricServer(List<Metric> metrics) throws IOException, InterruptedException {
        if(metrics == null){
            return ;
        }
        OutputStream wr = null;
        InputStream is = null;
        try {
            //URL url = new URL("http://172.16.210.247:4242/api/put?details");
            URL url = new URL("http://10.1.130.46:4242/api/put");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            wr = connection.getOutputStream();
            Metrics m = new Metrics();
            m.setMetrics(metrics);
            m.setToken("bd4d77d0e1dc8317abce2f51b52e08c35e9572a10d7d055ebb635e1f159b384464e85f0f1fd16a526fca440ff88377c3d308");
            String payload = constructJson(m);
            System.out.println(payload);
            byte[] b = payload.getBytes("UTF-8");
            wr.write(b);
            wr.flush();

            try {
                is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                //System.out.println(response.toString());
            } catch(Exception e) {
                System.out.println(e);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } finally {
            if (wr != null) {
                wr.close();
            }
        }
    }



    public static String constructJson(Object o) throws JsonProcessingException {
        return JacksonUtil.toJson(o);
    }

    public static void writeToMetricServers(Metrics metrics) throws IOException, InterruptedException {
        if(metrics == null){
            return ;
        }
        OutputStream wr = null;
        InputStream is = null;
        try {
            //URL url = new URL("http://172.16.210.247:4242/api/put?details");
            URL url = new URL("http://10.1.130:4242/api/put?details");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            wr = connection.getOutputStream();
            String payload = constructJson(metrics);
            //System.out.println(payload);
            byte[] b = payload.getBytes("UTF-8");
            wr.write(b);
            wr.flush();
            try {
                is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                //System.out.println(response.toString());
            } catch(Exception e) {
                System.out.println(e);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } finally {
            if (wr != null) {
                wr.close();
            }
        }
    }
}

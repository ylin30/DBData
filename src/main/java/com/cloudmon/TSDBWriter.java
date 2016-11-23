package com.cloudmon;

import com.cloudmon.api.Wrapper;
import com.cloudmon.api.Writer;
import com.cloudmon.model.Metric;
import com.cloudmon.model.MetricsWithToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by hehaiyuan on 11/14/16.
 */
public class TSDBWriter implements Writer<Metric>{

    @Override
    public void write(List<Metric> metrics , Wrapper context) throws IOException {
        if(metrics == null){
            return ;
        }
        OutputStream wr = null;
        InputStream is = null;
        try {
            URL url = new URL(Config.METRICS_SERVER);
            System.out.println(Config.METRICS_SERVER);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            wr = connection.getOutputStream();
            String token = (String)context.getApplication().get("token");
            MetricsWithToken m = new MetricsWithToken(metrics, token);
            String payload = JacksonUtil.toJson(m);
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
                System.out.println(response.toString());
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

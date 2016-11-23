package com.cloudmon;

import com.cloudmon.api.Request;
import com.cloudmon.api.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hehaiyuan on 11/14/16.
 */
public class AIServerRequest implements Request {
    private final static Logger logger = LoggerFactory.getLogger(AIServerRequest.class);

    @Override
    public String query(Wrapper w) throws IOException{
        logger.info("connect to ONEAPM server " + w.getRequestUrl());
        BufferedReader in = null;
        try {
            URL url = new URL(w.getRequestUrl());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            logger.info("Sending 'GET' request to URL : " + url + " ,Response Code : " + responseCode);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch(Exception e) {
            logger.error("error: failed to query metric %s, exception %s", w.getRequestUrl(), e);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ee) {
                    logger.error("warning: failed to close BufferedReader. Exception " + ee);
                }
            }
        }
    }
}

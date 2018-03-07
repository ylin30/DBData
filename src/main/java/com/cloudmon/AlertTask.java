package com.cloudmon;

import com.cloudmon.oneapm.Alert;
import com.cloudmon.oneapm.ApmResp;
import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by hehaiyuan on 3/2/18.
 */
public class AlertTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(AlertTask.class);
    private String ONEAPM_SERVER;
    private String OPEN_ID = "9";
    private String APP_NAME;
    private String opentsdbUrl;
    private static final int SPAN_TIME = 240000;
    private static final int INTERVAL = 60000;
    private String alertdUrl;

    public AlertTask() {

    }

    public AlertTask(String ONEAPM_SERVER, String OPEN_ID, String APP_NAME, String opentsdbUrl, String alertdUrl) {
        this.ONEAPM_SERVER = ONEAPM_SERVER;
        this.OPEN_ID = OPEN_ID;
        this.APP_NAME = APP_NAME;
        this.opentsdbUrl = opentsdbUrl;
        this.alertdUrl = alertdUrl;
    }

    public String getAgentUrl() {
        return String.format("%s/alert?openid=%s&alertKeyType=agent&span_time=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME);
    }

    public String getKeytransaction() {
        return String.format("%s/alert?openid=%s&alertKeyType=keytransaction&span_time=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME);
    }

    @Override
    public void run() {
        List<Alert> alertA = this.getAlerts(this.getAgentUrl());
        List<Alert> alertK = this.getAlerts(this.getKeytransaction());
        List<Alert> alerts = new ArrayList<>();
        List<com.cloudmon.agent.Alert> cwizAlerts = new ArrayList<>();
        alerts.addAll(alertA);
        alerts.addAll(alertK);
        for(Alert a : alerts) {
            com.cloudmon.agent.Alert cwizAlert = new com.cloudmon.agent.Alert(a);
            cwizAlerts.add(cwizAlert);
        }
        sendAlertsToAlertd(cwizAlerts);
    }

    public List<Alert> getAlerts(String strUrl) {
        System.out.println("connect to ONEAPM server " + strUrl);
        BufferedReader in = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            logger.info("Response Code : " + responseCode);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            final String content = response.toString();
            logger.debug("\ncontent:" + content);
            JavaType type = JacksonUtil.getCollectionType(ApmResp.class, Alert.class);
            ApmResp<Alert> alerts = JacksonUtil.fromJson(content, type);
            return alerts.getResults();
        } catch (Exception e) {
            logger.error("error: failed to query metric {}, exception ", strUrl, e);
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ee) {
                    logger.error("warning: failed to close BufferedReader. Exception ", ee);
                }
            }
        }
    }

    public void sendAlertsToAlertd(List<com.cloudmon.agent.Alert> alerts) {
        if (alerts == null) {
            return;
        }
        OutputStream wr = null;
        try {
            String jsonStr = JacksonUtil.toJson(alerts);
            URL url = new URL(this.alertdUrl + "/alert/import");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            wr = connection.getOutputStream();
            byte[] b = jsonStr.getBytes("UTF-8");
            wr.write(b);
            wr.flush();
        } catch (IOException e) {
            logger.error("error: failed to send alerts size:{}, exception ", alerts.size(), e);
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getONEAPM_SERVER() {
        return ONEAPM_SERVER;
    }

    public void setONEAPM_SERVER(String ONEAPM_SERVER) {
        this.ONEAPM_SERVER = ONEAPM_SERVER;
    }

    public String getOPEN_ID() {
        return OPEN_ID;
    }

    public void setOPEN_ID(String OPEN_ID) {
        this.OPEN_ID = OPEN_ID;
    }

    public String getAPP_NAME() {
        return APP_NAME;
    }

    public void setAPP_NAME(String APP_NAME) {
        this.APP_NAME = APP_NAME;
    }

    public String getOpentsdbUrl() {
        return opentsdbUrl;
    }

    public void setOpentsdbUrl(String opentsdbUrl) {
        this.opentsdbUrl = opentsdbUrl;
    }

    public String getAlertdUrl() {
        return alertdUrl;
    }

    public void setAlertdUrl(String alertdUrl) {
        this.alertdUrl = alertdUrl;
    }
}

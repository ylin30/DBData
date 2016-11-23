package com.cloudmon;

import com.cloudmon.api.Wrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hehaiyuan on 11/17/16.
 */
public class RequestWrapper implements Wrapper {
    HashMap application;
    HashMap service;

    public RequestWrapper(Object application, Object service) {
        this.application = (HashMap) application;
        this.service = (HashMap) service;
    }

    @Override
    public String getRequestUrl() {
        String url = (String) application.get("url");
        String openId = (String) application.get("openId");
        String applicationName = (String) application.get("name");
        String spanTime = (String) application.get("spanTime");
        String interval = (String) application.get("interval");

        String uri = (String) service.get("uri");

        return String.format("%s%s?openid=%s&application_name=%s&span_time=%s&interval=%s", url, uri, openId, applicationName, spanTime, interval);

    }

    @Override
    public Map getApplication() {
        return application;
    }

    @Override
    public Map getService() {
        return service;
    }

    public String getFormatter() {
        return (String) service.get("formatter");
    }

}

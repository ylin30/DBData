package com.cloudmon.api;

import java.util.Map;

/**
 * Created by hehaiyuan on 11/17/16.
 */
public interface Wrapper {
    public String getRequestUrl();
    public Map getApplication();
    public Map getService();
}

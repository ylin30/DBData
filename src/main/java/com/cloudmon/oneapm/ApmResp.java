package com.cloudmon.oneapm;

import java.util.List;

/**
 * Created by hehaiyuan on 3/4/18.
 */
public class ApmResp<T> {
    String code;
    String msg;
    List<T> results;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}

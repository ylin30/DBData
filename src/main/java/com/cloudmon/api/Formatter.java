package com.cloudmon.api;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;

/**
 * Created by hehaiyuan on 10/27/16.
 */
public interface Formatter<T> {
    /**
     * @param resp get response form Request
     * @return write MetcisWithToken when use openTSDB to persist or write into log file  without formatter
     * @throws IOException
     */
    T format(JsonObject resp) throws IOException;
}

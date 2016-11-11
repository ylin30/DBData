package com.cloudmon.api;


import com.eclipsesource.json.JsonObject;

import java.io.IOException;

/**
 * Created by hehaiyuan on 10/27/16.
 */
public interface Request {
    JsonObject query() throws IOException;
}

package com.cloudmon.api;


import java.io.IOException;

/**
 * Created by hehaiyuan on 10/27/16.
 */
public interface Request {
    String query(Wrapper w) throws IOException;
}
package com.cloudmon.api;

import java.io.IOException;
import java.util.List;

/**
 * Created by hehaiyuan on 10/27/16.
 */
public interface Formatter{
    /**
     * @param resp get response form Request
     * @return write MetcisWithToken when use openTSDB to persist or write into log file  without formatter
     * @throws IOException
     */
    List format(String resp) throws IOException;
}

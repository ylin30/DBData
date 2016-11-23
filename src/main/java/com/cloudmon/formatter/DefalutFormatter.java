package com.cloudmon.formatter;

import com.cloudmon.api.Formatter;
import com.cloudmon.api.Wrapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by hehaiyuan on 11/20/16.
 */
public class DefalutFormatter implements Formatter {
    private Wrapper wrapper;
    public DefalutFormatter(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public List format(String resp) throws IOException {
        //nothing to do and write down the metric in log
        return null;
    }
}

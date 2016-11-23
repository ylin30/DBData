package com.cloudmon.api;

import java.io.IOException;
import java.util.List;


public interface Writer<T> {
    void write(List<T> M, Wrapper context) throws IOException;
}

package com.cloudmon.api;

import java.io.IOException;


public interface Writer<T> {
    void write(T M) throws IOException;
}

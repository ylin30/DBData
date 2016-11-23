package com.cloudmon;

import com.cloudmon.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimerTask;

public class AppInsightTask extends TimerTask{
    private final static Logger logger = LoggerFactory.getLogger(AppInsightTask.class);
    private Request request;
    private Writer writer;

    public AppInsightTask(Request request, Writer writer) {
        this.request = request;
        this.writer = writer;
    }

    @Override
    public void run() {
        logger.info("Starting to AppInsight Task.");
        for(Object application : Config.AI_APPLICATIONS_LIST){
            for(Object service : Config.AI_SERVICES_LIST){
                HashMap ser = (HashMap)service;

                RequestWrapper wrapper = new RequestWrapper(application, ser);
                try {
                    String req = request.query(wrapper);

                    //TODO filter for can't putting into server
                    Formatter formatter= (Formatter)Class.forName(wrapper.getFormatter()).getConstructor(Wrapper.class).newInstance(wrapper);
                    //TODO batch writer
                    writer.write(formatter.format(req), wrapper);
                } catch (IOException e) {
                    logger.error("Failed to send request ", e);
                } catch (ClassNotFoundException e) {
                    logger.error("Can't find Formatter in classpath , please get something ",e);
                } catch (ReflectiveOperationException e) {
                    logger.error("Can't get Formatter Class and new Instance it ",e);
                } catch (Exception e){
                    logger.error("Failed to read and send metrics ,url: {}", wrapper.getRequestUrl());
                    logger.error("ERROR to read or send ", e);
                }
            }
        }
        //这里可以循环所有的list .然后等待所有的被响应同时返回以后写入. 这样就做了一个简单的batch.但是可能有个问题就是. 有一些是不能存储的需要考虑这些的保存方式.
        //可以尝试不同的timetask 来处理不同的request 的结果.这样就不会出什么问题了. 比如这里 就直接返回  metrics  就等到所有的metrics 结束才发送

    }
}

package com.cloudmon.oneapm;

/**
 * Created by hehaiyuan on 3/2/18.
 */
public class Alert {
    String agent_name;
    String alert_Key_type;
    String alert_set_name;
    String application_name;
    String is_closed;
    String message_id;
    String message_info;
    String message_url;
    String notification_type;
    String start_time;
    String threshold_type;

    public String getAgent_name() {
        return agent_name;
    }

    public void setAgent_name(String agent_name) {
        this.agent_name = agent_name;
    }

    public String getAlert_Key_type() {
        return alert_Key_type;
    }

    public void setAlert_Key_type(String alert_Key_type) {
        this.alert_Key_type = alert_Key_type;
    }

    public String getAlert_set_name() {
        return alert_set_name;
    }

    public void setAlert_set_name(String alert_set_name) {
        this.alert_set_name = alert_set_name;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getIs_closed() {
        return is_closed;
    }

    public void setIs_closed(String is_closed) {
        this.is_closed = is_closed;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_info() {
        return message_info;
    }

    public void setMessage_info(String message_info) {
        this.message_info = message_info;
    }

    public String getMessage_url() {
        return message_url;
    }

    public void setMessage_url(String message_url) {
        this.message_url = message_url;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getThreshold_type() {
        return threshold_type;
    }

    public void setThreshold_type(String threshold_type) {
        this.threshold_type = threshold_type;
    }
}

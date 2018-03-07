package com.cloudmon.agent;

import java.util.HashMap;

/**
 * Created by hehaiyuan on 3/5/18.
 * "definition": {
     "id": "zabbix1",
     "name": "系统：内存使用过高",
     "description": "内存使用过高",
     "alertDetails": {
        "alertType": "ZABBIX_ALERT"
     },
     "creationTime": 1519631817466,
     "modificationTime": 1519631817466
    },
 */
public class AlertDefinition {
    String id;
    String name;
    String description;
    HashMap<String, String> alertDetails;
    String creationTime;
    String modificationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, String> getAlertDetails() {
        return alertDetails;
    }

    public void setAlertDetails(HashMap<String, String> alertDetails) {
        this.alertDetails = alertDetails;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }
}

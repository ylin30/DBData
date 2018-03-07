package com.cloudmon.agent;

/**
 * Created by hehaiyuan on 3/5/18.
 *
 * "status": {
     "alertId": "zabbix1",
     "monitoredEntity": "centos24",
     "level": "WARNING",1516325490
     "creationTime": 1519400508484,
     "levelChangedTime": 1519400508484,
     "triggeredValue": 83.69999694824219
    },
 */
public class AlertStatus {
    String alertId;
    String monitoredEntity;
    String level;
    String creationTime;
    String levelChangedTime;
    String triggeredValue;

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getMonitoredEntity() {
        return monitoredEntity;
    }

    public void setMonitoredEntity(String monitoredEntity) {
        this.monitoredEntity = monitoredEntity;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getLevelChangedTime() {
        return levelChangedTime;
    }

    public void setLevelChangedTime(String levelChangedTime) {
        this.levelChangedTime = levelChangedTime;
    }

    public String getTriggeredValue() {
        return triggeredValue;
    }

    public void setTriggeredValue(String triggeredValue) {
        this.triggeredValue = triggeredValue;
    }
}

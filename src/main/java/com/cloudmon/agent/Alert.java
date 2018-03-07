package com.cloudmon.agent;

/**
 * Created by hehaiyuan on 3/4/18.
 */

import java.util.HashMap;

/**
 * example
 * [
     {
     "definition": {
         "id": "zabbix1",
         "name": "系统：内存使用过高",
         "description": "内存使用过高",
         "alertDetails": {
            "alertType": "ZABBIX_ALERT"
         },
         "creationTime": 1519631817466,
         "modificationTime": 1519631817466
    },
    "status": {
         "alertId": "zabbix1",
         "monitoredEntity": "centos24",
         "level": "WARNING",1516325490
         "creationTime": 1519400508484,
         "levelChangedTime": 1519400508484,
         "triggeredValue": 83.69999694824219
     },
     "metric": "proc.meminfo.percentused"
     }
 ]
 */
public class Alert {
    public AlertDefinition definition;
    public AlertStatus status;
    public String metric;

    public AlertDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(AlertDefinition definition) {
        this.definition = definition;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Alert() {
    }

    public Alert(com.cloudmon.oneapm.Alert alert) {
        this.definition = new AlertDefinition();
        this.definition.setName(alert.getAgent_name());
        HashMap<String, String> oneapm_alert = new HashMap<String, String>();
        oneapm_alert.put("alertType", "ONEAPM_ALERT");
        this.definition.setAlertDetails(oneapm_alert);
        this.definition.setDescription(alert.getMessage_info());
        this.definition.setId(alert.getMessage_id());
        this.definition.setCreationTime(alert.getStart_time());
        this.definition.setModificationTime(alert.getStart_time());

        this.status.setAlertId(alert.getMessage_id());
        this.status.setCreationTime(alert.getStart_time());
        this.status.setLevel("WARNING");
        this.status.setLevelChangedTime(alert.getStart_time());
        this.status.setMonitoredEntity(alert.getApplication_name());
        this.status.setTriggeredValue("0");

    }
}

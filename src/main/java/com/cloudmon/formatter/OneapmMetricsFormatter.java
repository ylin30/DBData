package com.cloudmon.formatter;

import com.cloudmon.api.Formatter;
import com.cloudmon.api.Wrapper;
import com.cloudmon.model.Metric;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehaiyuan on 11/14/16.
 */
public class OneapmMetricsFormatter implements Formatter {
    private Wrapper wrapper;
    public OneapmMetricsFormatter(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public List<Metric> format(String response) throws IOException {
        JsonObject req = Json.parse(response.toString()).asObject();
        List<Metric> metricsList = new ArrayList();

        for (JsonValue result : req.get("results").asArray()) {
            JsonObject resultJsonObj = result.asObject();
            String name = resultJsonObj.getString("name", "default");
            for (JsonValue point : resultJsonObj.get("points").asArray()) {
                JsonObject pointJsonObj = point.asObject();
                Iterator<JsonObject.Member> it = pointJsonObj.get("data").asObject().iterator();
                JsonObject.Member m = it.next();
                String metricName = m.getName();
                String value = Double.toString(m.getValue().asDouble());

                long timeStamp = pointJsonObj.get("time").asObject().get("endTime").asLong() / 1000;
                HashMap<String, String> tag = new HashMap(4);
                tag.putAll((HashMap) wrapper.getService().get("tags"));
                String hostname = (String) wrapper.getApplication().get("name");
                tag.put("host",hostname);
                tag.put("name", name);

                Metric metric = new Metric(metricName, value, timeStamp, tag);
                metricsList.add(metric);
            }
        }
        return metricsList;
    }
}

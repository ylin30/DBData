package com.cloudmon;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private final static Logger logger = LoggerFactory.getLogger(Config.class);

    public static HashMap<String, Object> PROPERTIES = new HashMap<>();
    public static ArrayList AI_APPLICATIONS_LIST = new ArrayList();
    public static ArrayList AI_SERVICES_LIST = new ArrayList();
    public static int TIME_TASK_INTERVAL = 10; //second or minutes?
    public static String METRICS_SERVER = "";

    private YamlReader reader;


    public Config(final String configFile) throws YamlException, FileNotFoundException {
        reader = new YamlReader(new FileReader(configFile));
        PROPERTIES = (HashMap) reader.read();
        loadStaticVariables();
    }


    public void loadStaticVariables() {
        Map AInsight = (Map) PROPERTIES.get("AInsigth");
        AI_APPLICATIONS_LIST = (ArrayList) AInsight.get("applications");
        AI_SERVICES_LIST = (ArrayList) AInsight.get("services");
        TIME_TASK_INTERVAL = Integer.parseInt(AInsight.get("taskInterval").toString());
        METRICS_SERVER = (String) PROPERTIES.get("metricsServer");
    }

}

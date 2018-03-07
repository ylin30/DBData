import com.cloudmon.AlertTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

/**
 * Created by hehaiyuan on 2/8/18.
 */
public class ApmDaemon {
    private static final Logger logger = LoggerFactory.getLogger(ApmDaemon.class);

    public static void main(String[] args) throws IOException {
        InputStream is = ApmDaemon.class.getClassLoader().getResourceAsStream("DBData.conf");
        final Properties properties = new Properties();
        properties.load(is);
        String app_names = (String) properties.get("app_name");
        String open_id = (String) properties.get("open_id");
        String url = (String) properties.get("url");
        String opentsdb_url = (String) properties.get("opentsdb_url");
        String alertd_url = (String) properties.get("alert_url");
        String apps[] = app_names.split(",");
        int metric_interval = Integer.parseInt(properties.get("metric_interval").toString());
        int alert_interval = Integer.parseInt(properties.get("alert_interval").toString());
        try {
            for (String a : apps) {
                final Timer taskSchedulerScheduler = new Timer(a);
                DBDataRetriever retriever = new DBDataRetriever();
                retriever.setAPP_NAME(a);
                retriever.setONEAPM_SERVER(url);
                retriever.setOPEN_ID(open_id);
                retriever.setOpentsdbUrl(opentsdb_url);
//                retriever.setTimer(taskSchedulerScheduler);
                AlertTask alertdTask = new AlertTask(url, open_id, a, opentsdb_url, alertd_url);
                taskSchedulerScheduler.schedule(alertdTask, 0, alert_interval * 1000);
                taskSchedulerScheduler.schedule(retriever, 0, metric_interval * 1000);
            }
        } catch (Exception e) {
            logger.error("run in main function error", e);
        }
    }
}

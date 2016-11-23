import com.cloudmon.Config;
import com.cloudmon.AIServerRequest;
import com.cloudmon.AppInsightTask;
import com.cloudmon.TSDBWriter;
import com.esotericsoftware.yamlbeans.YamlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Timer;

public class Tides {
    private final static Logger logger = LoggerFactory.getLogger(Tides.class);

    public static void main(String[] args) {
        AIServerRequest server = new AIServerRequest();
        TSDBWriter tsdbWriter = new TSDBWriter();

        logger.info("Starting. ");
        // get config from command line

        try {
            new Config("/Users/hehaiyuan/workplace/dbdata/DBData/src/main/resources/application.yml");
            AppInsightTask aiTask = new AppInsightTask(server, tsdbWriter);
            final Timer timer = new Timer("Tides-scheduler");
            timer.schedule(aiTask, 0, Config.TIME_TASK_INTERVAL * 1000);

        } catch (YamlException e) {
            logger.error("Can't not read config file ", e);
        } catch (NullPointerException e){
            logger.error("Can't not get anything from Config file , probably file path was wrong", e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

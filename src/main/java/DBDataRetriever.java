import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.sun.webkit.perf.WCFontPerfLogger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.PooledConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;


public class DBDataRetriever {
    private final static Logger logger = LoggerFactory.getLogger(DBDataRetriever.class);
    private final BufferedWriter bufferedFileWriter;
    private final static String ONEAPM_SERVER = "http://10.1.130.102:8091/api/v2";
    private final static String OPEN_ID = "9";
    private final static String APP_NAME = "dztier";
    private final static int SPAN_TIME = 1800000;
    private final static int INTERVAL = 60000;
    private final static String END_TIME = "1472541600000";

    private final List<String> apis = new ArrayList<>();
    private final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        /*String query = "select COUNT(*) from PLAN_TRAIN t where t.ORG_CM_TRAIN_ID is not NULL and t.run_date= to_char(sysdate,'yyyymmdd')";
        queryOracle(query);

        long ts = new Date().getTime();
        final Metrics metrics = new Metrics();
        final Metric m1 = new Metric("m1", ts, 10.0);
        final Metric m2 = new Metric("m2", ts, 20.0);
        metrics.metrics.add(m1);
        metrics.metrics.add(m2);*/

        String filename = args.length > 0 ? args[0] : null;
        DBDataRetriever retriever = null;
        try {
            retriever = new DBDataRetriever(filename);
            retriever.run();
            //retriever.createMetrics();
        } catch(Exception e) {
            System.out.println(e);
        } finally {
            if (retriever != null) {
                retriever.close();
            }
        }
    }

    public DBDataRetriever(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            filename = "metrics.txt";
        }
        final File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileWriter fw = new FileWriter(f.getAbsoluteFile());
        this.bufferedFileWriter = new BufferedWriter(fw);

        //application
        this.apis.add(String.format("%s/applications/cpu?openid=%s&application_name=%s&span_time=%s&interval=%s&end_time=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL, END_TIME));
        this.apis.add(String.format("%s/applications/memory?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/applications/throughput?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/applications/errors?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/applications/apdex?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/applications/response%%20time?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));


        //datasotre
        this.apis.add(String.format("%s/transactions/Datastore/response%%20time?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/transactions/Datastore/throughput?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));


        //Web事务
        this.apis.add(String.format("%s/transactions/WebTransaction/throughput?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/transactions/WebTransaction/errors?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/transactions/WebTransaction/response%%20time?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/transactions/WebTransaction/apdex?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));

        //后台任务
        this.apis.add(String.format("%s/transactions/OtherTransaction/response%%20time?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        this.apis.add(String.format("%s/transactions/OtherTransaction/throughput?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
    }

    private void run() throws IOException, InterruptedException {
        while (true) {
            for (String serverUrl : this.apis) {
                Metrics metrics = queryOneAPMApi(serverUrl);
                //Metrics metrics = createOneAPMMetrics();
                writeToFile(metrics);
                //writeToMetricServer(metrics);

                Thread.sleep(1000);
            }
            Thread.sleep(15000000); // 25 minutes
            //Thread.sleep(5000); // 5 sec
            System.out.println("------------------");
        }
    }

    private void createMetrics() throws IOException,  InterruptedException{
        String[] metricNames = new String[] {"active_user_count", "Active_readers", "MQ_response_time"};
        int[] factors = new int[] {50, 20, 500};
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Asia/Beijing"));
        start.add(Calendar.HOUR, -24*4);
        int count = 24 * 4 * 60;
        while (count-- > 0) {
            Metrics metrics = new Metrics();
            for (int i = 0; i < metricNames.length; i++) {
                Metric m = new Metric(metricNames[i], start.getTime().getTime(), rand.nextDouble() * factors[i]);
                metrics.metrics.add(m);
            }
            start.add(Calendar.MINUTE, 1);
            writeToMetricServer(metrics);
            Thread.sleep((10));
        }

    }

    private Metrics createOneAPMMetricsRealtime() {
        String[] oneAPMMetrics = new String[] {"JSP.up.upload.jsp"};
        Metrics metrics = new Metrics();
        return metrics;
    }

    private Metrics createOneAPMMetrics() {
        String[] oneAPMMetrics = new String[] {"JSP.up.upload.jsp", "User.Utilization"};
        //String[] oneAPMMetrics = new String[] {"Spring.com.srie.cis.service.impl.WSServiceImpl.getDispatchCode", "JSP.up.upload.jsp", "JSP.esign.creatpng.jsp",
        //"Servlet.ESignServlet", "SpringController.cis.createSerAction.do", "JSP.Cc.createSer_005faction.jsp", "JSP.Cc.createSer_005faction.jsp", "JSP.ser.listSer.jsp",
        //"JSP.esign.sendEmail.jsp", "JSP.ser.ex_005freceiving.jsp", "Servlet.EsignShowServlet"};

        Metrics metrics = new Metrics();
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Asia/Beijing"));
        start.add(Calendar.MINUTE, -60);
        int count = 60;
        while (count-- > 0) {
            for (String metricName : oneAPMMetrics) {
                Metric m = new Metric(metricName, start.getTime().getTime(), rand.nextDouble() * 20);
                metrics.metrics.add(m);
            }
            start.add(Calendar.MINUTE, 1);
        }
        return metrics;
    }

    private Metrics queryOneAPMApi(String serverUrl) throws IOException {
        System.out.println("connect to ONEAPM server " + serverUrl);
        BufferedReader in = null;
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            final String content = response.toString();
            // System.out.println("\ncontent:" + content);

            return parseJSON(content);
        } catch(Exception e) {
            System.out.println(String.format("error: failed to query metric %s, exception %s", serverUrl, e));
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ee) {
                    System.out.println("warning: failed to close BufferedReader. Exception " + ee);
                }
            }
        }
    }

    private Metrics parseJSON(String content) {
        Metrics metrics = new Metrics();
        JSONObject obj = new JSONObject(content);
        int code = obj.getInt("code");
        String msg = obj.getString("msg");
        JSONArray results = obj.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject resultObj = results.getJSONObject(i);
            String metricName = resultObj.getString("name");
            metricName = metricName.replace('/', '.');
            JSONArray points = resultObj.getJSONArray("points");
            for (int j = 0; j < points.length(); j++) {
                JSONObject pointObj = points.getJSONObject(j);
                double val = rand.nextDouble() * 20; //pointObj.getJSONObject("data").getDouble("usage_rate");
                long ts = pointObj.getJSONObject("time").getLong("endTime");
                Metric m = new Metric(metricName, ts, val);
                metrics.metrics.add(m);
            }
        }
        return metrics;
    }

    private  void writeToFile(Metrics metrics) throws IOException {
        for (Metric metric : metrics.metrics) {
            final Iterator<Map.Entry<String, String>> it = metric.getTags().entrySet().iterator();
            final Map.Entry<String, String> tagEntry = it.next();
            //String line = String.format("%s,%d,%s=%s,%f%n", metric.getMetric(), metric.getTimestamp(), tagEntry.getKey(), tagEntry.getValue(), metric.getValue());
            String payload = constructJson(metric);

            //this.bufferedFileWriter.write(payload + "\n");
            // this.bufferedFileWriter.flush();
            logger.info(payload);
        }
    }

    private  void writeToMetricServer(Metrics metrics) throws IOException, InterruptedException {
        OutputStream wr = null;
        InputStream is = null;
        try {
            //URL url = new URL("http://172.16.210.247:4242/api/put?details");
            URL url = new URL("http://localhost:4242/api/put?details");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            wr = connection.getOutputStream();
            String payload = constructJson(metrics);
            //System.out.println(payload);
            byte[] b = payload.getBytes("UTF-8");
            wr.write(b);
            wr.flush();

            try {
                is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                //System.out.println(response.toString());
            } catch(Exception e) {
                System.out.println(e);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } finally {
            if (wr != null) {
                wr.close();
            }
        }
    }

    private void close() throws IOException {
        this.bufferedFileWriter.close();
    }

    private static String constructJson(Metrics metrics) throws JsonProcessingException {
        return JacksonUtil.toJson(metrics.metrics);
    }

    private static String constructJson(Metric metric) throws JsonProcessingException {
        return JacksonUtil.toJson(metric);
    }

    private static void queryOracle(String query) {
        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }
        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;
        Statement stmt = null;
        try {

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@10.1.132.118:1521:orcl", "jhpt_ky", "123456");
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
            }

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
    }

    private static void queryMysql() {
        System.out.println("test started");
        String dbUrl = "jdbc:mysql://localhost:3306/grafana";
        String user = "root";
        String password = "password";

        MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setUrl(dbUrl);
        ds.setUser(user);
        ds.setPassword(password);

        PooledConnection pcon;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            pcon = ds.getPooledConnection();
            con = pcon.getConnection();
            //stmt = con.createStatement();
            //rs = stmt.executeQuery("select empid, name from Employee");
            stmt = con.prepareStatement("select empid, name from Employee");
            rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println("Employee ID="+rs.getInt("empid")+", Name="+rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Metric {
        private final String metric;
        private final Map<String, String> tags = new HashMap();
        private final long timestamp;
        private double value;

        public Metric(String metric, long timestamp, double value) {
            this.metric = metric;
            this.timestamp = timestamp/1000;
            this.tags.put("service", "http");
            this.value = value;
        }

        public String getMetric() {
            return metric;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public double getValue() {
            return value;
        }

        public Map<String, String> getTags() {
            return this.tags;
        }
    }

    private static class Metrics {
        public final List<Metric> metrics = new ArrayList<>();
        //public final String token="7ef809091178e7a3093bde7cc6a83d0b5700cbeb11dc6fdaf8c8f18bee46118b2c385b65189fee4d1d5c2ee042df1566a0c0";
    }

    private static class Tuple {
        public final String first;
        public final String second;

        public Tuple(String f, String s) {
            this.first = f;
            this.second = s;
        }
    }
}


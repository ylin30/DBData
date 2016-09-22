import com.cloudmon.JacksonUtil;
import com.cloudmon.RequestWapper;
import com.cloudmon.agent.LogRetriever;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
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
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DBDataRetriever {
    private final static Logger logger = LoggerFactory.getLogger(DBDataRetriever.class);
    private final BufferedWriter bufferedFileWriter;
    private final static String ONEAPM_SERVER = "http://10.1.130.102:8091/api/v2";
    private final static String OPEN_ID = "9";
    private final static String APP_NAME = "dztier";
    private final static int SPAN_TIME = 240000;
    private final static int INTERVAL = 60000;

    private final Random rand = new Random();
    private final List<RequestWapper> apis = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String filename = args.length > 0 ? args[0] : null;
        DBDataRetriever retriever = null;
        try {
            ExecutorService es = Executors.newFixedThreadPool(2);
            es.execute(new LogRetriever());
            retriever = new DBDataRetriever(filename);
            retriever.run();
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
        RequestWapper req = new RequestWapper();
        req.setUrl(String.format("%s/applications?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req.setType("list");
        this.apis.add(req);

        RequestWapper req1 = new RequestWapper();
        req1.setUrl(String.format("%s/applications/cpu?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req1.setType("points");
        req1.setMetric("usage_rate");
        req1.setTags("service","applications");
        this.apis.add(req1);

        RequestWapper req2 = new RequestWapper();
        req2.setUrl(String.format("%s/applications/memory?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req2.setType("points");
        req2.setMetric("memory_rate");
        req2.setTags("service","applications");
        this.apis.add(req2);

        RequestWapper req3 = new RequestWapper();
        req3.setUrl(String.format("%s/applications/throughput?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req3.setType("points");
        req3.setMetric("throughput");
        req3.setTags("service","applications");
        this.apis.add(req3);

        RequestWapper req4 = new RequestWapper();
        req4.setUrl(String.format("%s/applications/errors?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req4.setType("points");
        req4.setMetric("error_rate");
        req4.setTags("service","applications");
        this.apis.add(req4);

        RequestWapper req5 = new RequestWapper();
        req5.setUrl(String.format("%s/applications/response%%20time?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req5.setType("points");
        req5.setMetric("avg_response_time");
        req5.setTags("service","applications");
        this.apis.add(req5);

        RequestWapper req6 = new RequestWapper();
        req6.setUrl(String.format("%s/applications/apdex?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req6.setType("points");
        req6.setMetric("apdex");
        req6.setTags("service","applications");
        this.apis.add(req6);

        //agent need agent_name
        RequestWapper req37 = new RequestWapper();
        req37.setUrl(String.format("%s/agents?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req37.setType("list");
        this.apis.add(req37);

        RequestWapper req38 = new RequestWapper();
        req38.setUrl(String.format("%s/agents/cpu?openid=%s&application_name=%s&span_time=%s&interval=%s&agent_name=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL,"java:dztier:8081(localhost.localdomain)"));
        req38.setType("points");
        req38.setMetric("usage_rate");
        req38.setTags("service","agent");
        this.apis.add(req38);

        RequestWapper req39 = new RequestWapper();
        req39.setUrl(String.format("%s/agents/memory?openid=%s&application_name=%s&span_time=%s&interval=%s&agent_name=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL,"java:dztier:8081(localhost.localdomain)"));
        req39.setType("points");
        req39.setMetric("memory_rate");
        req39.setTags("service","agent");
        this.apis.add(req39);

        RequestWapper req40 = new RequestWapper();
        req40.setUrl(String.format("%s/agents/throughput?openid=%s&application_name=%s&span_time=%s&interval=%s&agent_name=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL,"java:dztier:8081(localhost.localdomain)"));
        req40.setType("points");
        req40.setMetric("throughput");
        req40.setTags("service","agent");
        this.apis.add(req40);

        RequestWapper req41 = new RequestWapper();
        req41.setUrl(String.format("%s/agents/response%%20time?openid=%s&application_name=%s&span_time=%s&interval=%s&agent_name=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL,"java:dztier:8081(localhost.localdomain)"));
        req41.setType("points");
        req41.setMetric("avg_response_time");
        req41.setTags("service","agent");
        this.apis.add(req41);

        RequestWapper req42 = new RequestWapper();
        req42.setUrl(String.format("%s/agents/apdex?openid=%s&application_name=%s&span_time=%s&interval=%s&agent_name=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL,"java:dztier:8081(localhost.localdomain)"));
        req42.setType("points");
        req42.setMetric("apdex");
        req42.setTags("service","agent");
        this.apis.add(req42);

        RequestWapper req43 = new RequestWapper();
        req43.setUrl(String.format("%s/agents/errors?openid=%s&application_name=%s&span_time=%s&interval=%s&agent_name=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL,"java:dztier:8081(localhost.localdomain)"));
        req43.setType("points");
        req43.setMetric("error_rate");
        req43.setTags("service","agent");
        this.apis.add(req43);

        //datasotre
        RequestWapper req7 = new RequestWapper();
        req7.setUrl(String.format("%s/transactions/Datastore?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req7.setType("list");
        this.apis.add(req7);

        RequestWapper req8 = new RequestWapper();
        req8.setUrl(String.format("%s/transactions/Datastore/response%%20time?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req8.setType("points");
        req8.setMetric("avg_response_time");
        req8.setTags("service","datastore");
        this.apis.add(req8);

        RequestWapper req9 = new RequestWapper();
        req9.setUrl(String.format("%s/transactions/Datastore/throughput?openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req9.setType("points");
        req9.setMetric("throughput");
        req9.setTags("service","datastore");
        this.apis.add(req9);

        //Web事务
        RequestWapper req10 = new RequestWapper();
        req10.setUrl(String.format("%s/transactions/WebTransaction?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req10.setType("list");
        this.apis.add(req10);

        RequestWapper req11 = new RequestWapper();
        req11.setUrl(String.format("%s/transactions/WebTransaction/throughput?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req11.setType("points");
        req11.setMetric("throughput");
        req11.setTags("service","web_transaction");
        this.apis.add(req11);

        RequestWapper req12 = new RequestWapper();
        req12.setUrl(String.format("%s/transactions/WebTransaction/errors?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req12.setType("points");
        req12.setMetric("error_rate");
        req12.setTags("service","web_transaction");
        this.apis.add(req12);

        RequestWapper req13 = new RequestWapper();
        req13.setUrl(String.format("%s/transactions/WebTransaction/response%%20time?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req13.setType("points");
        req13.setMetric("avg_response_time");
        req13.setTags("service","web_transaction");
        this.apis.add(req13);

        RequestWapper req14 = new RequestWapper();
        req14.setUrl(String.format("%s/transactions/WebTransaction/apdex?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req14.setType("points");
        req14.setMetric("apdex");
        req14.setTags("service","web_transaction");
        this.apis.add(req14);


        //后台任务
        RequestWapper req15 = new RequestWapper();
        req15.setUrl(String.format("%s/transactions/OtherTransaction/response%%20time?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req15.setType("points");
        req15.setMetric("avg_response_time");
        req15.setTags("service","other_transaction");
        this.apis.add(req15);

        RequestWapper req16 = new RequestWapper();
        req16.setUrl(String.format("%s/transactions/OtherTransaction/throughput?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req16.setType("points");
        req16.setMetric("throughput");
        req16.setTags("service","other_transaction");
        this.apis.add(req16);

        RequestWapper req17 = new RequestWapper();
        req17.setUrl(String.format("%s/error?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req17.setType("errors");
        this.apis.add(req17);



        //slow sql
        RequestWapper req18 = new RequestWapper();
        req18.setUrl(String.format("%s/slowTrans?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req18.setType("list");
        this.apis.add(req18);

        RequestWapper req19 = new RequestWapper();
        req19.setUrl(String.format("%s/slowTrans/segment?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req19.setType("segment");
        this.apis.add(req19);

        RequestWapper req21 = new RequestWapper();
        req21.setUrl(String.format("%s/slowSqls?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req21.setType("slowSqls_trace");
        this.apis.add(req21);

    }

    private void run() throws IOException, InterruptedException {
        while (true) {
            try {
                for (RequestWapper reqWapper : this.apis) {
                    Metrics metrics = queryOneAPMApi(reqWapper);
                    //Metrics metrics = createOneAPMMetrics();
                    writeToFile(metrics);
                    writeToMetricServer(metrics);
                    Thread.sleep(1000);
                }
                Thread.sleep(60000); // 1 minutes
            } catch (SocketException e){
                System.out.println("network have disconnected");
                Thread.sleep(30000);
            } catch (Exception e) {
                System.out.println("In run:"+ e);
            }

            System.out.println("------------------");
        }
    }

    private Metrics queryOneAPMApi(RequestWapper serverUrl) throws IOException {
        System.out.println("connect to ONEAPM server " + serverUrl);
        BufferedReader in = null;
        try {
            URL url = new URL(serverUrl.getUrl());
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

            return parseJSON(content,serverUrl);
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

    private Metrics parseJSON(String content, RequestWapper reqWapper) {
        //TODO 后面想办法写这个parse
        if(!reqWapper.getType().equals("points")){
            logger.info(content);
            return null;
        }

        Metrics metrics = new Metrics();
        JSONObject obj = new JSONObject(content);
        int code = obj.getInt("code");
        String msg = obj.getString("msg");
        JSONArray results = obj.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject resultObj = results.getJSONObject(i);
            String tagName = resultObj.getString("name");
            String metricName = reqWapper.getMetric();
            reqWapper.setTags("host",tagName.replace("/","."));
            JSONArray points = resultObj.getJSONArray("points");
            for (int j = 0; j < points.length()-1; j++) {
                JSONObject pointObj = points.getJSONObject(j);
                double val = pointObj.getJSONObject("data").getDouble(reqWapper.getMetric());
                long ts = pointObj.getJSONObject("time").getLong("endTime");
                Metric m = new Metric(metricName, ts, val, reqWapper.getTags());
                metrics.metrics.add(m);
            }
        }
        return metrics;
    }

    private  void writeToFile(Metrics metrics) throws IOException {
        if(metrics == null)
            return ;

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
    private Metrics createOneAPMMetrics() {

        //String[] oneAPMMetrics = new String[] {"Spring.com.srie.cis.service.impl.WSServiceImpl.getDispatchCode", "JSP.up.upload.jsp", "JSP.esign.creatpng.jsp",
        //"Servlet.ESignServlet", "SpringController.cis.createSerAction.do", "JSP.Cc.createSer_005faction.jsp", "JSP.Cc.createSer_005faction.jsp", "JSP.ser.listSer.jsp",
        //"JSP.esign.sendEmail.jsp", "JSP.ser.ex_005freceiving.jsp", "Servlet.EsignShowServlet"};

        RequestWapper req = new RequestWapper();
        req.setUrl(String.format("%s/transactions/WebTransaction/response%%20time?&openid=%s&application_name=%s&span_time=%s&interval=%s", ONEAPM_SERVER, OPEN_ID, APP_NAME, SPAN_TIME, INTERVAL));
        req.setType("points");
        req.setMetric("avg_response_time");
        req.setTags("service","web_transaction");
        this.apis.add(req);

        RequestWapper[] oneAPMMetrics = new RequestWapper[]{req};
        Metrics metrics = new Metrics();
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Asia/Beijing"));
        start.add(Calendar.MINUTE, -60);
        int count = 60;
        while (count-- > 0) {
            for (RequestWapper requestWapper : oneAPMMetrics) {
                Metric m = new Metric(requestWapper.getMetric(), start.getTime().getTime(), rand.nextDouble() * 20,requestWapper.getTags());
                metrics.metrics.add(m);
            }
            start.add(Calendar.MINUTE, 1);
        }
        return metrics;
    }

    private  void writeToMetricServer(Metrics metrics) throws IOException, InterruptedException {
        if(metrics == null){
            return ;
        }
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

    class Metric {
        private final String metric;
        private Map<String, String> tags = new HashMap();
        private final long timestamp;
        private double value;

        public Metric(String metric, long timestamp, double value, Map<String,String> appendTags) {
            this.metric = metric;
            this.timestamp = timestamp/1000;
            this.tags.put("method", "http");
            this.tags.putAll(appendTags);
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


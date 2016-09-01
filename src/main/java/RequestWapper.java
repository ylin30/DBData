import java.util.HashMap;
import java.util.Map;

public class RequestWapper {
    private String url;
    private Map<String,String> tags = new HashMap();
    private String type;
    private String metric;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTags(String key,String value) {
        Map<String, String> m = new HashMap();
        m.put(key, value);
        this.tags.putAll(m);
    }
}

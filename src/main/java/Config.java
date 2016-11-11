import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;

public class Config {
    public static void main(String[] args) throws YamlException, FileNotFoundException {
        YamlReader reader = new YamlReader(new InputStreamReader(Config.class.getClassLoader().getResourceAsStream("application.yml")));
        Object object = reader.read();
        System.out.println(object);
        Map map = (Map)object;
        System.out.println(map.get("tokens"));
        System.out.println(map.get("app_name"));
    }
}

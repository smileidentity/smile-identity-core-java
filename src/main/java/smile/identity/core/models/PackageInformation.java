package smile.identity.core.models;

import lombok.Value;
import smile.identity.core.Utils;

import java.util.HashMap;
import java.util.Map;


@Value
public class PackageInformation {

    Map<String, Integer> apiVersion = setApiVersion();
    String language = "java";


    private Map<String, Integer> setApiVersion(){
        Map<String, Integer> map = new HashMap<>();
        String[] apiVersion = Utils.getApiVersion().split("\\.");
        map.put("buildNumber", Integer.valueOf(apiVersion[0]));
        map.put("majorVersion", Integer.valueOf(apiVersion[1]));
        map.put("minorVersion", Integer.valueOf(apiVersion[2]));
        return map;
    }
}

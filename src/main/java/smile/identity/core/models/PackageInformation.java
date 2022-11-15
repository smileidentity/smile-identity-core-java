package smile.identity.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter @Setter @NoArgsConstructor
public class PackageInformation {

    private final Map<String, Integer> apiVersion = setApiVersion();
    private final String language = "java";


    private Map<String, Integer> setApiVersion(){
        Map<String, Integer> map = new HashMap<>();
        map.put("buildNumber", 0);
        map.put("majorVersion", 2);
        map.put("minorVersion", 0);
        return map;
    }
}

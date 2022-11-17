package smile.identity.core;

import java.util.Properties;

public class Utils {
    private final static Properties properties = new Properties();

    public static String getVersion(){
        return properties.getProperty("version");
    }

    public static String getApiVersion(){
        return properties.getProperty("apiVersion");
    }
}

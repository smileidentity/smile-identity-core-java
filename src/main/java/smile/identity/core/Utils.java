package smile.identity.core;

import java.util.Properties;

public class Utils {
    private final static Properties properties = new Properties();

    public static String getVersion() {
        return properties.getProperty("version");
    }

    public static String getApiVersion() {
        return properties.getProperty("apiVersion");
    }

    public static String getSidServer(String sidServer) {
        if(sidServer.equals("0")) {
            return "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
        } else if (sidServer.equals("1")) {
            return "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
        } else {
            return sidServer;
        }
    }
}

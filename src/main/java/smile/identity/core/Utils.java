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
        if (sidServer.equals("0")) {
            return "https://testapi.smileidentity.com";
        } else if (sidServer.equals("1")) {
            return "https://api.smileidentity.com";
        } else {
            return sidServer;
        }
    }
}

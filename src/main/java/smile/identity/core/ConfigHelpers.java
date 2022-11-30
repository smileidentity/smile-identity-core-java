package smile.identity.core;

import java.util.Properties;

public class ConfigHelpers {
    private final static Properties properties = new Properties();
    private final static String TEST_ENV = "0";
    private final static String PROD_ENV = "1";
    private final static String TEST_SERVER = "https://testapi.smileidentity.com";
    private final static String PROD_SERVER = "https://api.smileidentity.com";

    public static String getVersion() {
        return properties.getProperty("version");
    }

    public static String getApiVersion() {
        return properties.getProperty("apiVersion");
    }

    public static String getSidServer(String sidServer) {
        if (sidServer.equals(TEST_ENV)) {
            return TEST_SERVER;
        } else if (sidServer.equals(PROD_ENV)) {
            return PROD_SERVER;
        } else {
            return sidServer;
        }
    }
}

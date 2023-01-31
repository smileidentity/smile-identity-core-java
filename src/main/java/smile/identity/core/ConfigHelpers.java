package smile.identity.core;


public class ConfigHelpers {
    private final static String TEST_ENV = "0";
    private final static String PROD_ENV = "1";
    private final static String TEST_SERVER = "https://testapi.smileidentity.com/";
    private final static String PROD_SERVER = "https://api.smileidentity.com/";

    public static String getVersion() {
        return "2.0.2";
    }

    public static String getApiVersion() {
        return "0.2.0";
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

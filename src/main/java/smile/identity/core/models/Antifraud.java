package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Value;

import java.util.List;

@Value
public class Antifraud {
    Summary summary;

    @Json(name = "smile_secure")
    SmileSecure smileSecure;

    @Value
    public static class Summary {
        @Json(name = "fraud_sources")
        List<String> fraudSources;
        @Json(name = "fraud_detected")
        boolean fraudDetected;
    }

    @Value
    public static class SmileSecure {
        @Json(name = "ResultCode")
        String resultCode;
        @Json(name = "ResultText")
        String resultText;
        @Json(name = "SuspectUsers")
        List<SuspectUser> suspectUsers;
    }

    @Value
    public static class SuspectUser {
        @Json(name = "user_id")
        String userId;
        @Json(name = "reasons")
        List<String> reasons;
    }
}
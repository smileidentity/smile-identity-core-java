package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
@NonFinal
@AllArgsConstructor
public class JobResponse {
    @Json(name = "JSONVersion")
    String jsonVersion;

    @Json(name = "SmileJobID")
    String smileJobId;

    @Json(name = "PartnerParams")
    PartnerParams partnerParams;

    @Json(name = "ResultType")
    String resultType;

    @Json(name = "ResultText")
    String resultText;

    @Json(name = "ResultCode")
    String resultCode;

    @Json(name = "IsFinalResult")
    String isFinalResult;

    @Json(name = "Actions")
    Actions actions;

    String signature;

    Instant timestamp;

    @Json(name = "ConfidenceValue")
    String confidence;

    @Json(name = "Source")
    String source;

    @Nullable
    @Json(name = "Antifraud")
    Antifraud antifraud;

    @Json(name = "FullData")
    Map<String, Object> fullData;

    public JobResponse(String smileJobId, PartnerParams partnerParams) {
        this("", smileJobId, partnerParams, "", "", "", "", null, "", null,
                "", "", null, new HashMap<>());
    }

    @Value
    @NonFinal
    @AllArgsConstructor
    public static class Antifraud {
        @Json(name = "summary")
        public Summary summary;
        @Json(name = "smile_secure")
        public SmileSecure smileSecure;
    }

    @Value
    @NonFinal
    @AllArgsConstructor
    public static class Summary {
        @Json(name = "fraud_sources")
        public List<String> fraudSources;
        @Json(name = "fraud_detected")
        public Boolean fraudDetected;
    }

    @Value
    @NonFinal
    @AllArgsConstructor
    public static class SmileSecure {
        @Json(name = "ResultCode")
        public String resultCode;
        @Json(name = "ResultText")
        public String resultText;
        @Json(name = "SuspectUsers")
        public List<SuspectUser> suspectUsers;
    }

    @Value
    @NonFinal
    @AllArgsConstructor
    public static class SuspectUser {
        @Json(name = "reasons")
        public List<String> reasons;
        @Json(name = "user_id")
        public String userId;
    }
}
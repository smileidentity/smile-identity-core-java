package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.time.Instant;
import java.util.Map;

@Value
@NonFinal
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

    @Json(name = "FullData")
    Map<String, Object> fullData;

}

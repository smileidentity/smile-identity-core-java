package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class JobResponse {
    @Json(name = "JSONVersion")
    private String jsonVersion;

    @Json(name = "SmileJobID")
    private String smileJobId;

    @Json(name = "PartnerParams")
    private PartnerParams partnerParams;

    @Json(name = "ResultType")
    private String resultType;

    @Json(name = "ResultText")
    private String resultText;

    @Json(name = "ResultCode")
    private String resultCode;

    @Json(name = "IsFinalResult")
    private String isFinalResult;

    @Json(name = "Actions")
    private Actions actions;

    private String signature;

    private String timestamp;

    @Json(name = "ConfidenceValue")
    private String confidence;

    @Json(name = "Source")
    private String source;

    @Json(name = "FullData")
    private Map<String, Object> fullData;

}
package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class PreUploadRequest {
    @Json(name = "file_name")
    private final String fileName = "selfie.zip";
    private String timestamp;
    private String signature;
    @Json(name = "smile_client_id")
    private String smileClientId;

    @Json(name = "partner_params")
    private PartnerParams partnerParams;

    @Json(name = "model_parameters")
    private Map<String, String> modelParameters = new HashMap<>();
    @Json(name = "callback_url")
    private String callbackUrl;

    @Json(name = "source_sdk")
    private final String sourceSdk = "java";

    @Json(name = "source_sdk_version")
    private final String sourceSdkVersion = "0.0.2";

    public PreUploadRequest(String timestamp, String signature, String smileClientId, PartnerParams partnerParams, String callbackUrl) {
        this.timestamp = timestamp;
        this.signature = signature;
        this.smileClientId = smileClientId;
        this.partnerParams = partnerParams;
        this.callbackUrl = callbackUrl;
    }

}

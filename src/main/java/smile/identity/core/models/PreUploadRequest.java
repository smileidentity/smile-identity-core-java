package smile.identity.core.models;

import com.squareup.moshi.Json;

import lombok.Value;
import smile.identity.core.Utils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Value
public class PreUploadRequest {
    @Json(name = "file_name")
    String fileName = "selfie.zip";

    Instant timestamp;

    String signature;

    @Json(name = "smile_client_id")
    String smileClientId;

    @Json(name = "partner_params")
    PartnerParams partnerParams;

    @Json(name = "model_parameters")
    Map<String, String> modelParameters = new HashMap<>();

    @Json(name = "callback_url")
    String callbackUrl;

    @Json(name = "source_sdk")
    String sourceSdk = "java";

    @Json(name = "source_sdk_version")
    String sourceSdkVersion = Utils.getVersion();

    public PreUploadRequest(Instant timestamp, String signature, String smileClientId, PartnerParams partnerParams, String callbackUrl) {
        this.timestamp = timestamp;
        this.signature = signature;
        this.smileClientId = smileClientId;
        this.partnerParams = partnerParams;
        this.callbackUrl = callbackUrl;
    }

}

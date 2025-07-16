package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Value;

import java.time.Instant;

@Value
public class MiscInformation {
    String retry = "false";

    @Json(name = "partner_params")
    PartnerParams partnerParams;

    Instant timestamp;

    String signature;

    @Json(name = "file_name")
    String fileName = "selfie.zip";

    @Json(name = "smile_client_id")
    String smileClientId;

    @Json(name = "callback_url")
    String callbackUrl;

    UserData userData;
}

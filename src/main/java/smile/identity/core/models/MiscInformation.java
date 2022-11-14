package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class MiscInformation {
    private final String retry = "false";

    @Json(name = "partner_params")
    private PartnerParams partnerParams;

    private String timestamp;

    private String signature;

    @Json(name = "file_name")
    private final String fileName = "selfie.zip";

    @Json(name = "smile_client_id")
    private String smileClientId;

    @Json(name = "callback_url")
    private String callbackUrl;

    private final UserData userData = new UserData();

    public MiscInformation(PartnerParams partnerParams, String timestamp, String signature, String smileClientId, String callbackUrl) {
        this.partnerParams = partnerParams;
        this.timestamp = timestamp;
        this.signature = signature;
        this.smileClientId = smileClientId;
        this.callbackUrl = callbackUrl;
    }
}

package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import smile.identity.core.enums.Product;

@Getter @Setter @NoArgsConstructor
public class WebTokenRequest {

    @Json(name = "user_id")
    private String userId;
    @Json(name = "job_id")
    private String jobId;
    private Product product;
    @Json(name = "callback_url")
    private String callbackUrl;
    private String signature;
    private String timestamp;
    @Json(name = "partner_id")
    private String partnerId;
    @Json(name = "source_sdk")
    private final String sourceSdk = "java";
    @Json(name = "source_sdk_version")
    private final String sourceSdkVersion = "3.0.0";
}
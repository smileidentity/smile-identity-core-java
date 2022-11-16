package smile.identity.core.models;

import com.squareup.moshi.Json;

import lombok.Value;
import smile.identity.core.Utils;
import smile.identity.core.enums.Product;

@Value
public class WebTokenRequest {

    @Json(name = "user_id")
    String userId;

    @Json(name = "job_id")
    String jobId;

    Product product;

    @Json(name = "callback_url")
    String callbackUrl;

    String signature;

    String timestamp;

    @Json(name = "partner_id")
    String partnerId;

    @Json(name = "source_sdk")
    String sourceSdk = "java";

    @Json(name = "source_sdk_version")
    String sourceSdkVersion = Utils.getVersion();
}

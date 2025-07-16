package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Value;

import java.time.Instant;

@Value
public class JobStatusRequest {

    @Json(name = "partner_id")
    String partnerId;

    @Json(name = "user_id")
    String userId;

    @Json(name = "job_id")
    String jobId;

    @Json(name = "image_links")
    boolean imageLinks;

    boolean history;

    String signature;

    Instant timestamp;
}

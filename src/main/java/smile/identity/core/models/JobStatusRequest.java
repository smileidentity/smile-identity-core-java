package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusRequest {

    @Json(name = "partner_id")
    private String partnerId;

    @Json(name = "user_id")
    private String userId;

    @Json(name = "job_id")
    private String jobId;

    @Json(name = "image_links")
    private boolean imageLinks;

    private boolean history;

    private String signature;

    private String timestamp;

}

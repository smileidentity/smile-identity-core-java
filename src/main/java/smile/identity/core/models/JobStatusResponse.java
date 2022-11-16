package smile.identity.core.models;

import com.squareup.moshi.Json;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import lombok.Value;

@Value
public class JobStatusResponse {

    String code;

    @Json(name = "job_complete")
    boolean jobComplete;

    @Json(name = "job_success")
    boolean jobSuccess;

    JobResponse result;

    String signature;

    Instant timestamp;

    @Json(name = "image_links")
    Map<String, String> imageLinks;

    List<JobResponse> history;

}

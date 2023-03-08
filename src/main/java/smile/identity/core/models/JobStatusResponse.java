package smile.identity.core.models;

import com.squareup.moshi.Json;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class JobStatusResponse {

    String code;

    @Json(name = "job_complete")
    boolean jobComplete;

    @Json(name = "job_success")
    boolean jobSuccess;

    Result result;

    String signature;

    Instant timestamp;

    @Json(name = "image_links")
    Map<String, String> imageLinks;

    List<JobResponse> history;

    public JobStatusResponse(JobResponse result) {
        this("", false, true, new Result(null, result), result.getSignature(),
                result.getTimestamp(), new HashMap<>(), new ArrayList<>());
    }

}

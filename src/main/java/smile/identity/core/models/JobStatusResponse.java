package smile.identity.core.models;

import com.squareup.moshi.Json;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.annotation.Nullable;

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
        this("", false, true, new Result(result), result.getSignature(),
                result.getTimestamp(), new HashMap<>(), new ArrayList<>());
    }

    @Value
    public static class Result {
        @Nullable
        String message;

        @Nullable
        JobResponse jobResponse;

        public Result(@Nullable String message) {
            this.message = message;
            this.jobResponse = null;
        }

        public Result(@Nullable JobResponse jobResponse) {
            this.jobResponse = jobResponse;
            this.message = null;
        }

        public Result() {
            this.message = null;
            this.jobResponse = null;
        }
    }

}

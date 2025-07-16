package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Value;
import smile.identity.core.enums.JobType;

import java.util.Map;

@Value
public class PartnerParams {
    @Json(name = "job_type")
    JobType jobType;

    @Json(name = "user_id")
    String userId;

    @Json(name = "job_id")
    String jobId;

    @Json(name = "optional_info")
    Map<String, Object> optionalInfo;
}

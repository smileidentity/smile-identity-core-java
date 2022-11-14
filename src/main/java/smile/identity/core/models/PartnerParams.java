package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import smile.identity.core.enums.JobType;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PartnerParams {
    @Json(name = "job_type")
    private JobType jobType;

    @Json(name = "optional_info")
    private String optionalInfo;

    @Json(name = "user_id")
    private String userId;

    @Json(name = "job_id")
    private String jobId;

}

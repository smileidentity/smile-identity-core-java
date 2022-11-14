package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class JobStatusResponse {

    private String code;

    @Json(name = "job_complete")
    private boolean jobComplete;

    @Json(name = "job_success")
    private boolean jobSuccess;

    private JobResponse result;

    private String signature;

    private String timestamp;

    @Json(name = "image_links")
    private Map<String, String> imageLinks;

    private List<JobResponse> history;

}

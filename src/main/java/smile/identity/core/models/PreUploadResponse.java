package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PreUploadResponse {
    @Json(name = "upload_url")
    private String uploadUrl;

    @Json(name = "smile_job_id")
    private String smileJobId;

    @Json(name = "ref_id")
    private String refId;

    @Json(name = "camera_config")
    private String cameraConfig;

    private String code;

}

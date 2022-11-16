package smile.identity.core.models;

import com.squareup.moshi.Json;

import lombok.Value;

@Value
public class PreUploadResponse {
    @Json(name = "upload_url")
    String uploadUrl;

    @Json(name = "smile_job_id")
    String smileJobId;

    @Json(name = "ref_id")
    String refId;

    @Json(name = "camera_config")
    String cameraConfig;

    String code;

}

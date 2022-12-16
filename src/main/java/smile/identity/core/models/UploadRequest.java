package smile.identity.core.models;

import com.squareup.moshi.Json;

import java.util.List;

import lombok.Value;

@Value
public class UploadRequest {

    @Json(name = "package_information")
    PackageInformation packageInformation;
    @Json(name = "misc_information")
    MiscInformation miscInformation;
    @Json(name = "id_info")
    IdInfo idInfo;
    List<ImageDetail> images;
    @Json(name = "server_information")
    PreUploadResponse serverInformation;
}

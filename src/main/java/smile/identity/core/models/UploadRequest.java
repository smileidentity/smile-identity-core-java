package smile.identity.core.models;

import lombok.Value;

import java.util.List;

@Value
public class UploadRequest {
    PackageInformation packageInformation;
    MiscInformation miscInformation;
    IdInfo idInfo;
    List<ImageDetail> images;
    String serverInformation;
}

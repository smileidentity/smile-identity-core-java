package smile.identity.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class UploadRequest {
    private PackageInformation packageInformation;
    private MiscInformation miscInformation;
    private IdInfo idInfo;
    private List<ImageDetail> images;
    private String serverInformation;
}
package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import smile.identity.core.enums.ImageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDetail {

    @Json(name = "image_type_id")
    private ImageType imageTypeId;

    private String image;

    @Json(name = "file_name")
    private String fileName;

}
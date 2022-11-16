package smile.identity.core.models;

import com.squareup.moshi.Json;

import lombok.Value;
import smile.identity.core.enums.ImageType;

@Value
public class ImageDetail {

    @Json(name = "image_type_id")
    ImageType imageTypeId;

    String image;

    @Json(name = "file_name")
    String fileName;

}

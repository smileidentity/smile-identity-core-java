package smile.identity.core.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import smile.identity.core.enums.ImageType;

public class ImageTypeAdapter {
    @ToJson
    int toJson(ImageType imageType){
        return imageType.getValue();
    }

    @FromJson
    ImageType fromJson(int value) {
        return ImageType.fromValue(value);
    }
}


package smile.identity.core;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class ImageParameters {
    JSONArray images;

    public ImageParameters() {
        JSONArray images = new JSONArray();
        this.images = images;
    }

    public void add(Integer image_type_id, String image) throws IllegalArgumentException {
        if (this.images == null) {
            this.images = new JSONArray();
        }

        if (image_type_id == null) {
            throw new IllegalArgumentException("image type cannot be null or empty");
        }

        if (checkNullAndEmpty(image)) {
            throw new IllegalArgumentException("image value cannot be null or empty");
        }
        JSONObject image_obj = new JSONObject();
        image_obj.put("image_type_id", image_type_id);
        image_obj.put("image", image);
        images.add(image_obj);
    }

    public String get() {
        return images.toString();
    }

    private Boolean checkNullAndEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package smile.identity.core;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class ImageParameters {
  JSONArray images;

  public ImageParameters() {
    JSONArray images = new JSONArray();
    this.images = images;
  }

  public void add(Integer image_type_id, String image) {

    try {
      JSONObject image_obj = new JSONObject();
      image_obj.put("image_type_id", image_type_id);
      image_obj.put("image", image);
      images.add(image_obj);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Exception" + e);
    }
  }

  public JSONArray get() {
    return images;
  }
}

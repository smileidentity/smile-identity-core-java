package smile.identity.core;
import org.json.simple.JSONObject;

public class Options implements Parameters {
  JSONObject options;

  public Options(String optional_callback, Boolean return_job_status, Boolean return_history, Boolean return_images) {
    JSONObject obj = new JSONObject();

    try {
      obj.put("optional_callback", optional_callback);
      obj.put("return_job_status", return_job_status);
      obj.put("return_history", return_history);
      obj.put("return_images", return_images);
    } catch (Exception e) {
      throw e;
    }
    this.options = obj;
  }

  public void add(String key, String value) {}

  public JSONObject get() {
    return options;
  }
}

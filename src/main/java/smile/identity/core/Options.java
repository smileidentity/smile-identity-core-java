package smile.identity.core;
import org.json.simple.JSONObject;

public class Options implements Parameters {
  JSONObject options;

  public Options(Boolean return_history, Boolean return_images) {
    this(null, null, return_history, return_images);
  }

  public Options(String optional_callback, Boolean return_job_status, Boolean return_history, Boolean return_images) {
    JSONObject obj = new JSONObject();

    try {
      if (!checkNullAndEmpty(optional_callback)) obj.put("optional_callback", optional_callback);
      if (!checkNull(return_job_status)) obj.put("return_job_status", return_job_status);
      if (!checkNull(return_history)) obj.put("return_history", return_history);
      if (!checkNull(return_images)) obj.put("return_images", return_images);

    } catch (Exception e) {
      throw e;
    }
    this.options = obj;
  }

  public void add(String key, String value) {}

  public String get() {
    return options.toString();
  }

  private Boolean checkNullAndEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  private Boolean checkNull(Boolean value) {
    return value == null;
  }

}

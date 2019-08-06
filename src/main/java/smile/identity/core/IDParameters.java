package smile.identity.core;
import org.json.simple.JSONObject;

public class IDParameters implements Parameters {
  JSONObject idInfo;

  public IDParameters(String first_name, String middle_name, String last_name, String country, String id_type, String id_number, String entered) {
    JSONObject obj = new JSONObject();

    try {
      obj.put("first_name", first_name);
      obj.put("last_name", last_name);
      obj.put("middle_name", middle_name);
      obj.put("country", country);
      obj.put("id_type", id_type);
      obj.put("id_number", id_number);
      obj.put("entered", entered);

      if(entered == "true") {
        if (first_name == null || last_name == null || country == null || id_type == null || id_number == null) {
          throw new IllegalArgumentException("ID Arguments may not be null");
        }

        if (first_name.trim().isEmpty() || last_name.trim().isEmpty() || country.trim().isEmpty() || id_type.trim().isEmpty() || id_number.trim().isEmpty()) {
          throw new IllegalArgumentException("ID Arguments may not be empty");
        }
      }

    } catch(Exception e) {
      System.out.println("exception" + e);
      throw e;
    }

    this.idInfo = obj;
  }

  public void add(String key, String value) {}

  public JSONObject get() {
    return idInfo;
  }
}

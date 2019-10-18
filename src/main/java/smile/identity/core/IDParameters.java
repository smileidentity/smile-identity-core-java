package smile.identity.core;
import org.json.simple.JSONObject;

public class IDParameters implements Parameters {
  JSONObject idInfo;

  public IDParameters(String first_name, String middle_name, String last_name, String country, String id_type, String id_number, String dob, String phone_number, String entered) {
    JSONObject obj = new JSONObject();

    try {
      obj.put("first_name", first_name);
      obj.put("last_name", last_name);
      obj.put("middle_name", middle_name);
      obj.put("country", country);
      obj.put("id_type", id_type);
      obj.put("id_number", id_number);
      obj.put("dob", dob);
      obj.put("phone_number", phone_number);
      obj.put("entered", entered);

      if(entered == "true") {
        if (checkNullAndEmpty(id_number)) {
          throw new IllegalArgumentException("id_number cannot be empty");
        }

        if ((country.equalsIgnoreCase("NG")) &&  (id_type.equalsIgnoreCase("PASSPORT") ||  id_type.equalsIgnoreCase("VOTER_ID") || id_type.equalsIgnoreCase("DRIVERS_LICENSE") ||  id_type.equalsIgnoreCase("NATIONAL_ID") ||  id_type.equalsIgnoreCase("TIN") ||  id_type.equalsIgnoreCase("CAC")) && (checkNullAndEmpty(dob))) {
          throw new IllegalArgumentException("The ID type " + id_type + "for " + country + " requires a valid dob paramater." );
        }
      }

    } catch(Exception e) {
      throw e;
    }

    this.idInfo = obj;
  }

  public IDParameters(String first_name, String middle_name, String last_name, String country, String id_type, String id_number, String dob, String phone_number) {
  JSONObject obj = new JSONObject();

  try {
    obj.put("first_name", first_name);
    obj.put("last_name", last_name);
    obj.put("middle_name", middle_name);
    obj.put("country", country);
    obj.put("id_type", id_type);
    obj.put("id_number", id_number);
    obj.put("dob", dob);
    obj.put("phone_number", phone_number);
  } catch(Exception e) {
    throw e;
  }

  if (checkNullAndEmpty(id_number)) {
    throw new IllegalArgumentException("id_number cannot be empty");
  }

  this.idInfo = obj;
}

  public void add(String key, String value) {}

  public String get() {
    return idInfo.toString();
  }

  private Boolean checkNullAndEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }
}

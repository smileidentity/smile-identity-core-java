package smile.identity.core;

import org.json.simple.JSONObject;

public class IDParameters implements Parameters {
    JSONObject idInfo;

    public IDParameters(String country, String id_type, String id_number, String entered) {
        this("", "", "", country, id_type, id_number, "", "", entered);
    }

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

            String[] requiredFields = {"id_number", "id_type", "country"};
            if (entered == "true") {
                for (String field : requiredFields) {
                    String fieldValue = (String) obj.get(field);
                    if (checkNullAndEmpty(fieldValue)) {
                        throw new IllegalArgumentException(field + " cannot be empty");
                    }
                }
            }

        } catch (Exception e) {
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
        } catch (Exception e) {
            throw e;
        }

        String[] requiredFields = {"id_number", "id_type", "country"};
        for (String field : requiredFields) {
            String fieldValue = (String) obj.get(field);
            if (checkNullAndEmpty(fieldValue)) {
                throw new IllegalArgumentException(field + " cannot be empty");
            }
        }

        this.idInfo = obj;
    }

    public void add(String key, String value) throws IllegalArgumentException {
        if (this.idInfo == null) {
            this.idInfo = new JSONObject();
        }
        if (checkNullAndEmpty(key)) {
            throw new IllegalArgumentException("key cannot be null or empty");
        }
        if (checkNullAndEmpty(value)) {
            throw new IllegalArgumentException(key + " cannot be null or empty");
        }
        this.idInfo.put(key, value);
    }

    public String get() {
        return idInfo.toString();
    }

    private Boolean checkNullAndEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}

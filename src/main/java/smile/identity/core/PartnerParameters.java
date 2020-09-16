package smile.identity.core;

import org.json.simple.JSONObject;

interface Parameters {
    public void add(String key1, String key2) throws Exception;

    public String get();
}

public class PartnerParameters implements Parameters {
    JSONObject partnerParams;

    public PartnerParameters(String user_id, String job_id, Integer job_type) {

        JSONObject obj = new JSONObject();
        try {
            if (user_id == null || job_id == null || job_type == null) {
                throw new IllegalArgumentException("Partner Parameter Arguments may not be null");
            }

            if (user_id.trim().isEmpty() || job_id.trim().isEmpty()) {
                throw new IllegalArgumentException("Partner Parameter Arguments may not be empty");
            }

            obj.put("user_id", user_id);
            obj.put("job_id", job_id);
            obj.put("job_type", job_type);
        } catch (Exception e) {
            throw e;
        }

        this.partnerParams = obj;
    }

    public void add(String key, String value) {
        if (this.partnerParams == null) {
            this.partnerParams = new JSONObject();
        }
        if (checkNullAndEmpty(key)) {
            throw new IllegalArgumentException("key cannot be null or empty");
        }
        if (checkNullAndEmpty(value)) {
            throw new IllegalArgumentException(key + " cannot be null or empty");
        }
        this.partnerParams.put(key, value);
    }

    public String get() {
        return partnerParams.toString();
    }

    private Boolean checkNullAndEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}


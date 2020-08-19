package smile.identity.core;

// json converter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// apache http client
// apache http client

public class Utilities {

    private String partner_id;
    private String api_key;
    private String url;

    @Deprecated
    public Utilities(String partner_id, String api_key, Integer sid_server) {
        this(partner_id, api_key, String.valueOf(sid_server));
    }

    public Utilities(String partner_id, String api_key, String sid_server) {
        try {
            this.partner_id = partner_id;
            this.api_key = api_key;

            if (sid_server.equals("0")) {
                url = "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
            } else if (sid_server.equals("1")) {
                url = "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
            } else {
                this.url = sid_server;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String get_job_status(String user_id, String job_id, String options) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String sec_key = determineSecKey(timestamp);
        JSONObject optionsJson = null;

        if (options != null && !options.trim().isEmpty()) {
            JSONParser parser = new JSONParser();
            optionsJson = (JSONObject) parser.parse(options);
        } else {
            optionsJson = fillInJobStatusOptions();
        }

        return queryJobStatus(user_id, job_id, optionsJson).toString();
    }

    public void validate_id_params(String partner_params, String id_info_params, Boolean useValidationApi) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
        JSONObject idInfo = (JSONObject) parser.parse(id_info_params);
        if (!idInfo.containsKey("entered") || !((Boolean) idInfo.get("entered"))) {
            return;
        }
        JSONObject combined = idInfo;
        for (Object key : partnerParams.keySet()) {
            combined.put(key, partnerParams.get(key));
        }

        List<String> generalRequire = new ArrayList<String>() {
            {
                add("country");
                add("id_type");
                add("id_number");
            }
        };
        if (combined.containsKey("entered")) {
            for (String key : generalRequire) {
                if (!combined.containsKey(key)) {
                    throw new IllegalArgumentException("key " + key + " cannot be empty");
                }
            }
        }
        if (!useValidationApi) {
            return;
        }
        JSONObject smileServices = query_smile_id_services();
        JSONArray idTypes = ((JSONArray) smileServices.get("id_types"));
        for (int i = 0; i < idTypes.size(); i++) {
            JSONObject idType = (JSONObject) idTypes.get(i);
            if (!idType.containsKey(combined.get("country").toString())) {
                throw new IllegalArgumentException("Invalid value for key country");
            }
            JSONObject country = (JSONObject) idType.get(combined.get("country").toString());
            if (!country.containsKey(combined.get("id_type").toString())) {
                throw new IllegalArgumentException("Invalid value for key id_type");
            }
            JSONArray params = (JSONArray) country.get(combined.get("id_type").toString());
            for (int k = 0; k < params.size(); k++) {
                String param = (String) params.get(k);
                if (!combined.containsKey(param) ||
                        (combined.get(param) == null && TextUtils.isEmpty(combined.get(param).toString()))) {
                    throw new IllegalArgumentException("Invalid value for key " + param);
                }
            }
        }
    }

    public JSONObject query_smile_id_services() throws Exception {
        JSONObject responseJson = null;

        String smileServicesUrl = (url + "/services").toString();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(smileServicesUrl);

        try {

            httpGet.setHeader("content-type", "application/json");

            HttpResponse response = client.execute(httpGet);
            final int statusCode = response.getStatusLine().getStatusCode();
            String strResult = readHttpResponse(response);

            if (statusCode != 200) {
                final String msg = String.format("Failed to get entity frm %s, response=%d:%s - %s",
                        smileServicesUrl, statusCode, response.getStatusLine().getReasonPhrase(), strResult);
                throw new RuntimeException(msg);
            } else {
                JSONParser parser = new JSONParser();
                responseJson = (JSONObject) parser.parse(strResult);
            }
        } catch (Exception e) {
            throw e;
        }
        return responseJson;
    }

    private JSONObject queryJobStatus(String user_id, String job_id, JSONObject options) throws Exception {
        Boolean job_complete = false;
        JSONObject responseJson = null;

        String jobStatusUrl = (url + "/job_status").toString();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(jobStatusUrl);

        try {
            StringEntity entityForPost = new StringEntity(configureJobQueryBody(user_id, job_id, options).toString());

            post.setHeader("content-type", "application/json");
            post.setEntity(entityForPost);

            HttpResponse response = client.execute(post);
            final int statusCode = response.getStatusLine().getStatusCode();
            String strResult = readHttpResponse(response);

            if (statusCode != 200) {
                final String msg = String.format("Failed to post entity to %s, response=%d:%s - %s",
                        jobStatusUrl, statusCode, response.getStatusLine().getReasonPhrase(), strResult);
                throw new RuntimeException(msg);
            } else {
                JSONParser parser = new JSONParser();
                responseJson = (JSONObject) parser.parse(strResult);

                String timestamp = (String) responseJson.get("timestamp");
                String secKey = (String) responseJson.get("signature");

                Boolean valid = new Signature(partner_id, api_key).confirm_sec_key(timestamp, secKey);
                if (!valid) {
                    throw new IllegalArgumentException("Unable to confirm validity of the job_status response");
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return responseJson;
    }

    private JSONObject configureJobQueryBody(String user_id, String job_id, JSONObject options) throws Exception {
        JSONObject body = new JSONObject();
        Boolean returnImages = (Boolean) options.get("return_images");
        Boolean returnHistory = (Boolean) options.get("return_history");
        Long timestamp = System.currentTimeMillis();
        try {
            body.put("sec_key", determineSecKey(timestamp));
            body.put("timestamp", timestamp);
            body.put("partner_id", partner_id);
            body.put("user_id", user_id);
            body.put("job_id", job_id);
            body.put("image_links", returnImages);
            body.put("history", returnHistory);
        } catch (Exception e) {
            throw e;
        }
        return body;
    }

    // these two methods are common across web api, we could put it in a helper class but it would mean that the functions are public
    private String determineSecKey(Long timestamp) throws Exception {
        Signature connection = new Signature(partner_id, api_key);
        String secKey = "";
        JSONParser parser = new JSONParser();

        try {
            String signatureJsonStr = connection.generate_sec_key(timestamp);
            JSONObject signature = (JSONObject) parser.parse(signatureJsonStr);
            secKey = (String) signature.get("sec_key");
        } catch (Exception e) {
            throw e;
        }

        return secKey;
    }

    private String readHttpResponse(HttpResponse response) throws Exception {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    private JSONObject fillInJobStatusOptions() throws Exception {
        JSONObject obj = new JSONObject();
        try {
            obj.put("return_history", false);
            obj.put("return_images", false);
        } catch (Exception e) {
            throw e;
        }

        return obj;
    }
}

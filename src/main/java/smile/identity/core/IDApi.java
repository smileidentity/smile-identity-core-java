package smile.identity.core;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

// json converter
// apache http client

public class IDApi {
    private String partner_id;
    private String api_key;
    private String url;
    private String sid_server;

    private int connectionTimeout = -1;
    private int readTimeout = -1;

    @Deprecated
    public IDApi(String partner_id, String api_key, Integer sid_server) throws Exception {
        this(partner_id, api_key, String.valueOf(sid_server));
    }


    public IDApi(String partner_id, String api_key, String sid_server) throws Exception {
        try {
            this.partner_id = partner_id;
            this.api_key = api_key;
            this.sid_server = sid_server;

            if (sid_server.equals("0")) {
                url = "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
            } else if (sid_server.equals("1")) {
                url = "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
            } else {
                url = sid_server;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public IDApi(String partner_id, String api_key, String sid_server, int connectionTimeout, int readTimeout) throws Exception {
        this(partner_id, api_key, sid_server);
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public String submit_job(String partner_params, String id_info_params) throws Exception {
        return submit_job(partner_params, id_info_params, true);
    }

    public String submit_job(String partner_params, String id_info_params, Boolean useValidationApi) throws Exception {

        String response = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
            JSONObject idInfo = (JSONObject) parser.parse(id_info_params);

            Long job_type = (Long) partnerParams.get("job_type");
            if (job_type != 5) {
                throw new IllegalArgumentException("Please ensure that you are setting your job_type to 5 to query ID Api");
            }
            new Utilities(partner_id, api_key, sid_server, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, useValidationApi);

            Long timestamp = System.currentTimeMillis();
            String sec_key = determineSecKey(timestamp);

            response = setupRequests(sec_key, timestamp, partnerParams, idInfo);
        } catch (Exception e) {
            throw e;
        }

        return response;
    }

    private String setupRequests(String sec_key, Long timestamp, JSONObject partnerParams, JSONObject idInfo) throws Exception {
        String strResponse = null;
        try {
            String idApiUrl = url + "/id_verification";

            HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
            HttpPost post = new HttpPost(idApiUrl.trim());
            JSONObject uploadBody = configureJson(sec_key, timestamp, partnerParams, idInfo);
            StringEntity entityForPost = new StringEntity(uploadBody.toString());
            post.setHeader("content-type", "application/json");
            post.setEntity(entityForPost);

            HttpResponse response = client.execute(post);
            final int statusCode = response.getStatusLine().getStatusCode();
            strResponse = readHttpResponse(response);

            if (statusCode != 200) {
                final String msg = String.format("Failed to post entity to %s, response=%d:%s - %s",
                        idApiUrl, statusCode, response.getStatusLine().getReasonPhrase(), strResponse);
                throw new RuntimeException(msg);
            }
        } catch (Exception e) {
            throw e;
        }
        return strResponse;
    }

    private JSONObject configureJson(String sec_key, Long timestamp, JSONObject partnerParams, JSONObject idInfo) throws Exception {
        JSONObject body = new JSONObject();

        try {
            body.put("timestamp", timestamp);
            body.put("sec_key", sec_key);
            body.put("partner_id", partner_id);
            body.put("partner_params", partnerParams);
            body.putAll(idInfo);
        } catch (Exception e) {
            throw e;
        }
        return body;
    }

    private String determineSecKey(Long timestamp) throws Exception {
        SignatureTest connection = new SignatureTest(partner_id, api_key);
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
}

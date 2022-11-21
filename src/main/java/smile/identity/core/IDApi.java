package smile.identity.core;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

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
    }

    public IDApi(String partner_id, String api_key, String sid_server, int connectionTimeout, int readTimeout) throws Exception {
        this(partner_id, api_key, sid_server);
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public String submit_job(String partner_params, String id_info_params) throws Exception {
        return submit_job(partner_params, id_info_params, false, null) ;
    }

    public String submit_job(String partner_params, String id_info_params, String options_params) throws Exception {
        return submit_job(partner_params, id_info_params, true, options_params);
    }

    public String submit_job(String partner_params, String id_info_params, Boolean useValidationApi) throws Exception {
    	return submit_job(partner_params, id_info_params, useValidationApi, null);
    }

    public String submit_job(String partner_params, String id_info_params, Boolean useValidationApi, String options_params) throws Exception {

    	JSONParser parser = new JSONParser();
    	JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
        JSONObject idInfo = (JSONObject) parser.parse(id_info_params);

        Long job_type = (Long) partnerParams.get("job_type");
        
        if (job_type != 5) {
            throw new IllegalArgumentException("Please ensure that you are setting your job_type to 5 to query ID Api");
        }
        
        new Utilities(partner_id, api_key, sid_server, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, useValidationApi);

        Boolean useSignature = true;
        Long timestamp = System.currentTimeMillis();
        Signature sigObj = new Signature(partner_id, api_key);
        
        if (options_params != null && !options_params.trim().isEmpty()) {
            JSONObject options = (JSONObject) parser.parse(options_params);
            useSignature = Signature.useSignature(options);
        }
        
        String signature = sigObj.getSignature(timestamp);
        
        return setupRequests(signature, timestamp, partnerParams, idInfo, useSignature);
    }

    private String setupRequests(String signature, Long timestamp, JSONObject partnerParams, JSONObject idInfo, Boolean useSignature) throws Exception {
        String strResponse = null;
        
        String idApiUrl = url + "/id_verification";

        HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
        HttpPost post = new HttpPost(idApiUrl.trim());
        JSONObject uploadBody = configureJson(signature, timestamp, partnerParams, idInfo, useSignature);
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
        
        return strResponse;
    }

    @SuppressWarnings("unchecked")
	private JSONObject configureJson(String signature, Long timestamp, JSONObject partnerParams, JSONObject idInfo, Boolean useSignature) throws Exception {
        JSONObject body = new JSONObject();
        body.put(Signature.TIME_STAMP_KEY, new SimpleDateFormat(Signature.DATE_TIME_FORMAT).format(timestamp));
        body.put(Signature.SIGNATURE_KEY, signature);
        body.put("partner_id", partner_id);
        body.put("partner_params", partnerParams);
        body.putAll(idInfo);
        return body;
    }

    private String readHttpResponse(HttpResponse response) throws Exception {
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        return result.toString();
    }
}
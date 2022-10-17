package smile.identity.core;

// json converter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

// apache http client
// apache http client

public class Utilities {

    private String partner_id;
    private String api_key;
    private String url;
    private int connectionTimeout = -1;
    private int readTimeout = -1;

    @Deprecated
    public Utilities(String partner_id, String api_key, Integer sid_server) {
        this(partner_id, api_key, String.valueOf(sid_server));
    }

    public Utilities(String partner_id, String api_key, String sid_server) {
    	this.partner_id = partner_id;
        this.api_key = api_key;

        if (sid_server.equals("0")) {
            url = "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
        } else if (sid_server.equals("1")) {
            url = "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
        } else {
            this.url = sid_server;
        }
    }

    public Utilities(String partner_id, String api_key, String sid_server, int connectionTimeout, int readTimeout) {
        this(partner_id, api_key, sid_server);
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public String get_job_status(String user_id, String job_id, String options) throws Exception {
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
        
        if (!idInfo.containsKey("entered") || !Boolean.parseBoolean(String.valueOf(idInfo.get("entered")))) {
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
        JSONObject idTypes = ((JSONObject) smileServices.get("id_types"));
        
        if (idTypes != null) {
            if (!idTypes.containsKey(combined.get("country").toString())) {
                throw new IllegalArgumentException("Invalid value for key country");
            }

            JSONObject country = (JSONObject) idTypes.get(combined.get("country").toString());
            
            if (!country.containsKey(combined.get("id_type").toString())) {
                throw new IllegalArgumentException("Invalid value for key id_type");
            }

            JSONArray params = (JSONArray) country.get(combined.get("id_type").toString());
            
            for (int k = 0; k < params.size(); k++) {
                String param = (String) params.get(k);
                
                if (!combined.containsKey(param) ||
                        (combined.get(param) == null && TextUtils.isEmpty(combined.get(param).toString()))) {
                    throw new IllegalArgumentException("Invalid or missing value for key " + param);
                }
            }
        }
    }

    public JSONObject query_smile_id_services() throws Exception {
        JSONObject responseJson = null;

        String smileServicesUrl = (url + "/services").toString();
        HttpClient client = buildHttpClient(connectionTimeout, readTimeout);
        HttpGet httpGet = new HttpGet(smileServicesUrl);

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
        
        return responseJson;
    }

    private JSONObject queryJobStatus(String user_id, String job_id, JSONObject options) throws Exception {
        JSONObject responseJson = null;

        String jobStatusUrl = (url + "/job_status").toString();
        HttpClient client = buildHttpClient(connectionTimeout, readTimeout);
        HttpPost post = new HttpPost(jobStatusUrl);

        String body = configureJobQueryBody(user_id, job_id, options).toString();
        StringEntity entityForPost = new StringEntity(body);

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

            String timestamp = (String) responseJson.get(Signature.TIME_STAMP_KEY);
            String signature = (String) responseJson.get(Signature.SIGNATURE_KEY);
            Signature sigObj = new Signature(partner_id, api_key);
            Boolean valid = false;
            Boolean useSignature = useSignature(options);
            
            if (useSignature) {
            	Long tstmpLng = new SimpleDateFormat(Signature.DATE_TIME_FORMAT).parse(timestamp).getTime();
            	valid = sigObj.confirm_signature(tstmpLng, signature);
            } else {
            	valid = sigObj.confirm_sec_key(timestamp, signature);
            }
            
            if (!valid) {
                throw new IllegalArgumentException("Unable to confirm validity of the job_status response");
            }
        }
        
        return responseJson;
    }

    private JSONObject configureJobQueryBody(String user_id, String job_id, JSONObject options) throws Exception { 
        Long timestamp = System.currentTimeMillis();
        JSONObject body = new JSONObject();
        Boolean returnImages = (Boolean) options.get("return_images");
        Boolean returnHistory = (Boolean) options.get("return_history");
        Boolean useSignature = useSignature(options);
        
        Signature sigObj = new Signature(partner_id, api_key);
        body.put((useSignature) ? Signature.SIGNATURE_KEY : Signature.SEC_KEY, (useSignature) ? sigObj.getSignature(timestamp) : sigObj.getSecKey(timestamp));
        body.put(Signature.TIME_STAMP_KEY, (useSignature) ? new SimpleDateFormat(Signature.DATE_TIME_FORMAT).format(timestamp) : timestamp);
        body.put("partner_id", partner_id);
        body.put("user_id", user_id);
        body.put("job_id", job_id);
        body.put("image_links", returnImages);
        body.put("history", returnHistory);
        
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

    @SuppressWarnings("unchecked")
	private JSONObject fillInJobStatusOptions() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("return_history", false);
        obj.put("return_images", false);
        return obj;
    }

    static HttpClient buildHttpClient(int connectionTimeout, int readTimeout) {

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .setSocketTimeout(readTimeout)
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
    }

    public static Boolean useSignature(JSONObject options){
        if (options.containsKey(Signature.SIGNATURE_KEY)) {
            return (Boolean) options.get(Signature.SIGNATURE_KEY);
        }
        return true;
    }
}
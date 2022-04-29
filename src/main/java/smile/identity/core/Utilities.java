package smile.identity.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.TestOnly;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utilities {

    private static final String PARTNER_ID = "partner_id";
    private static final String USER_ID = "user_id";
    private static final String JOB_ID = "job_id";
    private static final String IMAGE_LINKS = "image_links";
    private static final String HISTORY = "history";
    private static final String RETURN_HISTORY = "return_history";
    private static final String RETURN_IMAGES = "return_images";
    private static final String SOURCE_SDK = "source_sdk";
    private static final String SOURCE_SDK_VERSION = "source_sdk_version";
    
	private String partner_id;
    private String api_key;
    private String url;
    private int connectionTimeout = -1;
    private int readTimeout = -1;

     /**
     * Creates a Utilities object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param api_key the partner-provided API key 
     * 
     * @param sid_server an integer value corresponding to the chosen server
     * 0 for test/sandbox
     * 1 for production
     */
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

    /**
     * Creates a Utilities object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param api_key the partner-provided API key
     * 
     * @param connection_timeout the connection timeout in milliseconds
     * 
     * @param read_timeout the read_timeout the read timeout in milliseconds
     * 
     * @param sid_server an integer value corresponding to the chosen server
     * 0 for test/sandbox
     * 1 for production
     */
    public Utilities(String partner_id, String api_key, String sid_server, int connection_timeout, int read_timeout) {
        this(partner_id, api_key, sid_server);
        this.connectionTimeout = connection_timeout;
        this.readTimeout = read_timeout;
    }
    
    @Deprecated
    public Utilities(String partner_id, String api_key, Integer sid_server) {
        this(partner_id, api_key, String.valueOf(sid_server));
    }

    /***
     * Gets the status of a specific job
     * 
     * @param user_id the supplied user id
     * 
     * @param job_id the supplied id for this job
     * 
     * @return a JSON string containing custom options
     * 
     * @throws ClassCastException
     * 
     * @throws InvalidKeyException
     * 
     * @throws IllegalArgumentException
     * 
     * @throws UnsupportedOperationException
     * 
     * @throws NoSuchAlgorithmException
     * 
     * @throws RuntimeException
     * 
     * @throws ParseException
     * 
     * @throws java.text.ParseException
     * 
     * @throws IOException
     */
    public String get_job_status(String user_id, String job_id, String options) throws ClassCastException, InvalidKeyException, IllegalArgumentException, UnsupportedOperationException, NoSuchAlgorithmException, RuntimeException, ParseException, java.text.ParseException, IOException {
        JSONObject responseJson = getJobStatus(user_id, job_id, options);
        
        String timestamp = (String) responseJson.get(Signature.TIME_STAMP_KEY);
        String signature = (String) responseJson.get(Signature.SIGNATURE_KEY);
        Signature sigObj = new Signature(partner_id, api_key);
        Boolean valid = sigObj.confirm_signature(new SimpleDateFormat(Signature.DATE_TIME_FORMAT).parse(timestamp).getTime(), signature);
        
        if (!valid) {
            throw new IllegalArgumentException("Unable to confirm validity of the job_status response");
        }
        
        return responseJson.toJSONString();
    }

    private JSONObject queryJobStatus(String user_id, String job_id, JSONObject options) throws RuntimeException, ParseException, java.text.ParseException, UnsupportedOperationException, IOException, InvalidKeyException, NoSuchAlgorithmException {
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
        }
        
        return (JSONObject) new JSONParser().parse(strResult);
    }
    
    @SuppressWarnings({ "unchecked", "serial" })
	@TestOnly
    public JSONObject getJobStatus(String user_id, String job_id, String options) throws ClassCastException, InvalidKeyException, IllegalArgumentException, UnsupportedOperationException, NoSuchAlgorithmException, RuntimeException, ParseException, java.text.ParseException, IOException {
    	JSONObject optionsJson = null;

        if (options != null && !options.trim().isEmpty()) {
            optionsJson = (JSONObject) new JSONParser().parse(options);
        } else {
            optionsJson = new JSONObject() {
            	{
            		put(RETURN_HISTORY, false);
            		put(RETURN_IMAGES, false);
            	}
            };
        }

        return queryJobStatus(user_id, job_id, optionsJson);
    }

    /***
     * Validates supplied ID parameters
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param id_info_params a JSON string containing user's specified ID information
     * 
     * @param use_validation_api a BOOLEAN specifying whether API validation is required
     * 
     * @throws IllegalArgumentException
     * 
     * @throws ParseException
     * 
     * @throws RuntimeException
     * 
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "serial" })
	public void validate_id_params(String partner_params, String id_info_params, Boolean use_validation_api) throws IllegalArgumentException, ParseException, RuntimeException, IOException {
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
        
        if (!use_validation_api) {
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

    /***
     * Queries the list of Smile ID services
     * 
     * @throws RuntimeException
     * 
     * @throws ParseException
     * 
     * @throws IOException
     */
    public JSONObject query_smile_id_services() throws RuntimeException, ParseException, IOException {
        String smileServicesUrl = url + "/services";
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
            return (JSONObject) new JSONParser().parse(strResult);
        }
    }

    @SuppressWarnings({ "unchecked", "serial" })
	private JSONObject configureJobQueryBody(String user_id, String job_id, JSONObject options) throws InvalidKeyException, NoSuchAlgorithmException, ParseException { 
        Long timestamp = System.currentTimeMillis();
        Boolean returnImages = (Boolean) options.get(RETURN_IMAGES);
        Boolean returnHistory = (Boolean) options.get(RETURN_HISTORY);
        
        return new JSONObject() {
        	{
        		put(Signature.SIGNATURE_KEY, new Signature(partner_id, api_key).getSignature(timestamp));
        		put(Signature.TIME_STAMP_KEY, new SimpleDateFormat(Signature.DATE_TIME_FORMAT).format(timestamp));
        		put(PARTNER_ID, partner_id);
        		put(USER_ID, user_id);
        		put(JOB_ID, job_id);
        		put(IMAGE_LINKS, returnImages);
        		put(HISTORY, returnHistory);
        		put(SOURCE_SDK, "PHP");
        		put(SOURCE_SDK_VERSION, "2.0.0");
        	}
        };
    }

    private String readHttpResponse(HttpResponse response) throws UnsupportedOperationException, IOException {
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        return result.toString();
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
}
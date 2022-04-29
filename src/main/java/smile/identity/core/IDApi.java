package smile.identity.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IDApi {
	
    private String partner_id;
    private String api_key;
    private String url;
    private String sid_server;

    private int connectionTimeout = -1;
    private int readTimeout = -1;
    
    /**
     * Creates a IDApi object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param api_key the partner-provided API key 
     * 
     * @param sid_server an integer value corresponding to the chosen server
     * 0 for test/sandbox
     * 1 for production
     */
    public IDApi(String partner_id, String api_key, String sid_server) {
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

    /**
     * Creates a IDApi object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param api_key the partner-provided API key
     * 
     *  @param connection_timeout the connection timeout in milliseconds
     * 
     * @param read_timeout the read_timeout the read timeout in milliseconds
     * 
     * @param sid_server an integer value corresponding to the chosen server
     * 0 for test/sandbox
     * 1 for production
     */
    public IDApi(String partner_id, String api_key, String sid_server, int connection_timeout, int read_timeout) {
        this(partner_id, api_key, sid_server);
        this.connectionTimeout = connection_timeout;
        this.readTimeout = read_timeout;
    }

    @Deprecated
    public IDApi(String partner_id, String api_key, Integer sid_server) {
        this(partner_id, api_key, String.valueOf(sid_server));
    }

    /***
     * Submits a job with specified partner parameters and ID information
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param id_info_params a JSON string containing user's specified ID information
     * 
     * @return a string-formatted JSON payload response
     * 
     * @throws ClientProtocolException
     * 
     * @throws IOException
     * 
     * @throws ParseException
     * 
     * @throws GeneralSecurityException
     * 
     * @throws IllegalArgumentException
     * 
     * @throws RuntimeException
     */
    public String submit_job(String partner_params, String id_info_params) throws ClientProtocolException, IOException, ParseException, GeneralSecurityException, IllegalArgumentException, RuntimeException {
        return submit_job(partner_params, id_info_params, false, null) ;
    }

    /***
     * Submits a job with specified partner parameters and ID information
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param id_info_params a JSON string containing user's specified ID information
     * 
     * @param options_params a JSON string containing additional, optional parameters
     * 
     * @return a string-formatted JSON payload response
     * 
     * @throws ClientProtocolException
     * 
     * @throws IOException
     * 
     * @throws ParseException
     * 
     * @throws GeneralSecurityException
     * 
     * @throws IllegalArgumentException
     * 
     * @throws RuntimeException
     */
    public String submit_job(String partner_params, String id_info_params, String options_params) throws ClientProtocolException, IOException, ParseException, GeneralSecurityException, IllegalArgumentException, RuntimeException {
        return submit_job(partner_params, id_info_params, true, options_params);
    }

    /***
     * Submits a job with specified partner parameters and ID information
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param id_info_params a JSON string containing user's specified ID information
     * 
     * @param options_params a JSON string containing additional optional parameters
     * 
     * @return a string-formatted JSON payload response
     * 
     * @throws ClientProtocolException
     * 
     * @throws IOException
     * 
     * @throws ParseException
     * 
     * @throws GeneralSecurityException
     * 
     * @throws IllegalArgumentException
     * 
     * @throws RuntimeException
     */
    public String submit_job(String partner_params, String id_info_params, Boolean useValidationApi) throws ClientProtocolException, IOException, ParseException, GeneralSecurityException, IllegalArgumentException, RuntimeException {
    	return submit_job(partner_params, id_info_params, useValidationApi, null);
    }

    /***
     * Submits a job with specified partner parameters and ID information
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param id_info_params a JSON string containing user's specified ID information
     * 
     * @param use_validation_api boolean value whether special API validation is required
     * 
     * @param options_params a JSON string containing additional optional parameters
     * 
     * @return a string-formatted JSON payload response
     * 
     * @throws ClientProtocolException
     * 
     * @throws IOException
     * 
     * @throws ParseException
     * 
     * @throws GeneralSecurityException
     * 
     * @throws IllegalArgumentException
     * 
     * @throws RuntimeException
     */
    public String submit_job(String partner_params, String id_info_params, Boolean use_validation_api, String options_params) throws ClientProtocolException, IOException, ParseException, GeneralSecurityException, IllegalArgumentException, RuntimeException {

    	JSONParser parser = new JSONParser();
    	JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
        JSONObject idInfo = (JSONObject) parser.parse(id_info_params);

        Long job_type = (Long) partnerParams.get("job_type");
        
        if (job_type != 5) {
            throw new IllegalArgumentException("Please ensure that you are setting your job_type to 5 to query ID Api");
        }
        
        new Utilities(partner_id, api_key, sid_server, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, use_validation_api);

        Long timestamp = System.currentTimeMillis();
        String signature = new Signature(partner_id, api_key).getSignature(timestamp);
        
        return setupRequests(signature, timestamp, partnerParams, idInfo);
    }

    private String setupRequests(String signature, Long timestamp, JSONObject partnerParams, JSONObject idInfo) throws IOException, RuntimeException {
        String strResponse = null;
        
        String idApiUrl = url + "/id_verification";

        HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
        HttpPost post = new HttpPost(idApiUrl.trim());
        JSONObject uploadBody = configureJson(signature, timestamp, partnerParams, idInfo);
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

    @SuppressWarnings({ "unchecked", "serial" })
	private JSONObject configureJson(String signature, Long timestamp, JSONObject partnerParams, JSONObject idInfo) {
        return new JSONObject() {
        	{
        		put(Signature.TIME_STAMP_KEY, new SimpleDateFormat(Signature.DATE_TIME_FORMAT).format(timestamp));
        		put(Signature.SIGNATURE_KEY, signature);
        		put("partner_id", partner_id);
        		put("partner_params", partnerParams);
        		putAll(idInfo);
        		put("source_sdk", "PHP");
        		put("source_sdk_version", "2.0.0");
        	}
        };
    }

    private String readHttpResponse(HttpResponse response) throws IOException {
    	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        return result.toString();
    }
}
package smile.identity.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.TestOnly;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebApi {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(".png", ".jpg", ".jpeg");

    private String partnerId;
    private String apiKey;

    private String url;
    private String sidServer;
    private String callbackUrl;

    private Utilities utilitiesConnection;

    private int connectionTimeout = -1;
    private int readTimeout = -1;
    
    public enum WEB_PRODUCT_TYPE {
    	
    	EKYC_SMART_SELFIE("ekyc_smartselfie"),
    	DOC_VERIFICATION("doc_verification"),
    	AUTHENTICATION("authentication"),
    	SMART_SELFIE("smartselfie"),
    	IDENTITY_VERIFICATION("identity_verification"),
    	ENHANCED_KYC("enhanced_kyc");
    	
    	private String value = "";
    	
    	WEB_PRODUCT_TYPE(String value) {
    		this.value = value;
    	}
    	
    	@Override
    	public String toString() {
    		return this.value;
    	}
    };

    /**
     * Creates a WebApi object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param default_callback the custom's specified default callback url
     * 
     * @param api_key the partner-provided API key 
     * 
     * @param sid_server an integer value corresponding to the chosen server
     * 0 for test/sandbox
     * 1 for production
     */
    public WebApi(String partner_id, String default_callback, String api_key, String sid_server) {
    	this.partnerId = partner_id;
        this.callbackUrl = (default_callback != null) ? default_callback.trim() : "";
        this.apiKey = api_key;
        this.sidServer = sid_server;

        if (sid_server.equals("0")) {
            url = "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
        } else if (sid_server.equals("1")) {
            url = "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
        } else {
            url = sid_server;
        }
    }

    /**
     * Creates a WebApi object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param default_callback the custom's specified default callback url
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
    public WebApi(String partner_id, String default_callback, String api_key, String sid_server, int connectionTimeout, int readTimeout) {
        this(partner_id, default_callback, api_key, sid_server);
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    @Deprecated
    public WebApi(String partner_id, String default_callback, String api_key, Integer sid_server) {
        this(partner_id, default_callback, api_key, String.valueOf(sid_server));
    }

    /***
     * Submits a job with specified partner parameters and ID information
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param images_params a JSON string containing base_64 encode image data
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
     * 
     * @throws java.text.ParseException
     *  
     * @throws InterruptedException 
     */
    public String submit_job(String partner_params, String images_params, String id_info_params, String options_params) throws IllegalArgumentException, ParseException, RuntimeException, IOException, GeneralSecurityException, InterruptedException, java.text.ParseException {
        return submit_job(partner_params, images_params, id_info_params, options_params, true);
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
     * 
     * @throws java.text.ParseException
     *  
     * @throws InterruptedException 
     */
    @SuppressWarnings({ "unchecked", "serial" })
	public String submit_job(String partner_params, String images_params, String id_info_params, String options_params, Boolean use_validation_api) throws IllegalArgumentException, ParseException, RuntimeException, IOException, GeneralSecurityException, InterruptedException, java.text.ParseException {
    	JSONParser parser = new JSONParser();
        JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
        JSONObject idInfo;
        
        if (id_info_params != null && !id_info_params.trim().isEmpty()) {
            idInfo = (JSONObject) parser.parse(id_info_params);
        } else {
            idInfo = new JSONObject() {
            	{
            		put("entered", "false");
            	}
            };
        }

        Long job_type = (Long) partnerParams.get("job_type"); 
        
        if (job_type == 5) {
            new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, use_validation_api);
            return callIDApi(partnerParams, idInfo, options_params);
        }

        JSONArray images = (JSONArray) parser.parse(images_params);

        validateImages(images);

        if (job_type == 1) {
            new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, use_validation_api);
            validateEnrollWithId(images, idInfo);
        }

        JSONObject options = extractOptions(options_params, parser);
        validateReturnData((Boolean) options.get("return_job_status"));

        return setupRequests(partnerParams, options, idInfo, images);
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
     * @throws IllegalArgumentException
     * 
     * @throws ClientProtocolException
     * 
     * @throws IOException
     * 
     * @throws ParseException
     * 
     * @throws GeneralSecurityException
     * 
     * @throws RuntimeException
     */
    @SuppressWarnings("null")
	public String submit_job(String partner_params, String id_info_params) throws IllegalArgumentException, ParseException, ClientProtocolException, IOException, GeneralSecurityException, RuntimeException {
    	JSONParser parser = new JSONParser();
        JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
        JSONObject idInfo;
        
        if (id_info_params == null && id_info_params.trim().isEmpty()) {
            throw new IllegalArgumentException("You need to send through a vaild ID Information payload");
        } else {
            idInfo = (JSONObject) parser.parse(id_info_params);
        }

        Long job_type = (Long) partnerParams.get("job_type");
        
        if (job_type == 5) {
            return callIDApi(partnerParams, idInfo, null);
        } else {
            throw new IllegalArgumentException("You need to send through more parameters");
        }
    }

    /***
     * Gets the status of a specific job
     * 
     * @param partner_params a JSON string containing partner's specified parameters
     * 
     * @param options_params a JSON string containing additional, optional parameters
     * 
     * @return a JSON string containing custom options
     * 
     * @throws ParseException
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
     * @throws java.text.ParseException 
     * 
     * @throws IOException
     */
    public String get_job_status(String partner_params, String options) throws ParseException, InvalidKeyException, IllegalArgumentException, UnsupportedOperationException, NoSuchAlgorithmException, RuntimeException, java.text.ParseException, IOException {
        JSONObject partnerParams = (JSONObject) new JSONParser().parse(partner_params);

        String user_id = (String) partnerParams.get("user_id");
        String job_id = (String) partnerParams.get("job_id");

        Utilities utilities = new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout);
        return utilities.get_job_status(user_id, job_id, options);
    }
    
    /***
     * Queries the backend for web session token with a specific timestamp
     * 
     * @param timestamp the timestamp to generate the token for
     * 
     * @param user_id the supplied user id
     * 
     * @param job_id the supplied id for this job
     *  
     * @param product_type literal value of any of the 6 options specified by the WEB_PRODUCT_TYPE enum
     * 
     * @return A stringified JSONObject containing the returned token
     * 
     * @throws ParseException 
     * 
     * @throws NoSuchAlgorithmException
     *  
     * @throws InvalidKeyException
     * 
     * @throws IOException
     *  
     * @throws ClientProtocolException 
     */
    @SuppressWarnings({ "unchecked", "serial" })
	public String get_web_token(Long timestamp, String user_id, String job_id, String product_type) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, ClientProtocolException, IOException {
    	String url = this.url + "/token";
    	HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
    	HttpPost post = new HttpPost(url.trim());
    	
    	JSONObject uploadBody = new JSONObject() {
    		{
    			put(Signature.TIME_STAMP_KEY, new SimpleDateFormat(Signature.DATE_TIME_FORMAT).format(timestamp));
    			put("callback_url", callbackUrl);
    			put("partner_id", partnerId);
    			put("user_id", user_id);
    			put("job_id", job_id);
    			put("product", product_type);
    			put(Signature.SIGNATURE_KEY, new Signature(partnerId, apiKey).getSignature(timestamp));
        		put("source_sdk", "PHP");
        		put("source_sdk_version", "2.0.0");
    		}
    	};
        
        StringEntity entityForPost = new StringEntity(uploadBody.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(entityForPost);

        HttpResponse response = client.execute(post);
        
        return ((JSONObject) new JSONParser().parse(readHttpResponse(response))).toString();
    }
    
    @TestOnly
    public String getCallbackUrl() {
    	return callbackUrl;
    }
    
    @TestOnly
    public String getPartnerId() {
    	return partnerId;
    }

    private String callIDApi(JSONObject partnerParams, JSONObject idInfo, String options_params) throws ClientProtocolException, IllegalArgumentException, IOException, ParseException, GeneralSecurityException, RuntimeException {
        IDApi connection = new IDApi(partnerId, apiKey, sidServer, connectionTimeout, readTimeout);
        return connection.submit_job(partnerParams.toString(), idInfo.toString(), options_params);
    }

    private void validateImages(JSONArray images) throws IllegalArgumentException {
        if (images == null || images.size() < 1) {
            throw new IllegalArgumentException("You need to send through at least one selfie image");
        }
    }

    private void validateEnrollWithId(JSONArray images, JSONObject idInfo) throws IllegalArgumentException {
        String entered = (String) ((JSONObject) idInfo).get("entered");
        Integer counter = 0;
        
        for (Object o : images) {
            Long image_type_id = (Long) ((JSONObject) o).get("image_type_id");

            if ((image_type_id == 1) || (image_type_id == 3) || (entered.equals("true"))) {
                counter = counter + 1;
            }
        }

        if (counter < 1) {
            throw new IllegalArgumentException("You are attempting to complete a job type 1 without providing an id card image or id info");
        }
    }

    private void validateReturnData(boolean returnJobStatus) throws IllegalArgumentException {
        if (this.callbackUrl.trim().isEmpty() && returnJobStatus == false) {
            throw new IllegalArgumentException("Please choose to either get your response via the callback or job status query");
        }
    }

    @SuppressWarnings({ "unchecked", "serial" })
	private JSONObject extractOptions(String options_params, JSONParser parser) throws ParseException {

        JSONObject options;
        
        if (options_params != null && !options_params.trim().isEmpty()) {
            options = (JSONObject) parser.parse(options_params);
            
            if (options.containsKey("optional_callback")) {
                String optionalCallBack = (String) options.get("optional_callback");
                
                if (optionalCallBack != null && optionalCallBack.trim().isEmpty()) {
                    options.put("optional_callback", options.containsKey("optional_callback"));
                }
            }
        } else {
            options = new JSONObject() {
            	{
            		put("return_job_status", false);
            		put("return_history", false);
            		put("return_images", false);
            	}
            };
        }
        
        return options;
    }

    @SuppressWarnings({ "serial", "unchecked" })
	private String setupRequests(JSONObject partnerParams, JSONObject options, JSONObject idInfo, JSONArray images) throws InvalidKeyException, IllegalArgumentException, UnsupportedOperationException, NoSuchAlgorithmException, ParseException, InterruptedException, RuntimeException, java.text.ParseException, IOException {
        Long timestamp = System.currentTimeMillis();
        
        String signature = new Signature(partnerId, apiKey).getSignature(timestamp);
        String prepUploadUrl = url + "/upload";

        HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
        HttpPost post = new HttpPost(prepUploadUrl.trim());
        JSONObject uploadBody = configurePrepUploadJson(signature, timestamp, partnerParams);
        StringEntity entityForPost = new StringEntity(uploadBody.toString());
        post.setHeader("content-type", "application/json");
        post.setEntity(entityForPost);

        HttpResponse response = client.execute(post);
        final int statusCode = response.getStatusLine().getStatusCode();
        String strResult = readHttpResponse(response);

        if (statusCode != 200) {
            final String msg = String.format("Failed to post entity to %s, response=%d:%s - %s",
                    prepUploadUrl, statusCode, response.getStatusLine().getReasonPhrase(), strResult);
            throw new RuntimeException(msg);
        } else {
            JSONParser parser = new JSONParser();
            JSONObject responseJson = (JSONObject) parser.parse(strResult);
            String uploadUrl = responseJson.get("upload_url").toString();
            String smileJobId = responseJson.get("smile_job_id").toString();

            JSONObject infoJson = configureInfoJson(uploadUrl, signature, timestamp, partnerParams, idInfo, images);
            ByteArrayOutputStream baos = zipUpFile(infoJson, images);
            uploadFile(uploadUrl, baos);
            
            if ((Boolean) options.get("return_job_status") == true) {
                Utilities utilitiesConnection = new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout);
                this.utilitiesConnection = utilitiesConnection;

                Integer counter = 0;
                JSONObject jsonJobStatusResponse = pollJobStatus(counter, partnerParams, options);
                jsonJobStatusResponse.put("success", true);
                jsonJobStatusResponse.put("smile_job_id", smileJobId);

                return jsonJobStatusResponse.toString();
            } else {
            	return new JSONObject() {
                	{
                		put("success", true);
                		put("smile_job_id", smileJobId);
                	}
                }.toString();
            }
        }
    }

    @SuppressWarnings({ "unchecked", "serial" })
	private JSONObject configurePrepUploadJson(String signature, Long timestamp, JSONObject partnerParams) {
        return new JSONObject() {
        	{
        		put("file_name", "selfie.zip");
        		put("timestamp", timestamp);
        		put(Signature.SIGNATURE_KEY, signature);
        		put("smile_client_id", partnerId);
        		put("partner_params", partnerParams);
        		put("model_parameters", new JSONObject());
        		put("callback_url", callbackUrl);
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

    @SuppressWarnings({ "unchecked", "serial", "deprecation" })
	private JSONObject configureInfoJson(String uploadUrl, String signature, Long timestamp, JSONObject partnerParams, JSONObject idInfo, JSONArray images) {
        JSONObject api_version = new JSONObject() {
        	{
        		put("buildNumber", new Integer(0));
        		put("majorVersion", new Integer(2));
        		put("minorVersion", new Integer(0));
        	}
        };

        JSONObject package_information = new JSONObject() {
        	{
        		put("apiVersion", api_version);
        		put("language", "java");
        	}
        };

        JSONObject userData = new JSONObject() {
        	{
        		put("isVerifiedProcess", new Boolean(false));
        		put("name", "");
        		put("fbUserID", "");
        		put("firstName", "Bill");
        		put("lastName", "");
        		put("gender", "");
        		put("email", "");
        		put("phone", "");
        		put("countryCode", "+");
        		put("countryName", "");
        	}
        };

        JSONObject misc_information = new JSONObject() {
        	{
        		put(Signature.SIGNATURE_KEY, signature);
        		put("retry", "false");
        		put("partner_params", partnerParams);
        		put("timestamp", timestamp);
        		put("file_name", "selfie.zip");
        		put("smile_client_id", partnerId);
        		put("callback_url", callbackUrl);
        		put("userData", userData);
        	}
        };

        return new JSONObject() {
        	{
        		put("package_information", package_information);
        		put("misc_information", misc_information);
        		put("id_info", idInfo);
        		put("images", configureImagePayload(images));
        		put("server_information", uploadUrl);
        	}
        };
    }

    @SuppressWarnings("unchecked")
	private JSONArray configureImagePayload(JSONArray images) {
        JSONArray imagePayload = new JSONArray();

        for (Object o : images) {
            JSONObject imageObject = new JSONObject();
            
            if (o instanceof JSONObject) {
                imageObject.put("image_type_id", ((JSONObject) o).get("image_type_id"));

                String imageType = (String) ((JSONObject) o).get("image");

                if (SUPPORTED_IMAGE_TYPES.stream().anyMatch(imageType::endsWith)) {
                    imageObject.put("image", "");
                    String filePath = ((JSONObject) o).get("image").toString();
                    String fileName = new File(filePath).getName();
                    imageObject.put("file_name", fileName);
                } else {
                    imageObject.put("image", ((JSONObject) o).get("image"));
                    imageObject.put("file_name", "");
                }
            }
            
            imagePayload.add(imageObject);
        }
        
        return imagePayload;
    }

    private ByteArrayOutputStream zipUpFile(JSONObject infoJson, JSONArray images) throws IOException {
        // http://www.avajava.com/tutorials/lessons/how-can-i-create-a-zip-file-from-a-set-of-files.html
        // https://stackoverflow.com/questions/23612864/create-a-zip-file-in-memory
        ByteArrayOutputStream baos = null;
        
        baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        ZipEntry entry = new ZipEntry("info.json");
        zos.putNextEntry(entry);
        zos.write(infoJson.toString().getBytes());
        zos.closeEntry();

        for (Object o : images) {
            if (o instanceof JSONObject) {
                String imageType = (String) ((JSONObject) o).get("image");
                
                if (SUPPORTED_IMAGE_TYPES.stream().anyMatch(imageType::endsWith)) {
                    String fileName = ((JSONObject) o).get("image").toString();
                    File file = new File(fileName);
                    FileInputStream fis = new FileInputStream(file);

                    ZipEntry imageEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(imageEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }

                    zos.closeEntry();
                    baos.close();
                    fis.close();
                }
            }
        }
        
        zos.flush();
        zos.close();
        
        return baos;
    }

    private void uploadFile(String awsUrl, ByteArrayOutputStream baos) throws RuntimeException, ClientProtocolException, IOException {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut putRequest = new HttpPut(awsUrl.trim().toString());
        putRequest.setHeader("content-type", "application/zip");
        putRequest.setEntity(new ByteArrayEntity(baos.toByteArray()));

        HttpResponse response = httpclient.execute(putRequest);
        final int statusCode = response.getStatusLine().getStatusCode();
        String strResult = readHttpResponse(response);

        if (statusCode != 200) {
            final String msg = String.format("Failed to post entity to %s, response=%d:%s - %s",
                    awsUrl, statusCode, response.getStatusLine().getReasonPhrase(), strResult);
            throw new RuntimeException(msg);
        }
    }

    private JSONObject pollJobStatus(int counter, JSONObject partnerParams, JSONObject options) throws ParseException, InterruptedException, InvalidKeyException, IllegalArgumentException, UnsupportedOperationException, NoSuchAlgorithmException, RuntimeException, java.text.ParseException, IOException {
        Boolean job_complete = false;
        JSONObject responseJson = null;
        String responseStr = null;

        counter = counter + 1;
        Thread.sleep((counter < 4) ? 2000 : 4000);

        String user_id = (String) partnerParams.get("user_id");
        String job_id = (String) partnerParams.get("job_id");
        boolean returnHistory = (Boolean) options.get("return_history");
        boolean returnImages = (Boolean) options.get("return_images");

        String jobStatusOptions = new Options(returnHistory, returnImages).get();
        responseStr = utilitiesConnection.get_job_status(user_id, job_id, jobStatusOptions);

        responseJson = (JSONObject) new JSONParser().parse(responseStr);

        job_complete = (Boolean) responseJson.get("job_complete");
        
        if (!job_complete && counter < 20) {
            responseJson = pollJobStatus(counter, partnerParams, options);
        }
        
        return responseJson;
    }
}
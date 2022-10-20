package smile.identity.core;

//export package -tbd
//package com.smileidentity.services.WebApi

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.TestOnly;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// json converter
// apache http client
// zip file

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

    @Deprecated
    public WebApi(String partner_id, String default_callback, String api_key, Integer sid_server) {
        this(partner_id, default_callback, api_key, String.valueOf(sid_server));
    }

    public WebApi(String partner_id, String default_callback, String api_key, String sid_server) {
    	this.partnerId = partner_id;
        //TODO:
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

    public WebApi(String partner_id, String default_callback, String api_key, String sid_server, int connectionTimeout, int readTimeout) {
        this(partner_id, default_callback, api_key, sid_server);
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public String submit_job(String partner_params, String images_params, String id_info_params, String options_params) throws Exception {
        return submit_job(partner_params, images_params, id_info_params, options_params, true);
    }

    public String submit_job(String partner_params, String images_params, String id_info_params, String options_params, Boolean useValidationApi) throws Exception {
    	JSONParser parser = new JSONParser();
        JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
        JSONObject idInfo;
        
        if (id_info_params != null && !id_info_params.trim().isEmpty()) {
            idInfo = (JSONObject) parser.parse(id_info_params);
        } else {
            idInfo = fillInIdInfo();
        }

        Long job_type = (Long) partnerParams.get("job_type"); 
        
        if (job_type == 5) {
            new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, useValidationApi);
            return callIDApi(partnerParams, idInfo, options_params);
        }

        JSONArray images = (JSONArray) parser.parse(images_params);

        validateImages(images);

        if (job_type == 1) {
            new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout).validate_id_params(partner_params, id_info_params, useValidationApi);
            validateEnrollWithId(images, idInfo);
        }

        JSONObject options = extractOptions(options_params, parser);
        validateReturnData((Boolean) options.get("return_job_status"));

        return setupRequests(partnerParams, options, idInfo, images);
    }

    public String submit_job(String partner_params, String id_info_params) throws Exception {
    	String response = null;
        
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
            response = callIDApi(partnerParams, idInfo, null);
        } else {
            throw new IllegalArgumentException("You need to send through more parameters");
        }
        
        return response;
    }

    public String get_job_status(String partner_params, String options) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject partnerParams = (JSONObject) parser.parse(partner_params);

        String user_id = (String) partnerParams.get("user_id");
        String job_id = (String) partnerParams.get("job_id");

        Utilities utilities = new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout);
        return utilities.get_job_status(user_id, job_id, options);
    }
    
    /***
     *  Will query the backend for web session token with a specific timestamp
     * @param timestamp the timestamp to generate the token from
     * @param user_id
     * @param job_id
     * @param product_type - Literal value of any of the 6 options specified by the WEB_PRODUCT_TYPE enum
     * @return A stringified JSONObject containing the returned token
     */
    @SuppressWarnings("unchecked")
	public String get_web_token(Long timestamp, String user_id, String job_id, String product_type) throws Exception {
    	String url = this.url + "/token";
    	HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
    	HttpPost post = new HttpPost(url.trim());
    	
    	JSONObject uploadBody = new JSONObject();
    	uploadBody.put(Signature.TIME_STAMP_KEY, new SimpleDateFormat(Signature.DATE_TIME_FORMAT).format(timestamp));
    	uploadBody.put("callback_url", callbackUrl);
    	uploadBody.put("partner_id", partnerId);
    	uploadBody.put("user_id", user_id);
    	uploadBody.put("job_id", job_id);
    	uploadBody.put("product", product_type);
    	uploadBody.put(Signature.SIGNATURE_KEY, new Signature(partnerId, apiKey).getSignature(timestamp));
        
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

    private String callIDApi(JSONObject partnerParams, JSONObject idInfo, String options_params) throws Exception {
        IDApi connection = new IDApi(partnerId, apiKey, sidServer, connectionTimeout, readTimeout);
        return connection.submit_job(partnerParams.toString(), idInfo.toString(), options_params);
    }

    private void validateImages(JSONArray images) throws IllegalArgumentException {
        if (images == null || images.size() < 1) {
            throw new IllegalArgumentException("You need to send through at least one selfie image");
        }
    }

    private void validateEnrollWithId(JSONArray images, JSONObject idInfo) throws Exception {
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

    private void validateReturnData(boolean returnJobStatus) throws Exception {
        if (this.callbackUrl.trim().isEmpty() && returnJobStatus == false) {
            throw new IllegalArgumentException("Please choose to either get your response via the callback or job status query");
        }
    }

    private JSONObject extractOptions(String options_params, JSONParser parser) throws Exception {

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
            options = new JSONObject();
            options.put("return_job_status", false);
            options.put("return_history", false);
            options.put("return_images", false);
        }
        
        return options;
    }

    private JSONObject fillInIdInfo() throws Exception {
        JSONObject obj = new JSONObject();
        
        try {
            obj.put("entered", "false");
        } catch (Exception e) {
            throw e;
        }

        return obj;
    }

    private String setupRequests(JSONObject partnerParams, JSONObject options, JSONObject idInfo, JSONArray images) throws Exception {
        String res = null;
        Long timestamp = System.currentTimeMillis();
        Boolean useSignature = false;
        Signature sigObj = new Signature(partnerId, apiKey);
        
        if (options.containsKey(Signature.SIGNATURE_KEY)) {
        	useSignature = (Boolean) options.get(Signature.SIGNATURE_KEY);
        }
        
        String signature = (useSignature) ? sigObj.getSignature(timestamp) : sigObj.getSecKey(timestamp);
        String prepUploadUrl = url + "/upload";

        HttpClient client = Utilities.buildHttpClient(connectionTimeout, readTimeout);
        HttpPost post = new HttpPost(prepUploadUrl.trim());
        JSONObject uploadBody = configurePrepUploadJson(signature, timestamp, partnerParams, useSignature, options);
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

            JSONObject infoJson = configureInfoJson(uploadUrl, signature, timestamp, partnerParams, idInfo, images, useSignature);
            ByteArrayOutputStream baos = zipUpFile(infoJson, images);
            uploadFile(uploadUrl, baos);
            
            if ((Boolean) options.get("return_job_status") == true) {
                Utilities utilitiesConnection = new Utilities(partnerId, apiKey, sidServer, connectionTimeout, readTimeout);
                this.utilitiesConnection = utilitiesConnection;

                Integer counter = 0;
                JSONObject jsonJobStatusResponse = pollJobStatus(counter, partnerParams, options);
                jsonJobStatusResponse.put("success", true);
                jsonJobStatusResponse.put("smile_job_id", smileJobId);

                String jobStatusResponse = jsonJobStatusResponse.toString();

                res = jobStatusResponse;
            } else {
                JSONObject successResponse = new JSONObject();
                successResponse.put("success", true);
                successResponse.put("smile_job_id", smileJobId);
                res = successResponse.toString();
            }
        }
        
        return res;
    }

    private JSONObject configurePrepUploadJson(String signature, Long timestamp, JSONObject partnerParams, Boolean useSignature, JSONObject options) throws Exception {
        JSONObject body = new JSONObject();
        body.put("file_name", "selfie.zip");
        body.put("timestamp", timestamp);
        body.put((useSignature) ? Signature.SIGNATURE_KEY : Signature.SEC_KEY, signature);
        body.put("smile_client_id", partnerId);
        body.put("partner_params", partnerParams);
        body.put("model_parameters", new JSONObject());
        body.put("callback_url", options.containsKey("optional_callback") ? options.get("optional_callback") : callbackUrl);
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

    private JSONObject configureInfoJson(String uploadUrl, String signature, Long timestamp, JSONObject partnerParams, JSONObject idInfo, JSONArray images, Boolean useSignature) throws Exception {
        JSONObject json = new JSONObject();
        
        JSONObject api_version = new JSONObject();
        api_version.put("buildNumber", new Integer(0));
        api_version.put("majorVersion", new Integer(2));
        api_version.put("minorVersion", new Integer(0));

        JSONObject package_information = new JSONObject();
        package_information.put("apiVersion", api_version);
        package_information.put("language", "java");

        JSONObject userData = new JSONObject();
        userData.put("isVerifiedProcess", new Boolean(false));
        userData.put("name", "");
        userData.put("fbUserID", "");
        userData.put("firstName", "Bill");
        userData.put("lastName", "");
        userData.put("gender", "");
        userData.put("email", "");
        userData.put("phone", "");
        userData.put("countryCode", "+");
        userData.put("countryName", "");

        JSONObject misc_information = new JSONObject();
        misc_information.put((useSignature) ? Signature.SIGNATURE_KEY : Signature.SEC_KEY, signature);
        misc_information.put("retry", "false");
        misc_information.put("partner_params", partnerParams);
        misc_information.put("timestamp", timestamp);
        misc_information.put("file_name", "selfie.zip");
        misc_information.put("smile_client_id", partnerId);
        misc_information.put("callback_url", callbackUrl);
        misc_information.put("userData", userData);

        json.put("package_information", package_information);
        json.put("misc_information", misc_information);
        json.put("id_info", idInfo);
        json.put("images", configureImagePayload(images));
        json.put("server_information", uploadUrl);
        
        return json;
    }

    private JSONArray configureImagePayload(JSONArray images) {
        JSONArray imagePayload = new JSONArray();

        for (Object o : images) {
            JSONObject imageObject = new JSONObject();
            
            if (o instanceof JSONObject) {
                Long image_type_id = (Long) ((JSONObject) o).get("image_type_id");
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

    private ByteArrayOutputStream zipUpFile(JSONObject infoJson, JSONArray images) throws Exception {
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

    private void uploadFile(String awsUrl, ByteArrayOutputStream baos) throws Exception {
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

    private JSONObject pollJobStatus(int counter, JSONObject partnerParams, JSONObject options) throws Exception {
        Boolean job_complete = false;
        Boolean useSignature = (Boolean) options.get(Signature.SIGNATURE_KEY);
        JSONObject responseJson = null;
        String responseStr = null;

        counter = counter + 1;
        
        if (counter < 4) {
            Thread.sleep(2000);
        } else {
            Thread.sleep(4000);
        }

        String user_id = (String) partnerParams.get("user_id");
        String job_id = (String) partnerParams.get("job_id");
        boolean returnHistory = (Boolean) options.get("return_history");
        boolean returnImages = (Boolean) options.get("return_images");

        String jobStatusOptions = new Options(returnHistory, returnImages).get();
        responseStr = utilitiesConnection.get_job_status(user_id, job_id, jobStatusOptions);

        JSONParser parser = new JSONParser();
        responseJson = (JSONObject) parser.parse(responseStr);

        job_complete = (Boolean) responseJson.get("job_complete");
        
        if (!job_complete && counter < 20) {
            responseJson = pollJobStatus(counter, partnerParams, options);
        }
        
        return responseJson;
    }
}
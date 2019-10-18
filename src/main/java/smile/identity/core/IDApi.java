package smile.identity.core;

import java.util.HashMap;
import java.util.Map;

// json converter
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

// apache http client
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class IDApi {
  private String partner_id;
  private String api_key;

  private JSONObject partnerParams;
  private JSONObject idInfo;

  private String url;
  private Integer sid_server;
  private String sec_key;
  private long timestamp;

  public IDApi(String partner_id, String api_key, Integer sid_server) throws Exception {
    try {
      this.partner_id = partner_id.toString();
      this.api_key = api_key;

      if(sid_server == 0) {
        url = "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
      } else if (sid_server == 1) {
        url = "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
      }

      this.sid_server = sid_server;
      this.url = url;

    } catch (Exception e) {
      throw e;
    }
  }

  public String submit_job(String partner_params, String id_info_params) throws Exception {

    String response = null;
    try {
      JSONParser parser = new JSONParser();
      JSONObject partnerParams = (JSONObject) parser.parse(partner_params);
      JSONObject idInfo = (JSONObject) parser.parse(id_info_params);

      Long job_type = (Long) partnerParams.get("job_type");
      if(job_type != 5) {
        throw new IllegalArgumentException("Please ensure that you are setting your job_type to 5 to query ID Api");
      }

      this.partnerParams = partnerParams;
      this.idInfo = idInfo;

      this.timestamp = System.currentTimeMillis();
      this.sec_key = determineSecKey();

      response = setupRequests();
    } catch (Exception e) {
      throw e;
    }

    return response;
  }

  private String setupRequests() throws Exception {
    String strResponse = null;
    try {
      String idApiUrl = url + "/id_verification";

      HttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost(idApiUrl.trim());
      JSONObject uploadBody = configureJson();
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
    } catch(Exception e) {
      throw e;
    }
    return strResponse;
  }

  private JSONObject configureJson() throws Exception {
    JSONObject body = new JSONObject();

    try {
      body.put("timestamp", timestamp);
      body.put("sec_key", sec_key);
      body.put("partner_id", partner_id);
      body.put("partner_params", partnerParams);
      body.putAll(idInfo);
    } catch(Exception e) {
      throw e;
    }
    return body;
  }

  private String determineSecKey() throws Exception {
    Signature connection = new Signature(partner_id, api_key);
    String secKey = "";
    JSONParser parser = new JSONParser();

    try {
      String signatureJsonStr = connection.generate_sec_key(timestamp);

      JSONObject signature = (JSONObject) parser.parse(signatureJsonStr);
      secKey = (String) signature.get("sec_key");
    } catch(Exception e) {
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

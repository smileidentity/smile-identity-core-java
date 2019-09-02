package smile.identity.core;

// json converter;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

// apache http client
import java.io.BufferedReader;
import java.io.InputStreamReader;

// apache http client
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class Utilities {

  private String partner_id;
  private String api_key;
  private String url;
  private String sec_key;
  private long timestamp;

  public Utilities(String partner_id, String api_key, Integer sid_server) {
    try {
      this.partner_id = partner_id.toString();
      this.api_key = api_key;

      if(sid_server == 0) {
        url = "https://3eydmgh10d.execute-api.us-west-2.amazonaws.com/test";
      } else if (sid_server == 1) {
        url = "https://la7am6gdm8.execute-api.us-west-2.amazonaws.com/prod";
      }

      this.url = url;
    } catch (Exception e) {
      throw e;
    }
  }

  public String get_job_status(String user_id, String job_id, Boolean returnImages, Boolean returnHistory) throws Exception {
    this.timestamp = System.currentTimeMillis();
    this.sec_key = determineSecKey();

    return queryJobStatus(user_id, job_id, returnImages, returnHistory).toString();
  }

  private JSONObject queryJobStatus(String user_id, String job_id, Boolean returnImages, Boolean returnHistory) throws Exception {
    Boolean job_complete = false;
    JSONObject responseJson = null;

    String jobStatusUrl = (url + "/job_status").toString();
    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost(jobStatusUrl);

    try {
      StringEntity entityForPost = new StringEntity(configureJobQueryBody(user_id, job_id, returnImages, returnHistory).toString());
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
        if(!valid) {
          throw new IllegalArgumentException("Unable to confirm validity of the job_status response");
        }
      }
    } catch (Exception e) {
      throw e;
    }
    return responseJson;
  }

  private JSONObject configureJobQueryBody(String user_id, String job_id, Boolean returnImages, Boolean returnHistory) throws Exception {
    JSONObject body = new JSONObject();
    try {
      body.put("sec_key", determineSecKey());
      body.put("timestamp", timestamp);
      body.put("partner_id", partner_id);
      body.put("user_id", user_id);
      body.put("job_id", job_id);
      body.put("image_links", returnImages);
      body.put("history", returnHistory);
    } catch(Exception e) {
      throw e;
    }
    return body;
  }


  // these two methods are common across web api, we could put it in a helper class but it would mean that the functions are public
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

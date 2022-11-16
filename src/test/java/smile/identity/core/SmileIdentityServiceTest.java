package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.Test;
import smile.identity.core.models.*;
import smile.identity.core.exceptions.JobFailed;
;

import static org.junit.Assert.*;

public class SmileIdentityServiceTest {

    private final String BASE_PATH = "testing/";

    @Test
    public void submitsIdVerificationJob() throws Exception {
        try(MockWebServer server = new MockWebServer()){
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());

            JobResponse jobResponse = new JobResponse();
            jobResponse.setResultType("ID Verification");
            jobResponse.setSignature("signature");
            jobResponse.setSmileJobId("jobid");

            JsonAdapter<JobResponse> adaptor = new Moshi.Builder().build().adapter(JobResponse.class);

            server.enqueue(new MockResponse().setBody(adaptor.toJson(jobResponse)));
            JobResponse result = service.idVerification(new EnhancedKYCRequest());
            assertEquals(result.getSignature(), "signature");
            assertEquals(result.getSmileJobId(), "jobid");
            assertEquals(result.getClass(), EnhancedResponse.class);
            server.shutdown();

        }
    }

    @Test
    public void submitIdVerificationFails() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            server.enqueue(new MockResponse().setResponseCode(400));

            assertThrows(JobFailed.class, ()->  service.idVerification(new EnhancedKYCRequest()));
            server.shutdown();
        }
    }

    @Test
    public void submitsPreUpload() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            String responseString = "{\"smile_job_id\": \"123232121\", \"upload_url\": \"photos.com\"}";
            server.enqueue(new MockResponse().setBody(responseString));
            PreUploadResponse response = service.preUpload(new PreUploadRequest());

            assertEquals(response.getSmileJobId(), "123232121");
            assertEquals(response.getUploadUrl(), "photos.com");
            server.shutdown();
        }
    }

    @Test
    public void submitPreUploadFails() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            server.enqueue(new MockResponse().setResponseCode(400));

            assertThrows(JobFailed.class, () -> service.preUpload(new PreUploadRequest()));
            server.shutdown();
        }

    }

    @Test
    public void uploadImagesToCorrectUrl() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());

            server.enqueue(new MockResponse());
            service.uploadImages("callback_url", new byte[1024]);
            RecordedRequest recordedRequest = server.takeRequest();

            assertEquals(recordedRequest.getRequestUrl().toString(), baseUrl + "callback_url");

            server.shutdown();

        }

    }


    @Test
    public void getServices() throws Exception{
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            String response = "{\"id_types\":{\"GH\":{\"SSNIT\":[\"name\"]}}}";
            server.enqueue(new MockResponse().setBody(response));

            String services = service.getServices();
            assertEquals(services, response);

            server.shutdown();
        }
    }

    @Test
    public void getJobStatus() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());

            JobStatusResponse statusResponse = new JobStatusResponse();
            statusResponse.setSignature("signature");
            JobResponse result = new JobResponse();
            result.setResultType("Great Job");
            result.setConfidence("99.999");
            statusResponse.setResult(result);

            JsonAdapter<JobStatusResponse> adapter = new Moshi.Builder().build().adapter(JobStatusResponse.class);

            server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
            JobStatusResponse response = service.getJobStatus(new JobStatusRequest());

            assertEquals(response.getResult().getClass(), JobResponse.class);
            server.shutdown();
        }
    }

    @Test
    public void getJobStatusEnhanced() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());

            JobStatusResponse statusResponse = new JobStatusResponse();
            EnhancedResponse result = new EnhancedResponse();
            result.setSmileJobId("12345");
            result.setResultType("ID Verification");
            statusResponse.setResult(result);

            JsonAdapter<JobStatusResponse> adapter = new Moshi.Builder().build().adapter(JobStatusResponse.class);

            server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
            JobStatusResponse response = service.getJobStatus(new JobStatusRequest());

            assertEquals(response.getResult().getClass(), EnhancedResponse.class);
            server.shutdown();
        }
    }


    @Test
    public void pollJobStatus() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            String bad = "{\"job_complete\": false, \"signature\": \"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\", \"timestamp\": \"2022-11-10T19:05:42.775Z\"}";
            String success = "{\"job_complete\": true, \"signature\": \"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\", \"timestamp\": \"2022-11-10T19:05:42.775Z\"}";

            server.enqueue(new MockResponse().setBody(bad));
            server.enqueue(new MockResponse().setBody(bad));
            server.enqueue(new MockResponse().setBody(bad));
            server.enqueue(new MockResponse().setBody(success));
            JobStatusResponse response = service.pollJobStatus(new JobStatusRequest());
            assertTrue(response.isJobComplete());
            server.shutdown();
        }
    }

    @Test
    public void pollJobStatusReturnsUncompleted() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            String bad = "{\"job_complete\": false, \"signature\": \"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\", \"timestamp\": \"2022-11-10T19:05:42.775Z\"}";
            for(int i = 0; i <= 10; i++){
                server.enqueue(new MockResponse().setBody(bad));
            }

            JobStatusResponse response = service.pollJobStatus(new JobStatusRequest());
            assertFalse(response.isJobComplete());
            server.shutdown();
        }
    }

    @Test
    public void getsWebToken() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            WebTokenResponse tokenResponse = new WebTokenResponse();
            tokenResponse.setToken("heresatoken");
            tokenResponse.setSuccess(true);
            JsonAdapter<WebTokenResponse> adapter = new Moshi.Builder().build().adapter(WebTokenResponse.class);
            server.enqueue(new MockResponse().setBody(adapter.toJson(tokenResponse)));

            WebTokenResponse response = service.getWebToken(new WebTokenRequest());
            assertTrue(response.isSuccess());
            assertEquals(response.getToken(), "heresatoken");
            server.shutdown();
        }
    }

}

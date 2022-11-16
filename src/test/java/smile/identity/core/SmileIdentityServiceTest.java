package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.Before;
import org.junit.Test;

import smile.identity.core.enums.Product;
import smile.identity.core.models.*;
import smile.identity.core.exceptions.JobFailed;
;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SmileIdentityServiceTest {

    private final String BASE_PATH = "testing/";
    private JobStatusRequest jobStatusRequest;
    private PreUploadRequest preUploadRequest;

    @Before
    public void setup(){
    preUploadRequest = new PreUploadRequest("", "", "", null, "");
    jobStatusRequest = new JobStatusRequest("partner", "user-01", "10", false,
            false, "signature", "");
    }

    @Test
    public void submitsIdVerificationJob() throws Exception {
        try(MockWebServer server = new MockWebServer()){
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());

            JobResponse jobResponse = new JobResponse("","smile-100",null,"ID Verification",
                    "","","",null,"signature", "", "",
                    "", null);

            JsonAdapter<JobResponse> adaptor = new Moshi.Builder().build().adapter(JobResponse.class);

            server.enqueue(new MockResponse().setBody(adaptor.toJson(jobResponse)));
            EnhancedKYCRequest request = new EnhancedKYCRequest("partner", "",
                    "signature", null, "", "", "",
                    "", "", "", "", false, false);
            JobResponse result = service.idVerification(request);
            assertEquals(result.getSignature(), "signature");
            assertEquals(result.getSmileJobId(), "smile-100");
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
            EnhancedKYCRequest request = new EnhancedKYCRequest("partner", "",
                    "signature", null, "", "", "",
                    "", "", "", "", false, false);
            assertThrows(JobFailed.class, ()->  service.idVerification(request));
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
            PreUploadResponse response = service.preUpload(preUploadRequest);

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

            assertThrows(JobFailed.class, () -> service.preUpload(preUploadRequest));
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

            JobResponse result = new JobResponse("", "", null, "Great Job",
                    "", "", "done", null, "signature",
                    "", "99.999", "", null);

            JobStatusResponse statusResponse = new JobStatusResponse(
                    "2020", true, true, result,
                    "signature", "", null, null
            );

            JsonAdapter<JobStatusResponse> adapter = new Moshi.Builder().build().adapter(JobStatusResponse.class);

            server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
            JobStatusResponse response = service.getJobStatus(this.jobStatusRequest);

            assertEquals(response.getResult().getClass(), JobResponse.class);
            server.shutdown();
        }
    }

    @Test
    public void getJobStatusEnhanced() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());

            JobResponse result = new EnhancedResponse("v1", "smile-100", null,
                    "Document Verification", "Document Verified After Human Review", "0810",
                    "yes", null, "signature",
                    "", "99.0" ,"", null, "", "", "",
                    "", "", "", "", "", "", "",
                    "M", "");

            JobStatusResponse statusResponse = new JobStatusResponse(
                    "2010", true, true, result, "", "",
                    new HashMap<>(), new ArrayList<>());

            JsonAdapter<JobStatusResponse> adapter = new Moshi.Builder().build().adapter(JobStatusResponse.class);

            server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
            JobStatusResponse response = service.getJobStatus(this.jobStatusRequest);

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
            JobStatusResponse response = service.pollJobStatus(this.jobStatusRequest);
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

            JobStatusResponse response = service.pollJobStatus(this.jobStatusRequest);
            assertFalse(response.isJobComplete());
            server.shutdown();
        }
    }

    @Test
    public void getsWebToken() throws Exception {
        try(MockWebServer server = new MockWebServer()) {
            HttpUrl baseUrl = server.url(BASE_PATH);
            SmileIdentityService service = new SmileIdentityService(baseUrl.toString());
            WebTokenResponse tokenResponse = new WebTokenResponse(true, "heresatoken");

            JsonAdapter<WebTokenResponse> adapter = new Moshi.Builder().build().adapter(WebTokenResponse.class);
            server.enqueue(new MockResponse().setBody(adapter.toJson(tokenResponse)));

            WebTokenResponse response = service.getWebToken(new WebTokenRequest("", "", Product.BASIC_KYC,
                    "", "", "", ""));
            assertTrue(response.isSuccess());
            assertEquals(response.getToken(), "heresatoken");
            server.shutdown();
        }
    }

}

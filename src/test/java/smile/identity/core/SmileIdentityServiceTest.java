package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import smile.identity.core.enums.Product;
import smile.identity.core.exceptions.JobFailed;
import smile.identity.core.models.*;

import static org.junit.Assert.*;

public class SmileIdentityServiceTest {

    MockWebServer server;
    SmileIdentityService service;
    private final Moshi moshi = MoshiUtils.getMoshi();

    @Before
    public void setup() {
        server = new MockWebServer();
        HttpUrl baseUrl = server.url("testing/");
        service = new SmileIdentityService(baseUrl.toString());
    }

    @After
    public void shutdown() throws IOException {
        server.shutdown();
    }


    @Test
    public void submitsIdVerificationJob() throws Exception {
        JobResponse jobResponse = new JobResponse("1.0", "smile-100", null,
                "ID Verification", "", "1010", "Yes", null, "signature",
                Instant.now(), "99.99", "", null);

        JsonAdapter<JobResponse> adaptor = moshi.adapter(JobResponse.class);

        server.enqueue(new MockResponse().setBody(adaptor.toJson(jobResponse)));
        EnhancedKYCRequest request = new EnhancedKYCRequest("partner",
                Instant.now(), "signature", null, "", "", "", "", "", "", "",
                false, false);
        JobResponse result = service.idVerification(request);
        assertEquals(result.getSignature(), "signature");
        assertEquals(result.getSmileJobId(), "smile-100");
        assertEquals(result.getClass(), IDResponse.class);
    }

    @Test
    public void submitIdVerificationFails() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody(
                "{\"error\": \"Job already exists. Did you mean to set the " +
                        "retry flag to true\", \"code\": \"2215\"}"));
        EnhancedKYCRequest request = new EnhancedKYCRequest("partner",
                Instant.now(), "signature", null, "", "", "", "", "", "", "",
                false, false);
        assertThrows("Job already exists. Did you mean to set the retry flag " +
                        "to true", JobFailed.class,
                () -> service.idVerification(request));

    }

    @Test
    public void submitsPreUpload() throws Exception {
        String responseString = "{\"smile_job_id\": \"123232121\", " +
                "\"upload_url\": \"photos.com\"}";
        server.enqueue(new MockResponse().setBody(responseString));
        PreUploadResponse response =
                service.preUpload(new PreUploadRequest(Instant.now(),
                        "signature", "client", null, ""));

        assertEquals(response.getSmileJobId(), "123232121");
        assertEquals(response.getUploadUrl(), "photos.com");
    }

    @Test
    public void submitPreUploadFails() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody(
                "{\"error\": \"Job already exists. Did you mean to set the " +
                        "retry flag to true\", \"code\": \"2215\"}"));

        assertThrows("Job already exists. Did you mean to set the retry flag " +
                        "to true", JobFailed.class,
                () -> service.preUpload(new PreUploadRequest(Instant.now(),
                        "signature", "client", null, "")));
    }

    @Test
    public void uploadImagesToCorrectUrl() throws Exception {
        server.enqueue(new MockResponse());
        service.uploadImages("callback_url", new byte[1024]);
        RecordedRequest recordedRequest = server.takeRequest();

        assertEquals(recordedRequest.getRequestUrl().toString(), server.url(
                "testing/") + "callback_url");
    }


    @Test
    public void getServices() throws Exception {
        String response = "{\"id_types\":{\"GH\":{\"SSNIT\":[\"name\"]}}}";
        server.enqueue(new MockResponse().setBody(response));

        String services = service.getServices();
        assertEquals(services, response);
    }

    @Test
    public void getJobStatus() throws Exception {
        JobResponse statusResult = new JobResponse("", "", null, "Great Job", "",
                "", "done", null, "signature", Instant.now(), "99.999", "",
                null);

        JobStatusResponse statusResponse = new JobStatusResponse("2020", true, true, new JobStatusResponse.Result(statusResult), "signature", Instant.now(), new HashMap<>(), new ArrayList<>(), "", "");

        JsonAdapter<JobStatusResponse> adapter = moshi.adapter(JobStatusResponse.class);

        server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
        JobStatusResponse response =
                service.getJobStatus(new JobStatusRequest("partner", "user-01"
                        , "10", false, false, "signature", Instant.now()));

        assertNotNull(response.getResult().getJobResponse());
    }

    @Test
    public void getJobStatusWithStringResult() throws Exception {
        String result = "{\"timestamp\":\"1678216434434\",\"signature\":\"6f3dec6d6fad3cfe36fb62b38a185de1cd53088291082d6f20fddbf5ea0e4a0f\",\"job_complete\":false,\"job_success\":false,\"code\":\"2314\",\"result\":\"No zip file received\"}";
        server.enqueue(new MockResponse().setBody(result));
        JobStatusResponse response =
                service.getJobStatus(new JobStatusRequest("partner", "user-01"
                        , "10", false, false, "signature", Instant.now()));

        assertEquals(response.getResult().getMessage(), "No zip file received");
    }

    @Test
    public void getJobStatusIDResponse() throws Exception {

        JobResponse result = new IDResponse("v1", "smile-100", null,
                "Document Verification", "Document Verified After Human " +
                "Review", "0810", "yes", null, "signature", Instant.now(),
                "99.0", "", null, "", "", "", "", "", "", "", "", "", "", "M"
                , "");

        JobStatusResponse statusResponse = new JobStatusResponse("2010", true, true, new JobStatusResponse.Result(result), "", Instant.now(), new HashMap<>(), new ArrayList<>(), "", "");

        JsonAdapter<JobStatusResponse> adapter = moshi.adapter(JobStatusResponse.class);

        server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
        JobStatusResponse response =
                service.getJobStatus(new JobStatusRequest("partner", "user-01"
                        , "10", false, false, "signature", Instant.now()));

        assertEquals(IDResponse.class, response.getResult().getJobResponse().getClass());
    }


    @Test
    public void pollJobStatus() throws Exception {
        String bad = "{\"job_complete\": false, \"signature\": " +
                "\"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\", " +
                "\"timestamp\": \"2022-11-10T19:05:42.775Z\"}";
        String success = "{\"job_complete\": true, \"signature\": " +
                "\"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\", " +
                "\"timestamp\": \"2022-11-10T19:05:42.775Z\"}";

        server.enqueue(new MockResponse().setBody(bad));
        server.enqueue(new MockResponse().setBody(bad));
        server.enqueue(new MockResponse().setBody(bad));
        server.enqueue(new MockResponse().setBody(success));
        JobStatusResponse response =
                service.pollJobStatus(new JobStatusRequest("partner", "user" +
                        "-01", "10", false, false, "signature",
                        Instant.now()), 4, 1);
        assertTrue(response.isJobComplete());
    }

    @Test
    public void pollJobStatusReturnsUncompleted() throws Exception {
        String bad = "{\"job_complete\": false, \"signature\": " +
                "\"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\", " +
                "\"timestamp\": \"2022-11-10T19:05:42.775Z\"}";
        for (int i = 0; i <= 4; i++) {
            server.enqueue(new MockResponse().setBody(bad));
        }

        JobStatusResponse response =
                service.pollJobStatus(new JobStatusRequest("partner", "user" +
                        "-01", "10", false, false, "signature",
                        Instant.now()), 3, 1);
        assertFalse(response.isJobComplete());
    }

    @Test
    public void getsWebToken() throws Exception {
        WebTokenResponse tokenResponse = new WebTokenResponse(true,
                "heresatoken");

        JsonAdapter<WebTokenResponse> adapter = moshi.adapter(WebTokenResponse.class);
        server.enqueue(new MockResponse().setBody(adapter.toJson(tokenResponse)));

        WebTokenResponse response = service.getWebToken(new WebTokenRequest(
                "user", "smile-100", Product.BASIC_KYC, "callback",
                "signature", Instant.now(), "partner"));
        assertTrue(response.isSuccess());
        assertEquals(response.getToken(), "heresatoken");
    }

}

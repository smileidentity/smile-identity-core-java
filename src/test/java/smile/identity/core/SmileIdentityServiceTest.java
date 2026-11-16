package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import smile.identity.core.enums.Product;
import smile.identity.core.exceptions.JobFailed;
import smile.identity.core.models.EnhancedKYCRequest;
import smile.identity.core.models.IDResponse;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusRequest;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.PreUploadRequest;
import smile.identity.core.models.PreUploadResponse;
import smile.identity.core.models.WebTokenRequest;
import smile.identity.core.models.WebTokenResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SmileIdentityServiceTest {

    private final Moshi moshi = MoshiUtils.getMoshi();
    MockWebServer server;
    SmileIdentityService service;

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
                Instant.now(), "99.99", null, null, null);

        JsonAdapter<JobResponse> adaptor = moshi.adapter(JobResponse.class);

        server.enqueue(new MockResponse().setBody(adaptor.toJson(jobResponse)));
        EnhancedKYCRequest request = new EnhancedKYCRequest("partner",
                Instant.now(), "signature", null, "", "", "", "", "", "", "",
                false, false);
        JobResponse result = service.idVerification(request);
        assertEquals("signature", result.getSignature());
        assertEquals("smile-100", result.getSmileJobId());
        assertEquals(IDResponse.class, result.getClass());
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

        assertEquals("123232121", response.getSmileJobId());
        assertEquals("photos.com", response.getUploadUrl());
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
        assertEquals(response, services);
    }

    @Test
    public void getJobStatus() throws Exception {
        JobResponse statusResult = new JobResponse("", "", null, "Great Job", "",
                "", "done", null, "signature", Instant.now(), "99.999", "", null, null);

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

        assertEquals("No zip file received", response.getResult().getMessage());
    }

    @Test
    public void getJobStatusIDResponse() throws Exception {

        JobResponse result = new IDResponse("v1", "smile-100", null,
                "Document Verification", "Document Verified After Human " +
                "Review", "0810", "yes", null, "signature", Instant.now(),
                "99.0", "", null, null, "", "", "", "", "", "", "", "", "", "M"
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

    private static final String COMPLETE = "{\"job_complete\": true, " +
            "\"signature\": \"tMPhsPdh3k3PejTNGG970gQSA41oO9/I3oERRlSSBUc=\"," +
            " \"timestamp\": \"2022-11-10T19:05:42.775Z\"}";

    private final List<Long> sleeps = new ArrayList<>();

    private SmileIdentityService recordingSleeps() {
        return new SmileIdentityService(server.url("testing/").toString()) {
            @Override
            void sleep(long millis) {
                sleeps.add(millis);
            }
        };
    }

    private JobStatusRequest statusRequest() {
        return new JobStatusRequest("partner", "user-01", "10", false, false,
                "signature", Instant.now());
    }

    private MockResponse tooManyRequests(String retryAfter) {
        MockResponse response = new MockResponse().setResponseCode(429)
                .setBody("Too Many Requests");
        return retryAfter == null ? response
                : response.setHeader("Retry-After", retryAfter);
    }

    @Test
    public void pollJobStatusKeepsPollingOn429() throws Exception {
        server.enqueue(tooManyRequests("5"));
        server.enqueue(new MockResponse().setBody(COMPLETE));

        JobStatusResponse response =
                recordingSleeps().pollJobStatus(statusRequest(), 3, 1);

        assertTrue(response.isJobComplete());
        assertEquals(List.of(5000L), sleeps);
        assertEquals(2, server.getRequestCount());
    }

    @Test
    public void pollJobStatusUsesBackoffWhenLongerThanRetryAfter() throws Exception {
        server.enqueue(tooManyRequests("1"));
        server.enqueue(new MockResponse().setBody(COMPLETE));

        recordingSleeps().pollJobStatus(statusRequest(), 3, 5000);

        assertEquals(List.of(5000L), sleeps);
    }

    @Test
    public void pollJobStatusCapsRetryAfterAt60Seconds() throws Exception {
        server.enqueue(tooManyRequests("120"));
        server.enqueue(tooManyRequests("99999999999999999999"));
        server.enqueue(new MockResponse().setBody(COMPLETE));

        recordingSleeps().pollJobStatus(statusRequest(), 3, 1);

        assertEquals(List.of(60000L, 60000L), sleeps);
    }

    @Test
    public void pollJobStatusIgnoresUnusableRetryAfter() throws Exception {
        server.enqueue(tooManyRequests("Wed, 21 Oct 2015 07:28:00 GMT"));
        server.enqueue(tooManyRequests("1.5"));
        server.enqueue(tooManyRequests("-3"));
        server.enqueue(tooManyRequests(null));
        server.enqueue(new MockResponse().setBody(COMPLETE));

        JobStatusResponse response =
                recordingSleeps().pollJobStatus(statusRequest(), 3, 1);

        assertTrue(response.isJobComplete());
        assertEquals(List.of(1L, 2L, 4L, 8L), sleeps);
    }

    @Test
    public void pollJobStatusFailsWhenLastPollIs429() {
        for (int i = 0; i < 3; i++) {
            server.enqueue(tooManyRequests("1"));
        }
        SmileIdentityService service = recordingSleeps();

        JobFailed error = assertThrows(JobFailed.class,
                () -> service.pollJobStatus(statusRequest(), 1, 1));

        assertEquals("429", error.getErrorCode());
        assertEquals(3, server.getRequestCount());
        assertEquals(List.of(1000L, 1000L), sleeps);
    }

    @Test
    public void pollJobStatusStillFailsFastOnServerError() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody(
                "{\"error\": \"System error\", \"code\": \"2201\"}"));
        server.enqueue(new MockResponse().setBody(COMPLETE));
        SmileIdentityService service = recordingSleeps();

        JobFailed error = assertThrows(JobFailed.class,
                () -> service.pollJobStatus(statusRequest(), 3, 1));

        assertEquals("2201", error.getErrorCode());
        assertEquals("System error", error.getMessage());
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void nonJsonErrorBodyFailsWithHttpStatus() {
        server.enqueue(new MockResponse().setResponseCode(502).setBody(
                "<html>Bad Gateway</html>"));

        JobFailed error = assertThrows(JobFailed.class,
                () -> service.getWebToken(new WebTokenRequest("user",
                        "smile-100", Product.BASIC_KYC, "callback",
                        "signature", Instant.now(), "partner")));

        assertEquals("502", error.getErrorCode());
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
        assertEquals("heresatoken", response.getToken());
    }
}

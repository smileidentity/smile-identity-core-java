package smile.identity.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import smile.identity.core.adapters.InstantAdapter;
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

public class SmileIdentityServiceTest {

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
                Instant.now(), "99.99", "", null);

        JsonAdapter<JobResponse> adaptor =
                new Moshi.Builder().add(new InstantAdapter()).build().adapter(JobResponse.class);

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
        JobResponse result = new JobResponse("", "", null, "Great Job", "",
                "", "done", null, "signature", Instant.now(), "99.999", "",
                null);

        JobStatusResponse statusResponse = new JobStatusResponse("2020", true
                , true, result, "signature", Instant.now(), null, null);

        JsonAdapter<JobStatusResponse> adapter =
                new Moshi.Builder().add(new InstantAdapter()).build().adapter(JobStatusResponse.class);

        server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
        JobStatusResponse response =
                service.getJobStatus(new JobStatusRequest("partner", "user-01"
                        , "10", false, false, "signature", Instant.now()));

        assertEquals(response.getResult().getClass(), JobResponse.class);

    }

    @Test
    public void getJobStatusIDResponse() throws Exception {

        JobResponse result = new IDResponse("v1", "smile-100", null,
                "Document Verification", "Document Verified After Human " +
                "Review", "0810", "yes", null, "signature", Instant.now(),
                "99.0", "", null, "", "", "", "", "", "", "", "", "", "", "M"
                , "");

        JobStatusResponse statusResponse = new JobStatusResponse("2010", true
                , true, result, "", Instant.now(), new HashMap<>(),
                new ArrayList<>());

        JsonAdapter<JobStatusResponse> adapter =
                new Moshi.Builder().add(new InstantAdapter()).build().adapter(JobStatusResponse.class);

        server.enqueue(new MockResponse().setBody(adapter.toJson(statusResponse)));
        JobStatusResponse response =
                service.getJobStatus(new JobStatusRequest("partner", "user-01"
                        , "10", false, false, "signature", Instant.now()));

        assertEquals(response.getResult().getClass(), IDResponse.class);
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

        JsonAdapter<WebTokenResponse> adapter =
                new Moshi.Builder().build().adapter(WebTokenResponse.class);
        server.enqueue(new MockResponse().setBody(adapter.toJson(tokenResponse)));

        WebTokenResponse response = service.getWebToken(new WebTokenRequest(
                "user", "smile-100", Product.BASIC_KYC, "callback",
                "signature", Instant.now(), "partner"));
        assertTrue(response.isSuccess());
        assertEquals(response.getToken(), "heresatoken");
    }

}

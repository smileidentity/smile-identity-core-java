package smile.identity.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import smile.identity.core.adapters.InstantAdapter;
import smile.identity.core.adapters.JobTypeAdapter;
import smile.identity.core.adapters.PartnerParamsAdapter;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusRequest;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.Options;
import smile.identity.core.models.PartnerParams;

public class UtilitiesTest {
    private MockWebServer server;
    private Utilities utilities;


    private final JsonAdapter<JobStatusResponse> jobStatusResponseAdapter =
            new Moshi.Builder().add(new InstantAdapter())
                    .add(new PartnerParamsAdapter())
                    .add(new JobTypeAdapter())
                    .build().adapter(JobStatusResponse.class);

    private final JsonAdapter<JobStatusRequest> jobStatusRequestAdapter =
            new Moshi.Builder().add(new InstantAdapter())
                    .add(new PartnerParamsAdapter())
                    .add(new JobTypeAdapter())
                    .build().adapter(JobStatusRequest.class);


    @Before
    public void setup() {
        server = new MockWebServer();
        HttpUrl baseUrl = server.url("testing/");
        utilities = new Utilities("partner", "apiKey", baseUrl.toString());
    }

    @After
    public void shutdown() throws IOException {
        server.shutdown();
    }


    @Test
    public void getJobStatus() throws Exception {
        Options options = new Options(false, false, true, true, "");
        server.enqueue(new MockResponse().setBody(jobStatusResponseAdapter.toJson(response())));
        utilities.getJobStatus("user", "job-100"
                , options);

        // check request is correct
        RecordedRequest recordedRequest = server.takeRequest();
        JobStatusRequest request =
                jobStatusRequestAdapter.fromJson(recordedRequest.getBody());

        assertEquals(request.getJobId(), "job-100");
        assertEquals(request.getPartnerId(), "partner");
        assertEquals(request.getUserId(), "user");
        assertFalse(request.isHistory());
        assertFalse(request.isImageLinks());

    }

    @Test
    public void getJobStatusDefaultOptions() throws Exception {
        Options options = new Options();
        server.enqueue(new MockResponse().setBody(jobStatusResponseAdapter.toJson(response())));
        utilities.getJobStatus("user", "job-100");

        // check request is correct
        RecordedRequest recordedRequest = server.takeRequest();
        JobStatusRequest request =
                jobStatusRequestAdapter.fromJson(recordedRequest.getBody());

        assertEquals(request.getJobId(), "job-100");
        assertEquals(request.getPartnerId(), "partner");
        assertEquals(request.getUserId(), "user");
        assertEquals(request.isHistory(), options.isReturnHistory());
        assertEquals(request.isImageLinks(), options.isReturnImageLinks());
    }

    @Test
    public void pollForJobStatus() throws Exception {
        Options options = new Options(false, false, true, true, "");
        SmileIdentityService service = mock(SmileIdentityService.class);
        Whitebox.setInternalState(utilities, service);

        utilities.pollJobStatus("user", "job-100", options, 1, 200L);
        verify(service, times(1)).pollJobStatus(any(JobStatusRequest.class),
                eq(1), eq(200L));
    }

    @Test
    public void pollForJobStatusWithDefaults() throws Exception {
        SmileIdentityService service = mock(SmileIdentityService.class);
        Whitebox.setInternalState(utilities, service);

        utilities.pollJobStatus("user", "job-100");
        verify(service, times(1)).pollJobStatus(any(JobStatusRequest.class),
                eq(3), eq(2000L));
    }


    private JobStatusResponse response() {
        PartnerParams params = new PartnerParams(JobType.BASIC_KYC,
                new HashMap<>(), "user", "job-100");
        JobResponse result = new JobResponse("1.0", "smile-100", params,
                "KYC", "So Great", "90210", "Maybe", null,
                "signature", Instant.now(), "99.99", "internet",
                new HashMap<>());
        JobStatusResponse statusResponse = new JobStatusResponse("90210",
                true, true, result, "signature", Instant.now(), null, null);
        return statusResponse;
    }
}

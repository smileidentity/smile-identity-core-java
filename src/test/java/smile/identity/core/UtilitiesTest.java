package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusRequest;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.Options;
import smile.identity.core.models.PartnerParams;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UtilitiesTest {
    private final Moshi moshi = MoshiUtils.getMoshi();
    private final JsonAdapter<JobStatusResponse> jobStatusResponseAdapter =
            moshi.adapter(JobStatusResponse.class);
    private final JsonAdapter<JobStatusRequest> jobStatusRequestAdapter =
            moshi.adapter(JobStatusRequest.class);
    private MockWebServer server;
    private Utilities utilities;

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
        Options options = new Options(false, false, false, "");
        server.enqueue(new MockResponse().setBody(jobStatusResponseAdapter.toJson(response())));
        utilities.getJobStatus("user", "job-100"
                , options);

        // check request is correct
        RecordedRequest recordedRequest = server.takeRequest();
        JobStatusRequest request =
                jobStatusRequestAdapter.fromJson(recordedRequest.getBody());

        assertEquals("job-100", request.getJobId());
        assertEquals("partner", request.getPartnerId());
        assertEquals("user", request.getUserId());
        assertFalse(request.isHistory());
        assertFalse(request.isImageLinks());
    }

    @Ignore("This test needs to be fixed")
    @Test
    public void getJobStatusDefaultOptions() throws Exception {
        Options options = new Options();
        server.enqueue(new MockResponse().setBody(jobStatusResponseAdapter.toJson(response())));
        utilities.getJobStatus("user", "job-100");

        // check request is correct
        RecordedRequest recordedRequest = server.takeRequest();
        JobStatusRequest request =
                jobStatusRequestAdapter.fromJson(recordedRequest.getBody());

        assertEquals("job-100", request.getJobId());
        assertEquals("partner", request.getPartnerId());
        assertEquals("user", request.getUserId());
        assertEquals(request.isHistory(), options.isReturnHistory());
        assertEquals(request.isImageLinks(), options.isReturnImageLinks());
    }

    private JobStatusResponse response() {
        PartnerParams params = new PartnerParams(
                JobType.BASIC_KYC, "user", "job", new HashMap<>()
        );
        JobResponse result = new JobResponse("1.0", "smile-100", params,
                "KYC", "So Great", "90210", "Maybe", null,
                "signature", Instant.now(), "99.99", "internet", null,
                new HashMap<>());
        JobStatusResponse statusResponse = new JobStatusResponse("90210", true, true, new JobStatusResponse.Result(result), "signature", Instant.now(), new HashMap<>(), new ArrayList<>(), "", "");
        return statusResponse;
    }
}

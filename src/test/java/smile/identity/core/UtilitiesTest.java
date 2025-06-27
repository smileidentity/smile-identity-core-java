package smile.identity.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.*;

public class UtilitiesTest {
    private MockWebServer server;
    private Utilities utilities;

    private final Moshi moshi = MoshiUtils.getMoshi();

    private final JsonAdapter<JobStatusResponse> jobStatusResponseAdapter =
            moshi.adapter(JobStatusResponse.class);

    private final JsonAdapter<JobStatusRequest> jobStatusRequestAdapter =
            moshi.adapter(JobStatusRequest.class);


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

        assertEquals(request.getJobId(), "job-100");
        assertEquals(request.getPartnerId(), "partner");
        assertEquals(request.getUserId(), "user");
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

        assertEquals(request.getJobId(), "job-100");
        assertEquals(request.getPartnerId(), "partner");
        assertEquals(request.getUserId(), "user");
        assertEquals(request.isHistory(), options.isReturnHistory());
        assertEquals(request.isImageLinks(), options.isReturnImageLinks());
    }

    private JobStatusResponse response() {
        PartnerParams params = new PartnerParams(
                JobType.BASIC_KYC, "user", "job", new HashMap<>()
        );
        JobResponse result = new JobResponse("1.0", "smile-100", params,
                "KYC", "So Great", "90210", "Maybe", null,
                "signature", Instant.now(), "99.99", "internet",
                new HashMap<>());
        JobStatusResponse statusResponse = new JobStatusResponse("90210", true, true, new JobStatusResponse.Result(result), "signature", Instant.now(),  new HashMap<>(), new ArrayList<>(), "", "");
        return statusResponse;
    }
}

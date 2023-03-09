package smile.identity.core.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;
import smile.identity.core.MoshiUtils;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.PartnerParams;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class JobStatusResultAdapterTest {
    private final Moshi moshi = MoshiUtils.getMoshi();
    private final JsonAdapter<JobStatusResponse.Result> adapter = moshi.adapter(JobStatusResponse.Result.class);

    @Test
    public void FromJsonWhenString() {
        String message = "Zip file failed";
        JobStatusResponse.Result result = adapter.fromJsonValue(message);
        assertNotNull(result.getMessage());
        assertNull(result.getJobResponse());
        assertEquals(message, result.getMessage());
    }

    @Test
    public void FromJsonWhenJobResponse() throws IOException {
        JobResponse response = new JobResponse("job-id", new PartnerParams(JobType.BASIC_KYC,
                "user", "1245", new HashMap<>()));
        JsonAdapter<JobResponse> jobResponseJsonAdapter = moshi.adapter(JobResponse.class);
        JobStatusResponse.Result result = adapter.fromJson(jobResponseJsonAdapter.toJson(response));
        assertNotNull(result.getJobResponse());
        assertNull(result.getMessage());
        assertEquals("job-id", result.getJobResponse().getSmileJobId());
    }

}

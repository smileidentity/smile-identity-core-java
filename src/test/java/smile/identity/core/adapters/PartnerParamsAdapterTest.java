package smile.identity.core.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;
import smile.identity.core.MoshiUtils;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.PartnerParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PartnerParamsAdapterTest {

    private final Moshi moshi = MoshiUtils.getMoshi();
    private final JsonAdapter<PartnerParams> adaptor = moshi.adapter(PartnerParams.class);

    @Test
    public void toJson() {
        Map<String, Object> optional = new HashMap<>();
        optional.put("random", "key_1");
        optional.put("more", 20);

        PartnerParams params = new PartnerParams(
                JobType.BASIC_KYC, "user", null, optional
        );

        String json = adaptor.toJson(params);
        assertEquals("{\"job_type\":5,\"random\":\"key_1\",\"user_id\":\"user\",\"more\":20}", json);
    }

    @Test
    public void fromJson() throws IOException {
        String json = "{\"job_type\":5,\"random\":\"key_1\",\"user_id\":\"user\",\"more\":20}";

        PartnerParams partnerParams = adaptor.fromJson(json);

        assertEquals("user", partnerParams.getUserId());
        assertNull(partnerParams.getJobId());
        assertEquals(JobType.BASIC_KYC, partnerParams.getJobType());
        assertEquals("key_1", partnerParams.getOptionalInfo().get("random"));
        assertEquals(20.0, partnerParams.getOptionalInfo().get("more"));
    }
}

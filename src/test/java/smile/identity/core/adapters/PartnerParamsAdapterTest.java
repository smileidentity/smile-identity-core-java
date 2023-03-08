package smile.identity.core.adapters;

import static org.junit.Assert.*;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import smile.identity.core.SmileIdentityMoshi;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.PartnerParams;

public class PartnerParamsAdapterTest {

    private final Moshi moshi = SmileIdentityMoshi.getMoshi();
    private final JsonAdapter<PartnerParams> adaptor = moshi.adapter(PartnerParams.class);

    @Test
    public void toJson(){
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

        assertEquals(partnerParams.getUserId(), "user");
        assertEquals(partnerParams.getJobId(), null);
        assertEquals(partnerParams.getJobType(), JobType.BASIC_KYC);
        assertEquals(partnerParams.getOptionalInfo().get("random"), "key_1");
        assertEquals(partnerParams.getOptionalInfo().get("more"), 20.0);


    }
}

package smile.identity.core.adapters;

import static org.junit.Assert.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import smile.identity.core.enums.JobType;
import smile.identity.core.models.PartnerParams;

public class PartnerParamsAdapterTest {


    @Test
    public void toJson(){
        JsonAdapter<PartnerParams> adaptor = new Moshi.Builder()
                .add(new PartnerParamsAdapter()).build().adapter(PartnerParams.class);

        PartnerParams params = new PartnerParams();
        params.setJobType(JobType.BASIC_KYC);
        params.setUserId("user");

        Map<String, Object> optional = new HashMap<>();
        optional.put("random", "key_1");
        optional.put("more", 20);

        params.setOptionalInfo(optional);

        String json = adaptor.toJson(params);
        assertEquals("{\"job_type\":5,\"random\":\"key_1\",\"user_id\":\"user\",\"more\":20}", json);
    }

    @Test
    public void fromJson() throws IOException {
        JsonAdapter<PartnerParams> adaptor = new Moshi.Builder()
                .add(new PartnerParamsAdapter()).build().adapter(PartnerParams.class);

        String json = "{\"job_type\":5,\"random\":\"key_1\",\"user_id\":\"user\",\"more\":20}";

        PartnerParams partnerParams = adaptor.fromJson(json);

        assertEquals(partnerParams.getUserId(), "user");
        assertEquals(partnerParams.getJobId(), null);
        assertEquals(partnerParams.getJobType(), JobType.BASIC_KYC);
        assertEquals(partnerParams.getOptionalInfo().get("random"), "key_1");
        assertEquals(partnerParams.getOptionalInfo().get("more"), 20.0);


    }
}

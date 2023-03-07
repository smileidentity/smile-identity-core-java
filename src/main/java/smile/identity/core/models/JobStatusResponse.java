package smile.identity.core.models;

import com.squareup.moshi.Json;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import lombok.AllArgsConstructor;
import lombok.Value;
import smile.identity.core.adapters.ImageTypeAdapter;
import smile.identity.core.adapters.InstantAdapter;
import smile.identity.core.adapters.JobTypeAdapter;
import smile.identity.core.adapters.PartnerParamsAdapter;

@Value
@AllArgsConstructor
public class JobStatusResponse {

    String code;

    @Json(name = "job_complete")
    boolean jobComplete;

    @Json(name = "job_success")
    boolean jobSuccess;

    Object result;

    String signature;

    Instant timestamp;

    @Json(name = "image_links")
    Map<String, String> imageLinks;

    List<JobResponse> history;

    public JobStatusResponse(JobResponse result) {
        this("", false, true, result, result.getSignature(),
                result.getTimestamp(), new HashMap<>(), new ArrayList<>());
    }

    public String getResultAsString(){
        return result.toString();
    }
    public JobResponse getResultAsJobResponse() {
        PolymorphicJsonAdapterFactory<JobResponse> factory = PolymorphicJsonAdapterFactory.of(JobResponse.class, "ResultType")
                .withSubtype(IDResponse.class, "ID Verification")
                .withSubtype(IDResponse.class, "Document Verification")
                .withFallbackJsonAdapter(new Moshi.Builder()
                        .add(new JobTypeAdapter())
                        .add(new ImageTypeAdapter())
                        .add(new PartnerParamsAdapter())
                        .add(new InstantAdapter()).build().adapter((Type) JobResponse.class));

        JsonAdapter<JobResponse> adapter = new Moshi.Builder()
                .add(factory)
                .add(new PartnerParamsAdapter())
                .add(new ImageTypeAdapter())
                .add(new JobTypeAdapter())
                .add(new InstantAdapter())
                .build().adapter(JobResponse.class);

        return adapter.fromJsonValue(result);

    }
}

package smile.identity.core;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import smile.identity.core.adapters.*;
import smile.identity.core.models.IDResponse;
import smile.identity.core.models.JobResponse;

import java.lang.reflect.Type;

public class MoshiUtil {

    /*
    Returns an instance of Moshi with Smile Identity Custom Adapters
     */
    public static Moshi getMoshi() {
        PolymorphicJsonAdapterFactory<JobResponse> factory = PolymorphicJsonAdapterFactory.of(JobResponse.class, "ResultType")
                .withSubtype(IDResponse.class, "ID Verification")
                .withSubtype(IDResponse.class, "Document Verification")
                .withFallbackJsonAdapter(new Moshi.Builder()
                        .add(new JobTypeAdapter())
                        .add(new ImageTypeAdapter())
                        .add(new PartnerParamsAdapter())
                        .add(new InstantAdapter()).build().adapter((Type) JobResponse.class));

        return new Moshi.Builder()
                .add(factory)
                .add(new PartnerParamsAdapter())
                .add(new ImageTypeAdapter())
                .add(new JobTypeAdapter())
                .add(new InstantAdapter())
                .add(new ResultAdapter())
                .build();
    }
}

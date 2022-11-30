package smile.identity.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import smile.identity.core.adapters.InstantAdapter;
import smile.identity.core.adapters.JobTypeAdapter;
import smile.identity.core.adapters.PartnerParamsAdapter;
import smile.identity.core.enums.JobType;
import smile.identity.core.exceptions.IdTypeNotSupported;
import smile.identity.core.exceptions.IncorrectJobType;
import smile.identity.core.exceptions.MissingRequiredFields;
import smile.identity.core.models.EnhancedKYCRequest;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.Options;
import smile.identity.core.models.PartnerParams;

public class IDApiTest {
    private final Moshi moshi = new Moshi.Builder()
            .add(new PartnerParamsAdapter())
            .add(new JobTypeAdapter())
            .add(new InstantAdapter())
            .build();

    private MockWebServer server;
    private IDApi idApi;

    @Before
    public void setup() {
        server = new MockWebServer();
        idApi = new IDApi("partner", "apikey",
                server.url("testing/").toString());
    }

    @After
    public void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    public void submitJob() throws Exception {
        String validResponse = "{\"id_types\": {\"GH\": { \"PASSPORT\": " +
                "[\"id_number\"]}}}";
        server.enqueue(new MockResponse().setBody(validResponse));
        server.enqueue(new MockResponse().setBody(response()));

        PartnerParams partnerParams = new PartnerParams(
                JobType.BASIC_KYC, new HashMap<>(), "user", "job"
        );

        IdInfo idInfo = new IdInfo(
                "Tom", "", "Ford", "GH",
                "PASSPORT", "1111111", "", ""
        );

        JobStatusResponse job = idApi.submitJob(partnerParams, idInfo,
                true, new Options());

        RecordedRequest servicesRequest = server.takeRequest();
        RecordedRequest idRequest = server.takeRequest();
        EnhancedKYCRequest kycRequest = moshi
                .adapter(EnhancedKYCRequest.class).fromJson(idRequest.getBody());

        assertEquals(kycRequest.getCountry(), "GH");
        assertEquals(kycRequest.getFirstName(), "Tom");
        assertEquals(kycRequest.getLastName(), "Ford");
        assertEquals(kycRequest.getIdType(), "PASSPORT");
        assertEquals(kycRequest.getPartnerParams().getJobId(), "job");
        assertEquals(kycRequest.getPartnerParams().getUserId(), "user");
    }

    @Test
    public void submitJobWithoutValidation() throws Exception {
        server.enqueue(new MockResponse().setBody(response()));

        PartnerParams partnerParams = new PartnerParams(
                JobType.BASIC_KYC, new HashMap<>(), "user", "job"
        );

        IdInfo idInfo = new IdInfo(
                "Tom", "", "Ford", "GH",
                "PASSPORT", "1111111", "", ""
        );

        idApi.submitJob(partnerParams, idInfo, new Options());

        RecordedRequest idRequest = server.takeRequest();
        EnhancedKYCRequest kycRequest = moshi
                .adapter(EnhancedKYCRequest.class).fromJson(idRequest.getBody());

        assertEquals(kycRequest.getCountry(), "GH");
        assertEquals(kycRequest.getFirstName(), "Tom");
        assertEquals(kycRequest.getLastName(), "Ford");
        assertEquals(kycRequest.getIdType(), "PASSPORT");
        assertEquals(kycRequest.getPartnerParams().getJobId(), "job");
        assertEquals(kycRequest.getPartnerParams().getUserId(), "user");
    }

    @Test
    public void invalidJobType() {

        PartnerParams partnerParams = new PartnerParams(
                JobType.BIOMETRIC_KYC, new HashMap<>(), "user", "job"
        );

        IdInfo idInfo = new IdInfo(
                "Tom", "", "Ford", "GH",
                "PASSPORT", "1111111", "", ""
        );

        assertThrows(IncorrectJobType.class,
                () -> idApi.submitJob(partnerParams, idInfo));
    }

    @Test
    public void invalidIdInfo() {

        PartnerParams partnerParams = new PartnerParams(
                JobType.BASIC_KYC, new HashMap<>(), "user", "job"
        );

        IdInfo idInfo = new IdInfo(
                "Tom", "", "Ford", "",
                "PASSPORT", "1111111", "", ""
        );

        assertThrows(MissingRequiredFields.class,
                () -> idApi.submitJob(partnerParams, idInfo));
    }

    @Test
    public void unsupportedCountryIdTypeCombo() {
        String validResponse = "{\"id_types\": {\"US\": { \"PASSPORT\": " +
                "[\"id_number\"]}}}";
        server.enqueue(new MockResponse().setBody(validResponse));

        PartnerParams partnerParams = new PartnerParams(
                JobType.BASIC_KYC, new HashMap<>(), "user", "job"
        );

        IdInfo idInfo = new IdInfo(
                "Tom", "", "Ford", "GH",
                "PASSPORT", "1111111", "", ""
        );

        assertThrows(IdTypeNotSupported.class,
                () -> idApi.submitJob(partnerParams, idInfo, true));
    }

    @Test
    public void missingFieldsFromValidationEndpoint() {
        String validResponse = "{\"id_types\": {\"GH\": { \"PASSPORT\": " +
                "[\"favorite_color\"]}}}";
        server.enqueue(new MockResponse().setBody(validResponse));

        PartnerParams partnerParams = new PartnerParams(
                JobType.BASIC_KYC, new HashMap<>(), "user", "job"
        );

        IdInfo idInfo = new IdInfo(
                "Tom", "", "Ford", "GH",
                "PASSPORT", "1111111", "", ""
        );

        assertThrows(MissingRequiredFields.class,
                () -> idApi.submitJob(partnerParams, idInfo, true));
    }


    private String response() {
        JobResponse response = new JobResponse(
                "1.0",
                "smile-100",
                null,
                "Document Verification",
                "Great",
                "100",
                "Yes",
                null,
                "signature",
                Instant.now(),
                "99.99",
                "internet",
                new HashMap<>()
        );

        JsonAdapter<JobResponse> adapter = moshi.adapter(JobResponse.class);
        return adapter.toJson(response);
    }
}

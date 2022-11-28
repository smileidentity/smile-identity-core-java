package smile.identity.core;

import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import smile.identity.core.adapters.InstantAdapter;
import smile.identity.core.adapters.JobTypeAdapter;
import smile.identity.core.adapters.PartnerParamsAdapter;
import smile.identity.core.enums.JobType;
import smile.identity.core.exceptions.IdTypeNotSupported;
import smile.identity.core.exceptions.IncorrectJobType;
import smile.identity.core.exceptions.JobFailed;
import smile.identity.core.exceptions.MissingRequiredFields;
import smile.identity.core.keys.SignatureKey;
import smile.identity.core.models.EnhancedKYCRequest;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.Options;
import smile.identity.core.models.PartnerParams;


public class IDApi {

    private final String partnerId;
    private final String apiKey;

    private final SmileIdentityService smileIdentityService;


    public IDApi(String partnerId, String apiKey, String sidServer) {
        this.partnerId = partnerId;
        this.apiKey = apiKey;
        String url = Utils.getSidServer(sidServer);
        this.smileIdentityService = new SmileIdentityService(url);
    }


    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo) throws Exception {
        return submitJob(partnerParams, idInfo, true);
    }

    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo, Options options) throws Exception {
        return submitJob(partnerParams, idInfo, true, options);
    }

    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo,
                                       Boolean useValidationApi) throws Exception {
        Options options = new Options();
        return submitJob(partnerParams, idInfo, useValidationApi, options);
    }

    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo,
                                       boolean useValidationApi,
                                       Options options) throws Exception {

        if (!partnerParams.getJobType().equals(JobType.BASIC_KYC) && !partnerParams.getJobType().equals(JobType.ENHANCED_KYC)) {
            throw new IncorrectJobType(5, "ID Api");
        }

        if (!idInfo.valid()) {
            throw new MissingRequiredFields("idNumber, country, idType");
        }

        EnhancedKYCRequest request = setupRequests(partnerParams, idInfo,
                options);

        if (useValidationApi) {
            validateIdType(idInfo.getCountry(), idInfo.getIdType(), request);
        }

        JobResponse result = smileIdentityService.idVerification(request);
        return new JobStatusResponse(result.getResultCode(), true, true,
                result, result.getSignature(), result.getTimestamp(), null,
                null);
    }

    private EnhancedKYCRequest setupRequests(PartnerParams partnerParams,
                                             IdInfo idInfo, Options options) {

        SignatureKey key = new Signature(this.partnerId, this.apiKey).getSignatureKey();

        return new EnhancedKYCRequest(this.partnerId, key.getInstant(),
                key.getSignature(), partnerParams, idInfo.getCountry(),
                idInfo.getFirstName(), idInfo.getLastName(),
                idInfo.getIdType(), idInfo.getIdNumber(), idInfo.getDob(),
                idInfo.getPhoneNumber(), options.isReturnImageLinks(),
                options.isReturnHistory());
    }

    private void validateIdType(String country, String idType,
                                EnhancedKYCRequest idVerificationRequest) throws JobFailed, IOException, IdTypeNotSupported, MissingRequiredFields {
        String services = smileIdentityService.getServices();
        Map<String, Object> flattened =
                new JsonFlattener(services).withFlattenMode(FlattenMode.KEEP_PRIMITIVE_ARRAYS).flattenAsMap();
        String search = String.join(".", "id_types", country, idType);
        List<String> requiredFields =
                (List<String>) flattened.getOrDefault(search, null);

        if (requiredFields == null) {
            throw new IdTypeNotSupported(country, idType);
        }

        Moshi moshi =
                new Moshi.Builder().add(new PartnerParamsAdapter()).add(new JobTypeAdapter()).add(new InstantAdapter()).build();
        JsonAdapter<EnhancedKYCRequest> adapter =
                moshi.adapter(EnhancedKYCRequest.class);
        Map<String, Object> jsonObject =
                JsonFlattener.flattenAsMap(adapter.toJson(idVerificationRequest));

        for (String field : requiredFields) {
            if (jsonObject.get(field) == null && jsonObject.get(String.format("%s.partner_params", field)) == null) {
                throw new MissingRequiredFields(field);
            }
        }
    }
}

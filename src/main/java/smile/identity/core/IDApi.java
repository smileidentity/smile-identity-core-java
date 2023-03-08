package smile.identity.core;

import smile.identity.core.enums.JobType;
import smile.identity.core.exceptions.IncorrectJobType;
import smile.identity.core.exceptions.MissingRequiredFields;
import smile.identity.core.keys.SignatureKey;
import smile.identity.core.models.*;


public class IDApi {

    private final String partnerId;
    private final String apiKey;

    private final SmileIdentityService smileIdentityService;


    public IDApi(String partnerId, String apiKey, String sidServer) {
        this.partnerId = partnerId;
        this.apiKey = apiKey;
        String url = ConfigHelpers.getSidServer(sidServer);
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
                                       boolean useValidationApi) throws Exception {
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
            IdValidator.validateIdType(smileIdentityService, idInfo,
                    partnerParams);
        }

        JobResponse result = smileIdentityService.idVerification(request);
        return new JobStatusResponse(result.getResultCode(), true, true,
                new Result(null, result), result.getSignature(), result.getTimestamp(), null,
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

}

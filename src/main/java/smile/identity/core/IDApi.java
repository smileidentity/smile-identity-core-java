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


    /**
     * Submits a KYC Job
     * @param partnerParams partner parameters used for tracking job.
     * @param idInfo id information to lookup.
     * @return response from API
     * @throws Exception
     */
    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo) throws Exception {
        Options options = new Options();
        return submitJob(partnerParams, idInfo, options);
    }

    /**
     * @deprecated
     * The useValidationApi parameter is no longer used.
     * <p> Use {@link IDApi#submitJob(PartnerParams, IdInfo)} instead. </p>
     * @param partnerParams partner parameters used for tracking job.
     * @param idInfo id information to lookup.
     * @param useValidationApi validates the correct fields are provided for id type before submitting job.
     * @return response from API
     * @throws Exception
     */
    @Deprecated
    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo,
                                       boolean useValidationApi) throws Exception {
        Options options = new Options();
        return submitJob(partnerParams, idInfo, options);
    }

    /**
     * @deprecated
     * The useValidationApi parameter is no longer used.
     * <p> Use {@link IDApi#submitJob(PartnerParams, IdInfo, Options)} instead.</p>
     * @param partnerParams partner parameters used for tracking job.
     * @param idInfo id information to lookup.
     * @param useValidationApi validates the correct fields are provided for id type before submitting job.
     * @param options job related options
     * @return response from API
     * @throws Exception
     */
    @Deprecated
    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo,
                                       boolean useValidationApi,
                                       Options options) throws Exception {
        return submitJob(partnerParams, idInfo, options);
    }

    /**
     *  Submits a KYC job
     * @param partnerParams partner parameters used for tracking job.
     * @param idInfo id information to lookup.
     * @param options job related options.
     * @return response from API
     * @throws Exception
     */
    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo,
                                       Options options) throws Exception {

        if (!partnerParams.getJobType().equals(JobType.BASIC_KYC) && !partnerParams.getJobType().equals(JobType.ENHANCED_KYC)) {
            throw new IncorrectJobType(5, "ID Api");
        }

        if (!idInfo.valid()) {
            throw new MissingRequiredFields("idNumber, country, idType");
        }

        EnhancedKYCRequest request = setupRequests(partnerParams, idInfo,
                options);

        JobResponse result = smileIdentityService.idVerification(request);
        return new JobStatusResponse(result.getResultCode(), true, true,
                new JobStatusResponse.Result(result), result.getSignature(), result.getTimestamp(), null,
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

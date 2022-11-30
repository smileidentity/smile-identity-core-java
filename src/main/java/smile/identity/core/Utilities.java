package smile.identity.core;


import smile.identity.core.keys.SignatureKey;
import smile.identity.core.models.JobStatusRequest;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.Options;


public class Utilities {

    private final String partnerId;
    private final SmileIdentityService smileIdentityService;
    private final Signature signature;


    public Utilities(String partnerId, String apiKey, String sidServer) {
    	this.partnerId = partnerId;
        String url = ConfigHelpers.getSidServer(sidServer);
        this.smileIdentityService = new SmileIdentityService(url);
        this.signature = new Signature(partnerId, apiKey);
    }


    public JobStatusResponse getJobStatus(String userId, String jobId,
                                          Options options) throws Exception {

        JobStatusRequest request = configureJobQueryBody(userId, jobId,
                options);
        return smileIdentityService.getJobStatus(request);
    }

    public JobStatusResponse getJobStatus(String userId, String jobId) throws Exception {
        Options options = new Options();
        return getJobStatus(userId, jobId, options);
    }

    public JobStatusResponse pollJobStatus(String userId, String jobId,
                                           Options options, int retryCount,
                                           long initialDelay) throws Exception {
        JobStatusRequest request = configureJobQueryBody(userId, jobId,
                options);
        return smileIdentityService.pollJobStatus(request, retryCount,
                initialDelay);

    }

    public JobStatusResponse pollJobStatus(String userId, String jobId,
                                           Options options) throws Exception {
        int retryCount = 3;
        long initialDelay = 2000L;
        return pollJobStatus(userId, jobId, options, retryCount, initialDelay);
    }

    public JobStatusResponse pollJobStatus(String userId, String jobId) throws Exception {
        Options options = new Options();
        return pollJobStatus(userId, jobId, options);
    }


    private JobStatusRequest configureJobQueryBody(String userId, String jobId,
                                                   Options options) {

        SignatureKey key = signature.getSignatureKey();
        return new JobStatusRequest(
                this.partnerId,
                userId,
                jobId,
                options.isReturnImageLinks(),
                options.isReturnHistory(),
                key.getSignature(),
                key.getInstant()
        );
    }

}

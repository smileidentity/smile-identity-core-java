import smile.identity.core.IDApi;
import smile.identity.core.enums.JobType;

public class SmartSelfieAuthentication {

    public static main(String[] args) {

        String partnerId = "<Put your partner ID here>";
        String defaultCallback = "<Put your default callback url here>";
        String apiKey = "<Put your API key here>";
        String sidServer = "< 0 || 1 >";  // Use '0' for the sandbox server and '1' for production

        IDApi connection = new WebApi(partnerId, apiKey, defaultCallback, sidServer);

        // Create required tracking parameters
        Map<String, Object> optionalInfo = new HashMap(); // map of optional parameters partner uses to track jobs. can be left empty
        PartnerParams params = new PartnerParams(JobType.SMART_SELFIE_AUTHENTICATION, "<unique ID for user>", "< unique job ID >", optionalInfo);

        // Create image list
        // ImageType - This infers to either a file or a base64 encoded image, but not both.
        // If using base 64 encoded image, pass in the encoded string as the second parameter and set fileName to null.
        // If using file, set image parameter to null and filename to path to file
        // ImageType.SELFIE - Selfie image jpg or png (if you have the full path of the selfie)
        // ImageType.SELFIE_BASE64 - Selfie image jpg or png base64 encoded (if you have the base64image string of the selfie)
        // ImageType.LIVENESS - Liveness image jpg or png (if you have the full path of the liveness image)
        // ImageType.LIVENESS_BASE64 - Liveness image jpg or png base64 encoded (if you have the base64image string of the liveness image)


        List<ImageDetails> imageDetails = new ArrayList<>();
        ImageDetail selfie = new ImageDetail(ImageType.SELFIE, null, "< full path to selfie >");
        ImageDetail liveness = new ImageDetail(ImageType.LIVENESS, null, "< full path to liveness image");

        imageDetails.add(selfie);
        imageDetails.add(liveness); // Not required if you don't require proof of life

        // Options for the job
        boolean returnJobStatus = false; // Set to true if you want to get
        // the job result in sync (in addition to the result been sent to
        // your callback). If set to false, result is sent to callback url only
        boolean returnHistory = false; // Set to true to receive all of the
        // updates you would otherwise have received in your callback as
        // opposed to only the final result. You must set return_job_status
        // to true to use this flag.
        boolean returnImageLinks = false; // Set to true to receive links to
        // the selfie and the photo it was compared to. You must set
        // return_job_status to true to use this flag.
        String callBackUrl = "< optional callback url to use for this job only >";
        Options options = new Options(returnHistory, returnImageLinks, returnJobStatus, callBackUrl);

        connection.submitJob(params, imageDetails);


    }
}

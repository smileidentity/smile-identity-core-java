import java.util.ArrayList;
import java.util.HashMap;

import smile.identity.core.WebApi;
import smile.identity.core.enums.ImageType;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.ImageDetail;
import smile.identity.core.models.Options;
import smile.identity.core.models.PartnerParams;

public class BiometricKYC {

    // See https://docs.smileidentity.com/server-to-server/java/products/biometric-kyc
    // for how to setup and retrieve configuation values for the WebApi class.

    public static main(String[] args) {

        String partnerId = "<Put your partner ID here>";
        String defaultCallback = "<Put your default callback url here>";
        String apiKey = "<Put your API key here>";
        String sidServer = "< 0 || 1 >";  // Use '0' for the sandbox server
        // and '1' for production

        WebApi connection = new WebApi(partnerId, apiKey, defaultCallback, sidServer);

        // Create required tracking parameters
        Map<String, Object> optionalInfo = new HashMap(); // map of optional
        // parameters partner uses to track jobs. can be left empty
        PartnerParams params = new PartnerParams(JobType.BIOMETRIC_KYC, "<unique ID for user>", "< unique job ID >", optionalInfo);

        // Create image list
        List<ImageDetails> imageDetails = new ArrayList<>();

        // ImageType.SELFIE - Selfie image jpg or png (if you have the full path of
        // the selfie)
        // ImageType.SELFIE_BASE64 - Selfie image jpg or png base64 encoded (if you
        // have the base64image string of the selfie)
        // ImageType.LIVENESS - Liveness image jpg or png (if you have the full path
        // of the liveness image)
        // ImageType.LIVENESS_BASE64 - Liveness image jpg or png base64 encoded (if
        //  you have the base64image string of the liveness image)
        ImageDetail selfie = new ImageDetail(ImageType.SELFIE, null, "< full path to selfie >");
        // if using BASE64 image type, pass the string to image and set
        // filename to null
        // ImageDetail selfie = new ImageDetail(ImageType.SELFIE_BASE64, "<base64 encoded image>", null);


        ImageDetail liveness = new ImageDetail(ImageType.LIVENESS, null, "< full path to liveness image> ");
        imageDetails.add(selfie);
        imageDetails.add(liveness); // Not required if you don't required
        // proof of life (note photo of photo check will still be performed
        // on the uploaded selfie)

        // Create ID number info
        IdInfo idInfo = new IdInfo("< firstName >", "< middleName >", "< lastName >", "< 2 letter country code >", "< id type >", "< valid id number >", "< date of birth " +
                "yyyy-mm-dd >", "< phone number >");

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

        connection.submitJob(params, idInfo, options);
    }

}

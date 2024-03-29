import java.util.ArrayList;

import smile.identity.core.IDApi;
import smile.identity.core.enums.ImageType;
import smile.identity.core.enums.JobType;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.ImageDetail;

public class DocumentVerification {

    // See https://docs.usesmileid.com/server-to-server/java/products/document-verification for
    // how to setup and retrieve configuation values for the WebApi class.

    public static main(String[] args) {

        String partnerId = "<Put your partner ID here>";
        String defaultCallback = "<Put your default callback url here>";
        String apiKey = "<Put your API key here>";
        String sidServer = "< 0 || 1 >";  // Use '0' for the sandbox server and '1' for production

        WebApi connection = new WebApi(partnerId, apiKey, defaultCallback, sidServer);
        // Create required tracking parameters
        Map<String, Object> optionalInfo = new HashMap(); // map of optional parameters partner uses to track jobs. can be left empty
        PartnerParams params = new PartnerParams(JobType.DOCUMENT_VERIFICATION, "<unique ID for user>", "< unique job ID >", optionalInfo);

        // Create image list
        // ImageType - This infers to either a file or a base64 encoded image, but not both.
        // If using base 64 encoded image, pass in the encoded string as the second parameter and set fileName to null.
        // If using file, set image parameter to null and filename to path to file
        // ImageType.SELFIE - Selfie image jpg or png (if you have the full path of the selfie)
        // ImageType.SELFIE_BASE64 - Selfie image jpg or png base64 encoded (if you have the base64image string of the selfie)
        // ImageType.LIVENESS - Liveness image jpg or png (if you have the full path of the liveness image)
        // ImageType.LIVENESS_BASE64 - Liveness image jpg or png base64 encoded (if you have the base64image string of the liveness image)
        // ImageType.ID_CARD - Front of ID document image jpg or png (if you have the full path of the selfie)
        // ImageType.ID_CARD_BASE64 - Front of ID document image jpg or png base64 encoded (if you have the base64image string of the selfie)
        // ImageType.ID_CARD_BACK - Back of ID document image jpg or png (if you have the full path of the selfie)
        // ImageType.ID_CARD_BACK_BASE64 - Back of ID document image jpg or png base64 encoded (if you have the base64image string of the selfie)

        List<ImageDetail> imageDetails = new ArrayList<>();

        ImageDetail selfie = new ImageDetail(ImageType.SELFIE, null, "< path to file >");
        ImageDetail liveness = new ImageDetail(ImageType.LIVENESS_BASE64, "< base 64 encoded liveness >", null);
        ImageDetail idDocument = new ImageDetail(ImageType.ID_CARD, null, "< path to file >");
        ImageDetail backOfIdDocument = new ImageDetail(ImageType.ID_CARD_BACK_BASE64, "< base 64 encoded image >", null);

        imageDetails.add(selfie);
        imageDetails.add(liveness); // not required if you dont require proof of life. photo check will still be performed on selfie
        imageDetails.add(idDocument);
        imageDetails.add(backOfIdDocument); // Optional, only use if you're uploading the back of the id document image

        IdInfo idInfoWithIdType = new IdInfo("< 2 letter country code >", "< id type >"); // idType is optional. If a job is submitted without id_type and the machine can't classify the document, we will reject the job.
        IdInfo idInfoWithoutIdType = new IdInfo("< 2 letter country code >"); // idType is optional. If a job is submitted without id_type and the machine can't classify the document, we will reject the job.

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

        connection.submitJob(params, imageDetails, idInfoidInfoWithoutIdType, options);
    }


}

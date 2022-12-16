import smile.identity.core.IDApi;
import smile.identity.core.enums.JobType;

public class EnhancedKYC {

    public static main(String [] args) {

        String partnerId = "<Put your partner ID here>";
        String defaultCallback = "<Put your default callback url here>";
        String apiKey = "<Put your API key here>";
        String sidServer = "< 0 || 1 >";  // Use '0' for the sandbox server and '1' for production

        IDApi connection = new IDApi(partnerId, apiKey, defaultCallback, sidServer);

        // Create required tracking parameters
        Map<String, Object> optionalInfo = new HashMap(); // map of optional parameters partner uses to track jobs. can be left empty
        PartnerParams params = new PartnerParams(JobType.ENHANCED_KYC, "<unique ID for user>", "< unique job ID >", optionalInfo);

        // Create ID number info
        IdInfo idInfo = new IdInfo("< firstName >",
        "< middleName >",
        "< lastName >",
        "< 2 letter country code >",
        "< id type >",
        "< valid id number >",
        "< date of birth yyyy-mm-dd >",
        "< phone number >");

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

        // Submit the job
        connection.submitJob(params, idInfo, options);

    }

}

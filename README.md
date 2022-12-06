# SmileIdentityCore

The official Smile Identity library exposes four classes namely; the Web Api class, the ID Api class, the Signature class and the Utilities class.

Please see [changelog.md](https://github.com/smileidentity/smile-identity-core-java/blob/master/changelog.md). for release versions and changes

The **Web Api Class** allows you as the Partner to validate a user’s identity against the relevant Identity Authorities/Third Party databases that Smile Identity has access to using ID information provided by your customer/user (including photo for compare). It has the following public methods:
- submitJob
- getWebToken

The **ID Api Class** lets you performs basic KYC Services including verifying an ID number as well as retrieve a user's Personal Information. It has the following public methods:
- submitJob

The **Signature Class** allows you as the Partner to generate a sec key to interact with our servers. It has the following public methods:
- getSignatureKey
- confirmSignature

The **Utilities Class** allows you as the Partner to have access to our general Utility functions to gain access to your data. It has the following public methods:
- getJobStatus
- pollJobStatus


## Documentation

This library requires specific input parameters, for more detail on these parameters please refer to our [documentation for Web API](https://docs.smileidentity.com/products/web-api/java).

Please note that you will have to be a Smile Identity Partner to be able to query our services. You can sign up on the [Portal](https://portal.smileidentity.com/signup).

## Installation

View the package on [Maven](https://search.maven.org/search?q=a:smile-identity-core).

Add the group, name and version to your application's build file, it will look similar based on your build tool:

```java
group: "com.smileidentity", name: "smile-identity-core", version: "<current-version>"
```

You now may use the classes as follows:

#### Web Api Class

Import the necessary dependant classes for Web Api:

```java
import smile.identity.core.WebApi;
```

##### submitJob method

Your call to the library will be similar to the below code snippet:
```java
  try {
      
      Map<String, Object> optionalParams = new HashMap<>(); // custom partner params 
        PartnerParams partnerParams = new PartnerParams(
                JobType.BASIC_KYC, "user", "job", optionalParams
        );

    // Note dob is only required for PASSPORT, VOTER_ID, DRIVERS_LICENSE, NATIONAL_ID, TIN, and CAC. For the rest of the id types you can send through dob as null or empty.
      IdInfo idInfo = new IdInfo(
               "firstName", "middleName", "lastName", "country",
               "IdType", "idNumber", "dob", "phoneNumber"
      );

      // You can provide the image as a base encoded string or provide the path to the image
      List<ImageDetail> imageDetails = new ArrayList<>();
      ImageDetail selfie = new ImageDetail(ImageType.SELFIE_BASE64, "base64_encoded_image", null);
      ImageDetail idCard = new ImageDetail(ImageType.ID_CARD, null, "pathToImage");  
      imageDetails.add(selfie);
      imageDetails.add(idCard);

      Options options = new Options(returnHistory, returnImageLinks, returnJobStatus, "optionalCallback");
      WebApi webApi = new WebApi("partnerId", "apiKey", "defaultCallback", "sidServer");

      JobStatusResponse job = webApi.submitJob(partnerParams, imageDetails, idInfo, options);

  } catch (Exception e) {
      e.printStackTrace();
      throw e;
  }
```
This will first perform validation of IdInfo and PartnerParams. If you would like to skip this validation proccess use: 
```java
JobStatusResponse job = webApi.submitJob(partnerParams, imageDetails, idInfo, options, false);
```

In the case of a Job Type 5 you can simply omit the the images and options keys. Remember that the response is immediate, so there is no need to query the job_status. There is also no enrollment so no images are required. The response for a job type 5 can be found in the response section below.

```java
JobStatusResponse job = webApi.submitJob(partnerParams, idInfo);

```


**Response:**

Should you choose to *set returnJobStatus to false*, the response will be a JobStatusResponse containing:
```java
job.getJobComplete() == false;
job.getJobSuccess() == true;
job.getResult().getSmileId() == "smileJobId";
job.getResult().getPartnerParams()  == partnerParams; 
```

However, if you have *set returnJobStatus to true (with image_links and history)* then you will receive a JobStatusResponse created from this json:
```json
{
   "job_success":true,
   "result":{
      "ConfidenceValue":"99",
      "JSONVersion":"1.0.0",
      "Actions":{
         "Verify_ID_Number":"Verified",
         "Return_Personal_Info":"Returned",
         "Human_Review_Update_Selfie":"Not Applicable",
         "Human_Review_Compare":"Not Applicable",
         "Update_Registered_Selfie_On_File":"Not Applicable",
         "Liveness_Check":"Not Applicable",
         "Register_Selfie":"Approved",
         "Human_Review_Liveness_Check":"Not Applicable",
         "Selfie_To_ID_Authority_Compare":"Completed",
         "Selfie_To_ID_Card_Compare":"Not Applicable",
         "Selfie_To_Registered_Selfie_Compare":"Not Applicable"
      },
      "ResultText":"Enroll User",
      "IsFinalResult":"true",
      "IsMachineResult":"true",
      "ResultType":"SAIA",
      "PartnerParams":{
         "job_type":"1",
         "optional_info":"we are one",
         "user_id":"HBBBBBBH57g",
         "job_id":"HBBBBBBHg"
      },
      "Source":"WebAPI",
      "ResultCode":"0810",
      "SmileJobID":"0000001111"
   },
   "code":"2302",
   "job_complete":true,
   "signature":"HKBhxcv+1qaLy\C7PjVtk257dE=|1577b051a4313ed5e3e4d29893a66f966e31af0a2d2f6bec2a7f2e00f2701259",
   "history":[
      {
         "ConfidenceValue":"99",
         "JSONVersion":"1.0.0",
         "Actions":{
            "Verify_ID_Number":"Verified",
            "Return_Personal_Info":"Returned",
            "Human_Review_Update_Selfie":"Not Applicable",
            "Human_Review_Compare":"Not Applicable",
            "Update_Registered_Selfie_On_File":"Not Applicable",
            "Liveness_Check":"Not Applicable",
            "Register_Selfie":"Approved",
            "Human_Review_Liveness_Check":"Not Applicable",
            "Selfie_To_ID_Authority_Compare":"Completed",
            "Selfie_To_ID_Card_Compare":"Not Applicable",
            "Selfie_To_Registered_Selfie_Compare":"Not Applicable"
         },
         "ResultText":"Enroll User",
         "IsFinalResult":"true",
         "IsMachineResult":"true",
         "ResultType":"SAIA",
         "PartnerParams":{
            "job_type":"1",
            "optional_info":"we are one",
            "user_id":"HBBBBBBH57g",
            "job_id":"HBBBBBBHg"
         },
         "Source":"WebAPI",
         "ResultCode":"0810",
         "SmileJobID":"0000001111"
      }
   ],
   "image_links":{
      "selfie_image":"image_link"
   },
   "timestamp":"2019-10-10T12:32:04.622Z",
   "success": true,
   "smile_job_id": "0000001111"
}
```

You can also *view your response asynchronously at the callback* that you have set, it will look as follows:
```json
{
   "job_success":true,
   "result":{
      "ConfidenceValue":"99",
      "JSONVersion":"1.0.0",
      "Actions":{
         "Verify_ID_Number":"Verified",
         "Return_Personal_Info":"Returned",
         "Human_Review_Update_Selfie":"Not Applicable",
         "Human_Review_Compare":"Not Applicable",
         "Update_Registered_Selfie_On_File":"Not Applicable",
         "Liveness_Check":"Not Applicable",
         "Register_Selfie":"Approved",
         "Human_Review_Liveness_Check":"Not Applicable",
         "Selfie_To_ID_Authority_Compare":"Completed",
         "Selfie_To_ID_Card_Compare":"Not Applicable",
         "Selfie_To_Registered_Selfie_Compare":"Not Applicable"
      },
      "ResultText":"Enroll User",
      "IsFinalResult":"true",
      "IsMachineResult":"true",
      "ResultType":"SAIA",
      "PartnerParams":{
         "job_type":"1",
         "optional_info":"we are one",
         "user_id":"HBBBBBBH57g",
         "job_id":"HBBBBBBHg"
      },
      "Source":"WebAPI",
      "ResultCode":"0810",
      "SmileJobID":"0000001111"
   },
   "code":"2302",
   "job_complete":true,
   "signature":"HKBhxcv+1qaLy\C7PjVtk257dE=|1577b051a4313ed5e3e4d29893a66f966e31af0a2d2f6bec2a7f2e00f2701259",
   "history":[
      {
         "ConfidenceValue":"99",
         "JSONVersion":"1.0.0",
         "Actions":{
            "Verify_ID_Number":"Verified",
            "Return_Personal_Info":"Returned",
            "Human_Review_Update_Selfie":"Not Applicable",
            "Human_Review_Compare":"Not Applicable",
            "Update_Registered_Selfie_On_File":"Not Applicable",
            "Liveness_Check":"Not Applicable",
            "Register_Selfie":"Approved",
            "Human_Review_Liveness_Check":"Not Applicable",
            "Selfie_To_ID_Authority_Compare":"Completed",
            "Selfie_To_ID_Card_Compare":"Not Applicable",
            "Selfie_To_Registered_Selfie_Compare":"Not Applicable"
         },
         "ResultText":"Enroll User",
         "IsFinalResult":"true",
         "IsMachineResult":"true",
         "ResultType":"SAIA",
         "PartnerParams":{
            "job_type":"1",
            "optional_info":"we are one",
            "user_id":"HBBBBBBH57g",
            "job_id":"HBBBBBBHg"
         },
         "Source":"WebAPI",
         "ResultCode":"0810",
         "SmileJobID":"0000001111"
      }
   ],
   "image_links":{
      "selfie_image":"image_link"
   },
   "timestamp":"2019-10-10T12:32:04.622Z"
}
```

If you have queried a job type 5, your response be a JSON String that will contain the following:
```json
{
   "JSONVersion":"1.0.0",
   "SmileJobID":"0000001105",
   "PartnerParams":{
      "user_id":"T6yzdOezucdsPrY0QG9LYNDGOrC",
      "job_id":"FS1kd1dd15JUpd87gTBDapvFxv0",
      "job_type":5
   },
   "ResultType":"ID Verification",
   "ResultText":"ID Number Validated",
   "ResultCode":"1012",
   "IsFinalResult":"true",
   "Actions":{
      "Verify_ID_Number":"Verified",
      "Return_Personal_Info":"Returned"
   },
   "Country":"NG",
   "IDType":"PASSPORT",
   "IDNumber":"A12345678",
   "ExpirationDate":"2017-10-28",
   "FullName":"John Doe",
   "DOB":"1993-10-21",
   "Photo":"SomeBase64Image",
   "sec_key":"pjxsxEY69zEHjSPFvPEQTqu17vpZbw+zTNqaFxRWpYDiO+7wzKc9zvPU2lRGiKg7rff6nGPBvQ6rA7/wYkcLrlD2SuR2Q8hOcDFgni3PJHutij7j6ThRdpTwJRO2GjLXN5HHDB52NjAvKPyclSDANHrG1qb/tloO7x4bFJ7tKYE=|8faebe00b317654548f8b739dc631431b67d2d4e6ab65c6d53539aaad1600ac7",
   "timestamp":1570698930193
}
```

##### getWebToken method

This function provides the token to be used for authentication when using our hosted web session; it returns a stringified JSONObject containing an active web token.

```java
  String token = webApi.get_web_token(<Long timestamp>, <String userId>, <String jobId>, <Product product>);
```


#### ID Api Class

An API that lets you performs basic KYC Services including verifying an ID number as well as retrieve a user's Personal Information

Import the necessary dependant classes for ID Api:

```java
import smile.identity.core.IDApi;
```

##### submitJob method

Your call to the library will be similar to the below code snippet:
```java
  PartnerParams partnerParams = new PartnerParams(<JobType jobType>, <String userId>, <String jobId>,  <Map<String, Object> optionalInfo>);

  // Note dob is only required for PASSPORT, VOTER_ID, DRIVERS_LICENSE, NATIONAL_ID, TIN, and CAC. For the rest of the id types you can send through dob as null or empty.
  IdInfo idInfo = new IdInfo(<String firstName>, <String middleName>, <String lastName>, <String country>, <String idType>, <String idNumber>, <String dob>, <String phoneNumber>);

  IDApi idApi = new IDApi(<String partner_id>, <String decoded_version_of_api_key>, <String 0 || 1 || sidserver >);
  JobStatusResponse response = idApi.submitJob(partnerParams, idInfo);  
```

This will first perform validation of IdInfo and PartnerParams.
To disable validation against the SmileID services endpoint pass false for useValidationApi ( default is true).
This will still perform the basic validation for country, id type and id number for IdInfo

```java
JobStatusResponse response = idApi.submitJob(partnerParams, idInfo, false, options);
```

#### Signature Class


To calculate your signature first import the necessary class:
```java
import smile.identity.core.Signature;
```

##### getSignatureKey method

Then call the Signature class as follows:

```java
import smile.identity.core.Signature;

  Signature signature = new Signature(partnerId, apiKey);
  SignatureKey key = signature.getSignatureKey(timestamp); // where timestamp is optional
  long timestamp = key.getTimestamp();
  String signature = key.getSignature();
  String timestampString = key.getformattedTimestamp();
```

##### confirmSignature method

Use the confirmSignature method to confirm the signature you receive while interacting with our servers.

```java
import smile.identity.core.Signature;

Signature signature = new Signature(partnerId, apiKey);
boolean signatureConfirmed = signature.confirmSignature(timestamp, "receivedSignatureString");
```


#### Utilities Class

You may want to receive more information about a job. This is built into Web Api if you choose to set return_job_status as true in the options class. However, you also have the option to build the functionality yourself by using the Utilities class. Please note that if you are querying a job immediately after submitting it, you will need to poll it for the duration of the job.

```java
import smile.identity.core.Utilities;

Utilities utilities = new Utilities(<partnerId>, <the decoded-version of-your-api-key>, <sidServer>);
JobStatusResponse response = utilities.getJobStatus(<String userId>, <String jobId>, <Options options>);

```
This returns the job status as JobStatusResponse

You can also use the pollJobStatus to poll for a job status. It will retry until it the job is completed or it reaches max retries
The default retryCount = 3 and the default initialDelay = 2000L (milliseconds); 
```java
import smile.identity.core.Utilities;

Utilities utilities = new Utilities(<partnerId>, <the decoded-version of-your-api-key>, <sidServer>);
JobStatusResponse response = utilities.pollJobStatus(<String userId>, <String jobId>, <Options options>, <int retryCount>, <long initialDelay>);


```


## Development

Reference: https://guides.gradle.org/building-java-libraries/

After checking out the repo, run `gradle build` or `./gradlew build` to build. Ensure that you have a gradle.properties file setup with the necessary variables required by the build.

## Deployment

This is the https://issues.sonatype.org/browse/OSSRH-50589 that you can always reference for history.

#### Access Rights

For reference, find the Sonatype guides [here](https://central.sonatype.org/pages/ossrh-guide.html)

Create an account on [Sonatype](https://issues.sonatype.org/secure/Signup!default.jspa)
Thereafter, comment on the [original issue](https://issues.sonatype.org/browse/OSSRH-50589) (or make a new ticket) to allow access to new users.

#### GPG Keys

Follow these [instructions](https://central.sonatype.org/pages/working-with-pgp-signatures.html), however, you can find an overview of the instructions below:

- Check that you have [gpg](http://www.gnupg.org/download/) installed. You can do this by running `gpg --version`
- Next you want to create or generate a gpg key using `gpg —-gen-key`. You will need some basic details for this, like your name, email, and a passphrase. After entering your passphrase move your mouse etc. to gives the random number generator a better chance to gain enough entropy.
- To list the keys that you've now generated run `gpg --list-secret-keys --keyid-format LONG` or `gpg --list-keys --keyid-format LONG`
- Since others need your public key to verify your files, you have to distribute your public key to a key server `gpg --keyserver pool.sks-keyservers.net --send-keys <your KEYID>`,  but if you're using ipv4 you'll need to run $ gpg --keyserver ipv4.pool.sks-keyservers.net  --send-key <your KEYID>
- To double check your key is there run `gpg --keyserver ipv4.pool.sks-keyservers.net  --search-key <your key>`
Please note it takes some time to upload your key, so please wait some time before checking.


#### Accessing OSSHR and the Nexus Repo
If you go to https://oss.sonatype.org/ you can login using the username and password that you used for sonatype to access the nexus repo.

Additional Reading (not necessary for current setup):
In addition, the nexus repo has a feature that allows you to use credentials different from the username and password, no need to expose these credentials on the maven Repo in the settings.xml
- Go to profile, and dropdown to user token
    - Access token DNS (see in clear text)
    - You need to add your username and password
    - You can add this to your .env file
    - Authentication to deploy (more securer for deployments)
    - You have verifies that you can access the ossrh server  for deployment.
```
    <server>
      <id>${server}</id>
      <username>{username}</username>
      <password>{password}</password>
    </server>
```
Those credentials can be used from your build system to deploy to the repos in this OSS server (snapshots and release artefacts and ultimately get deploy to the central repository.

#### Gradle.properties

Setting up a consistent environment for your build is as simple as placing these settings into a gradle.properties file.

Create a gradle.properties file in the root of your projects (please never commit this file):

```
signing.keyId=<short ID specifically of your gpg key>
signing.password=<passphrase for your gpgp key>

# https://github.com/gradle/gradle/issues/888
signing.secretKeyRingFile=<path to your secret key ring - secring.gpg>

ossrhUsername=<your username for sonatype>
ossrhPassword=<your password for sonatype>
```

To obtain the above information:

1. keyId: run `gpg --list-keys --keyid-format SHORT`
2. passphrase: the passphrase you use to set up your gpg keys
3. secretKeyRingFile: With gpg2.1, it doesn't use secring.gpg file. Hence you need to [generate one as a workaround](https://github.com/gradle/gradle/issues/888), using `gpg --export-secret-keys >~/.gnupg/secring.gpg`
4. ossrhUsername: Your sonatype login ossrh username
5. ossrhPassword: Your sonatype login ossrh password

#### Build and Deploy
Reference: https://docs.gradle.org/current/userguide/publishing_overview.html#sec:basic_publishing


**Deploying a snapshot Version (this is a development version):**

You should deploy a snapshot version first before deploying to staging/production. It is good practice to first test your changes on the snapshot repo.

To do this:
1. Update the version number in the build.gradle file.
2. Make sure you do due diligence on the changelog.md too.
3. In your build.gradle file, find the version variable and update your version to the next version number with `-SNAPSHOT` added.
As an example on line 42 of the build.gradle change the version as follows: `version = "1.0.2-SNAPSHOT)`.
4. To now deploy run the task `./gradlew uploadArchives`
5. You can validate your SNAPSHOTs by using them in a build, or you can use the UI on oss.sonatype.org (find the Snapshots repo after clicking the "Repositories" link in the left-hand menu)
6. ask for access to the test-app if you are an internal staff member, you can test your build using the test app.
7. Once you are satisfied, deploy to staging and release to production.

**Deploying to staging and releasing to production:**

This will deploy to the staging repository where you will release to production.

1. Remove the snapshot version by removing `-SNAPSHOT` from the version. Make sure your version number is correct.
2. To now deploy run the task `./gradlew uploadArchives` again.
3. Log in to the nexus repo (with the same osshr details you've specified in the gradle.properties).
4. Go to the staging repositories and navigate to the last item.
5. To release to the production repository, click on the comsmileidentity repository table item. Then click on `Close` on the top bar. Add the description from the changelog for that version.
6. Thereafter you will see a sign in the activity section at the bottom that reads `Operation in Progress`. The activity section on the repo will tell you if there are any failures. Wait a minute before clicking on the `Refresh` button on the top bar.
7. You should now see the activity change to `Last Operation completed successfully`, and the `Release` button on the top menu bar will enable.
Now, click the `Release` button to release the repository from staging to production. You may keep the `Automatically Drop` checked. Add the description from the changelog for that version.  
8. `Refresh` again and the staging repositories should be empty for Smile. The synchronisation may take a little while, but it will appear [here](https://search.maven.org/search?q=g:com.smileidentity).
9. You should tag the release on github too. You can do this as follows, as an example:
```
git tag -a '1.0.2' -m 'Add {"success":true,"smile_job_id":"job_id"} to the response when we poll job status too'
git push origin --tags
```


## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/smileidentity/smile-identity-core-java

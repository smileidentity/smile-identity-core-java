# SmileIdentityCore

The official Smile Identity gem exposes four classes namely; the Web Api class, the ID Api class, the Signature class and the Utilities class.

The **Web Api Class** allows you as the Partner to validate a user’s identity against the relevant Identity Authorities/Third Party databases that Smile Identity has access to using ID information provided by your customer/user (including photo for compare). It has the following public methods:
- submit_job
- get_job_status

The **ID Api Class** lets you performs basic KYC Services including verifying an ID number as well as retrieve a user's Personal Information. It has the following public methods:
- submit_job

The **Signature Class** allows you as the Partner to generate a sec key to interact with our servers. It has the following public methods:
- generate_sec_key
- confirm_sec_key

The **Utilities Class** allows you as the Partner to have access to our general Utility functions to gain access to your data. It has the following public methods:
- get_job_status

## Documentation

This gem requires specific input parameters, for more detail on these parameters please refer to our [documentation for Web API](https://docs.smileidentity.com/products/web-api/java).

Please note that you will have to be a Smile Identity Partner to be able to query our services. You can sign up on the [Portal](https://test-smileid.herokuapp.com/signup?products[]=1-IDVALIDATION&products[]=2-AUTHENTICATION).

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
import smile.identity.core.PartnerParameters;
import smile.identity.core.ImageParameters;
import smile.identity.core.IDParameters;
import smile.identity.core.Options;
import smile.identity.core.WebApi;
```

##### submit_job method

Your call to the library will be similar to the below code snippet:
```java
  try {
    PartnerParameters partnerParameters = new PartnerParameters("1", "1", 1);
    partnerParameters.add("optional_info", "some optional info");

    // Note dob is only required for PASSPORT, VOTER_ID, DRIVERS_LICENSE, NATIONAL_ID, TIN, and CAC. For the rest of the id types you can send through dob as null or empty.
    IDParameters idInfo = new IDParameters(<String firstName>, <String middleName>, <String lastName>, <String country>, <String idType>, <String idNumber>, <String dob>, <String phoneNumber>, <String entered>);

    ImageParameters imageParameters = new ImageParameters();
    imageParameters.add(0, "../download.png");

    Options options = new Options("optional_callback.com", true, false, false);

    WebApi connection = new WebApi("125", "default_callback.com", "<the decoded-version of-your-api-key>", 0);

    String response = connection.submit_job(partnerParameters.get(), imageParameters.get(), idInfo.get(), options.get());

    System.out.println("\n Response" + response);

  } catch (Exception e) {
    e.printStackTrace();
    throw e;
  }
```

Please note that if you do not need to pass through IDParameters or Options, you may omit calling those class and send through null in submit_job, as follows:
```
String response = connection.submit_job(partnerParameters.get(), imageParameters.get(), null, null);
```

In the case of a Job Type 5 you can simply omit the the images and options keys. Remember that the response is immediate, so there is no need to query the job_status. There is also no enrollment so no images are required. The response for a job type 5 can be found in the response section below.

```
$ response = connection.submit_job(partner_params, null, id_info, null);
```

or, you can omit the two null parameters:
```
$ response = connection.submit_job(partner_params, id_info);
```


**Response:**

Should you choose to *set return_job_status to false*, the response will be a JSON String containing:
```
{success: true, smile_job_id: smile_job_id}
```

However, if you have *set return_job_status to true (with image_links and history)* then you will receive JSON Object response like below:
```
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
```
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
```
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
   "IDNumber":"A04150107",
   "ExpirationDate":"2017-10-28",
   "FullName":"ADEYEMI KEHINDE ADUNOLA",
   "DOB":"1989-09-20",
   "Photo":"SomeBase64Image",
   "sec_key":"pjxsxEY69zEHjSPFvPEQTqu17vpZbw+zTNqaFxRWpYDiO+7wzKc9zvPU2lRGiKg7rff6nGPBvQ6rA7/wYkcLrlD2SuR2Q8hOcDFgni3PJHutij7j6ThRdpTwJRO2GjLXN5HHDB52NjAvKPyclSDANHrG1qb/tloO7x4bFJ7tKYE=|8faebe00b317654548f8b739dc631431b67d2d4e6ab65c6d53539aaad1600ac7",
   "timestamp":1570698930193
}
```

##### get_job_status method

Sometimes, you may want to get a particular job status at a later time. You may use the get_job_status function to do this:

You will already have your Web Api class initialised as follows:
```java
  WebApi connection = new WebApi(<String partner_id>, <String default_callback_url>, <String decoded_version_of_api_key>, <Integer 0 || 1>);
```
Thereafter, simply call get_job_status with the correct parameters using the classes we have provided:
```java
  // create the stringified json for the partner params using our class (i.e. user_id, job_id, and job_type that you would are querying)
  PartnerParameters partnerParameters = new PartnerParameters(<String user_id>, <String job_id>, <Integer job_type>);

  // create the options - whether you would like to return_history and return_image_links in the job status response
  Options job_status_options = new Options(<Boolean return_history>, <Boolean return_image_links>);

  response = connection.get_job_status(partnerParameters.get(), job_status_options.get());
```

Please note that if you do not need to pass through Options if you will not be using them, you may omit calling those class and send through null instead:
```
String response = connection.get_job_status(partnerParameters.get(), null);
```

#### ID Api Class

An API that lets you performs basic KYC Services including verifying an ID number as well as retrieve a user's Personal Information

Import the necessary dependant classes for ID Api:

```java
import smile.identity.core.PartnerParameters;
import smile.identity.core.IDParameters;
import smile.identity.core.IDApi;
```

##### submit_job method

Your call to the library will be similar to the below code snippet:
```java
  PartnerParameters partnerParameters = new PartnerParameters(<String user_id>, <String job_id>, <Integer 5>);

  // Note dob is only required for PASSPORT, VOTER_ID, DRIVERS_LICENSE, NATIONAL_ID, TIN, and CAC. For the rest of the id types you can send through dob as null or empty.
  IDParameters idInfo = new IDParameters(<String firstName>, <String middleName>, <String lastName>, <String country>, <String idType>, <String idNumber>, <String dob>, <String phoneNumber>);
  // Note that entered is not required for ID API

  IDApi connection = new IDApi(<String partner_id>, <String decoded_version_of_api_key>, <Integer 0 || 1>);
  String response = connection.submit_job(partnerParameters.get(), idInfo.get());  
```

**Response**

Your response will return a JSON String containing the below:
```
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
   "IDNumber":"A04150107",
   "ExpirationDate":"2017-10-28",
   "FullName":"ADEYEMI KEHINDE ADUNOLA",
   "DOB":"1989-09-20",
   "Photo":"SomeBase64Image",
   "sec_key":"pjxsxEY69zEHjSPFvPEQTqu17vpZbw+zTNqaFxRWpYDiO+7wzKc9zvPU2lRGiKg7rff6nGPBvQ6rA7/wYkcLrlD2SuR2Q8hOcDFgni3PJHutij7j6ThRdpTwJRO2GjLXN5HHDB52NjAvKPyclSDANHrG1qb/tloO7x4bFJ7tKYE=|8faebe00b317654548f8b739dc631431b67d2d4e6ab65c6d53539aaad1600ac7",
   "timestamp":1570698930193
}
```

#### Signature Class

To calculate your signature first import the necessary class:
```java
import smile.identity.core.Signature;
```

##### generate_sec_key method

Then call the Signature class as follows:

```java
import smile.identity.core.Signature;

try {
  Signature connection = new Signature(partner_id, api_key);
  String signatureJsonStr = connection.generate_sec_key(timestamp); // where timestamp is optional

  // In order to utilise the signature you can then use a json parser and extract the signature
} catch (Exception e) {
  e.printStackTrace();
  throw e;
}
```

The response will be a stringified json object:
```java
{
  sec_key: "<the generated sec key>",
 timestamp: "<timestamp that you passed in or that was generated>"
}
```

##### confirm_sec_key method

You can also confirm the signature that you receive when you interacting with our servers, simply use the confirm_sec_key method which returns a boolean:

```java
import smile.identity.core.Signature;

try {
  Signature connection = new Signature(partner_id, api_key);
  String signatureJsonStr = connection.confirm_sec_key(sec_key, timestamp);
  // If it is valid then use the response, else throw an error
} catch (Exception e) {
  e.printStackTrace();
  throw e;
}
```

#### Utilities Class

You may want to receive more information about a job. This is built into Web Api if you choose to set return_job_status as true in the options class. However, you also have the option to build the functionality yourself by using the Utilities class. Please note that if you are querying a job immediately after submitting it, you will need to poll it for the duration of the job.

```java
import smile.identity.core.Utilities;

String job_status = new Utilities(<partner_id>, <the decoded-version of-your-api-key>, <sid_server>).get_job_status(<user_id>, <job_id>, <return_image_links> , <return_history>);

System.out.println(job_status);
```

This returns the job status as stringified json data.

## Development

After checking out the repo, run `gradle build` to build. Ensure that you have a gradle.properties file setup with the necessary variables required by the build.

## Deployment

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


#### Accessing OSSHR
If you go to https://oss.sonatype.org/ you can login using the username and password that you used for sonatype to access the nexus repo.

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

- keyId: run `gpg --list-keys --keyid-format SHORT`
- passphrase: the passphrase you use to set up your gpg keys
- secretKeyRingFile: With gpg2.1, it doesn't use secring.gpg file. Hence you need to [generate one as a workaround](https://github.com/gradle/gradle/issues/888), using `gpg --export-secret-keys >~/.gnupg/secring.gpg`

-ossrhUsername: Your sonatype login ossrh username
-ossrhPassword: Your sonatype login ossrh password

#### Build and Deploy
Reference: https://docs.gradle.org/current/userguide/publishing_overview.html#sec:basic_publishing

Update the version number in the build file. Make sure you do due diligence on the changelog too.
It is good practice to first test your changes on the snapshot repo (add -SNAPSHOT to your version), and thereafter deploy to the release repository.

To deploy run the task `./gradlew uploadArchives`
This will deploy to the staging release repository.
Log in to the nexus repo (with the same osshr details you've specified in the gradle.properties) to inspect the deploy.
Go to the staging repositories and navigate to the last item.

To release to the production repository, close the staging repository, then release.
Refresh the interface using the button.
Now check the activity on the repo to ensure that there are no failures.
Once the release button is available (this may take a little while), click release.
The synchronisation may take a little while, but it will appear [here](https://search.maven.org/search?q=g:com.smileidentity).
<!--  -->
Please note that you should tag the release on github too.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/smileidentity/smile-identity-core-java

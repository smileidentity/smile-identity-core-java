# SmileIdentityCore

The official Smile Identity gem exposes two classes namely; the Web API class, and the Signature class.

The **Web API Class** allows you as the Partner to validate a user’s identity against the relevant Identity Authorities/Third Party databases that Smile Identity has access to using ID information provided by your customer/user (including photo for compare). It has the following public methods:
- submit_job
- get_job_status

The **Signature Class** allows you as the Partner to generate a sec key to interact with our servers. It has the following public methods:
- generate_sec_key
- confirm_sec_key

<!-- The **Utilities Class** allows you as the Partner to have access to our general Utility functions to gain access to your data. It has the following public methods:
- get_job_status -->

## Documentation

This gem requires specific input parameters, for more detail on these parameters please refer to our [documentation for Web API](https://docs-smileid.herokuapp.com/docs#web-api-introduction).

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

Your call to the library will be similar to the below code snippet:
```java
  try {
    PartnerParameters partnerParameters = new PartnerParameters("1", "1", 1);
    partnerParameters.add("optional_info", "some optional info");

    // Note dob is only required for PASSPORT, VOTER_ID, DRIVERS_LICENSE, NATIONAL_ID, TIN, and CAC. For the rest of the id types you can send through dob as null or empty.
    IDParameters idInfo = new IDParameters("John", "", "Doe", "NG", "BVN", "00000000", "", "true");

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

The response will be nil if you chose to set return_job_status to false, however if you have set return_job_status to true then you will receive a response like below:

```
{
  "timestamp": "2018-03-13T21:04:11.193Z",
  "signature": "<your signature>",
  "job_complete": true,
  "job_success": true,
  "result": {
    "ResultText": "Enroll User",
    "ResultType": "SAIA",
    "SmileJobID": "0000001897",
    "JSONVersion": "1.0.0",
    "IsFinalResult": "true",
    "PartnerParams": {
      "job_id": "52d0de86-be3b-4219-9e96-8195b0018944",
      "user_id": "e54e0e98-8b8c-4215-89f5-7f9ea42bf650",
      "job_type": 4
    },
    "ConfidenceValue": "100",
    "IsMachineResult": "true",
  }
  "code": "2302"
}
```
You can also view your response asynchronously at the callback that you have set, it will look as follows:
```
{
  "ResultCode": "1220",
  "ResultText": "Authenticated",
  "ResultType": "DIVA",
  "SmileJobID": "0000000001",
  "JSONVersion": "1.0.0",
  "IsFinalResult": "true",
  "PartnerParams": {
    "job_id": "e7ca3e6c-e527-7165-b0b5-b90db1276378",
    "user_id": "07a0c120-98d7-4fdc-bc62-3c6bfd16c60e",
    "job_type": 2
  },
  "ConfidenceValue": "100.000000",
  "IsMachineResult": "true"
}
```

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




#### Signature Class

To calculate your signature first import the necessary class:
```java
import smile.identity.core.Signature;
```

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

<!-- #### Utilities Class

You may want to receive more information about a job. This is built into Web Api if you choose to set return_job_status as true in the options class. However, you also have the option to build the functionality yourself by using the Utilities class. Please note that if you are querying a job immediately after submitting it, you will need to poll it for the duration of the job.

```java
import smile.identity.core.Utilities;

String job_status = new Utilities(<partner_id>, <the decoded-version of-your-api-key>, <sid_server>).get_job_status(<user_id>, <job_id>, <return_image_links> , <return_history>);

System.out.println(job_status);
```

This returns the job status as stringified json data. -->

## Development

After checking out the repo, run `gradle build` to build. Ensure that you have a gradle.properties file setup with the necessary variables required by the build.


## Deployment
Update the version number in the build file
It is good practice to first test your changes on the snapshot repo (add -SNAPSHOT to your version), and thereafter deploy to the release repository.

To deploy run the task `./gradlew uploadArchives`

Please note that you should tag the release when doing a push to maven.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/smileidentity/smile-identity-core-java

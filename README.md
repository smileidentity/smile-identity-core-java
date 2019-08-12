# SmileIdentityCore

The official Smile Identity gem exposes two classes namely, the Web API and AuthSmile class.

The Web API allows you as the Partner to validate a user’s identity against the relevant Identity Authorities/Third Party databases that Smile Identity has access to using ID information provided by your customer/user (including photo for compare).

## Documentation

This gem requires specific input parameters, for more detail on these parameters please refer to our [documentation for Web API](https://docs-smileid.herokuapp.com/docs#web-api-introduction).

Please note that you will have to be a Smile Identity Partner to be able to query our services.
## Usage

Add the group, name and version to your application's build file, it will look similar based on your build tool:

```java
group: "com.smileidentity", name: "smile-identity-core", version: "<current-version>"
```
Thereafter, import the necessary classes:

```java
import smile.identity.core.PartnerParameters;
import smile.identity.core.ImageParameters;
import smile.identity.core.IDParameters;
import smile.identity.core.Options;
import smile.identity.core.WebApi;
```

#### Web api

Your call to the library will be similar to the below code snippet:
```java
  try {
    PartnerParameters partnerParameters = new PartnerParameters("1", "1", 1);
    partnerParameters.add("optional_info", "some optional info");

    IDParameters idInfo = new IDParameters("John", "", "Doe", "NG", "BVN", "00000000", "true");

    ImageParameters imageParameters = new ImageParameters();
    imageParameters.add(0, "../download.png");

    Options options = new Options("optional_callback.com", true, false, false);

    WebApi connection = new WebApi("125", "default_callback.com", "<your-api-key>", 0);

    String response = connection.submit_job(partnerParameters.get(), imageParameters.get(), idInfo.get(), options.get());

    System.out.println("\n Response" + response);

  } catch (Exception e) {
    e.printStackTrace();
  }
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

## Development

After checking out the repo, run `gradle build` to build. ensure that you have a gradle.properties file setup with the necessary variables required by the build. To deploy to staging run the task `./gradlew uploadArchives`

Please note that you should tag the release when doing a push to maven.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/smileidentity/smile-identity-core-java

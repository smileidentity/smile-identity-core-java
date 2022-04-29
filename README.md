#Smile Identity PHP Server Side

Smile Identity provides the best solutions for real time Digital KYC, identity verification, user onboarding, and user authentication across Africa. Our server side libraries make it easy to integrate us on the server-side. Since the library is server-side, you will be required to pass the images (if required) to the library.
If you haven’t already, sign up for a free Smile Identity account, which comes with Sandbox access.

Please see [changelog.md](changelog.md). for release versions and changes

##Features

The library exposes four classes namely; the WebApi class, the IDApi class, the Signature class, and the Utilities class.

The WebApi class has the following public methods:
submit_job - handles submission of any of Smile Identity products that requires an image i.e. Enhanced KYC + SmartSelfieTM, Document Verification and SmartSelfieTM Authentication.
get_web_token - handles generation of web token, if you are using the Hosted Web Integration.

The IDApi class has the following public method:
submit_job - handles submission of Enhanced KYC and Identity Verification.

The Signature class has the following public methods:
generate_signature - generate a signature which is then passed as a signature param when making requests to the Smile Identity server
confirm_signature - ensure a response is truly from the Smile Identity server by confirming the incoming signature

The Utilities Class allows you as the Partner to have access to our general Utility functions to gain access to your data. It has the following public methods:
get_job_status - retrieve information & results of a job. Read more on job status in the Smile Identity documentation.
get_smile_id_services - general information about different smile identity products such as required inputs for each supported id type.

##Dependencies

* Java 7 and above
* Gradle/Maven build tool

##Documentation

For extensive instructions on usage of the library and sample codes, please refer to the [official Smile Identity documentation](https://docs.smileidentity.com/server-to-server/java)

##Installation

View the package on [Maven](https://search.maven.org/search?q=a:smile-identity-core).

Add the group, name and version to your application's build file, it will look similar based on your build tool:

```java
group: "com.smileidentity", name: "smile-identity-core", version: "<current-version>"
```

##License

MIT License

##Documentation

For extensive instructions on usage of the library and sample codes, please refer to the official Smile Identity documentation <add hyperlink to the page in docs specific to library>.

##Getting Help

For usage questions, the best resource is [our official documentation](docs.smileidentity.com). However, if you require further assistance, you can file a [support ticket via our portal](https://portal.smileidentity.com/partner/support/tickets) or visit the [contact us page](https://portal.smileidentity.com/partner/support/tickets) on our website.

##Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/smileidentity/smile-identity-core-java

Please format the code with [black](https://github.com/psf/black) prior to submitting pull requests, by running `black .` from the project's root.

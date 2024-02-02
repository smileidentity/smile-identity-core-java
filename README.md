# Smile Identity Java Server Side SDK

Smile Identity provides the best solutions for real time Digital KYC, identity verification, user onboarding, and user authentication across Africa. Our server side libraries make it easy to integrate us on the server-side. Since the library is server-side, you will be required to pass the images (if required) to the library.


 If you haven’t already, [sign up for a free Smile Identity account](https://usesmileid.com/talk-to-an-expert), which comes with Sandbox access.


Please see [changelog.md](https://github.com/smileidentity/smile-identity-core-java/blob/master/changelog.md). for release versions and changes


## Features
The library exposes four classes namely; the WebApi class, the IDApi class, the Signature class, and the Utilities class.

The WebApi class has the following public methods:
 - `submitJob` -  handles submission of any of Smile Identity products that requires an image i.e. [Biometric KYC](https://docs.usesmileid.com/products/biometric-kyc), [Document Verification](https://docs.usesmileid.com/products/document-verification), [SmartSelfieTM  Authentication](https://docs.usesmileid.com/products/biometric-authentication) and [Business Verification](https://docs.usesmileid.com/products/for-businesses-kyb/business-verification).
 - `getWebToken` - handles generation of web token, if you are using the [Hosted Web Integration](https://docs.usesmileid.com/web-mobile-web/web-integration-beta).

 The IDApi class has the following public method:

- `submitJob` - handles submission of [Enhanced KYC](https://docs.usesmileid.com/products/identity-lookup) and [Basic KYC](https://docs.usesmileid.com/products/id-verification).

The Signature class has the following public methods:

- `getSignatureKey` - generate a signature which is then passed as a signature param when making requests to the Smile Identity server.
- `confirmSignature` - ensure a response is truly from the Smile Identity server by confirming the incoming signature.

The Utilities Class allows you as the Partner to have access to our general Utility functions to gain access to your data. It has the following public methods:

- `getJobStatus` - retrieve information & results of a job. Read more on job status in the [Smile Identity documentation](https://docs.usesmileid.com/further-reading/job-status).

## Installation

View the package on [Maven](https://search.maven.org/search?q=a:smile-identity-core)

Add the group, name and version to your application's build file, it will look similar based on your build tool:

```java
group: "com.smileidentity", name: "smile-identity-core", version: "<current-version>"
```

## Documentation

For extensive instructions on usage of the library and sample codes, please refer to the official Smile Identity [documentation](https://docs.usesmileid.com/server-to-server/java).

Before that, you should take a look at the examples in the [examples](/examples) folder.

## Getting Help

For usage questions, the best resource is [our official documentation](https://docs.usesmileid.com). However, if you require further assistance, you can file a [support ticket via our portal](https://portal.usesmileid.com/partner/support/tickets) or visit the [contact us page](https://usesmileid.com/company/contact-us) on our website.

## Contributing

Bug reports and pull requests are welcome on GitHub [here](https://github.com/smileidentity/smile-identity-core-java).

## License

MIT License

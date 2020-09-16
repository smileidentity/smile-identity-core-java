# Changelog
All notable changes to this project will be documented in this file.

## [0.0.2] - 2019-08-12
### Added
The first release version of Web Api.

## [0.0.3] - 2019-09-05
### Updated
Add a signature class
Confirm the signature when querying the job status
Ensure that we allow null for options and idInfo
Add dob for idInfo
Allow some null and empty strings for idInfo parameters
Update the docs
Add a get_job_status public method to the Web API class
Add a Utilities class we use to query job status as its own function

## [0.0.4] - 2019-09-17
Move the language key to the package information section

## [1.0.0] - 2019-10-07
Amend the success response to be a stringified json of {"success":true,"smile_job_id":"job_id"}

## [1.0.1] - 2019-10-10
Add the ID API Class
Add the ability to query ID Api from the Web API class
Add an extra required parameter of phone_number to ID Info params (can be null)
Allow for entered parameter to be left out (specifically for ID API)
Update the documentation to include Web API (job type 5) and ID API
Remove the id_info validations for Web API (only validate the id_number, id_type and country)

## [1.0.2] - 2020-01-16
Add {"success":true,"smile_job_id":"job_id"} to the response when we poll job status too

## [1.0.3] - 2020-05-29
Added support for java 8 and up 
Gradle from version 4 fully supports java 8 and gradle version 3 will work with java 8 but has limited support so
###  IMPORTANT
IDParameters() has two constructors please use the 8 parameters for Job Type 5 and  the 9 parameters constructor for Job Type 1
the difference being the entered parameter

####  JOB TYPE 1
public IDParameters(String first_name, String middle_name, String last_name, String country, String id_type, String id_number, String dob, String phone_number, String entered)

#### JOB TYPE 5  
public IDParameters(String first_name, String middle_name, String last_name, String country, String id_type, String id_number, String dob, String phone_number)

## [1.0.4] - 2020-06-23
Allow more image_type_id specifically 4 and 6 for more information on this please see https://docs.smileidentity.com/products/web-api/core-libraries
Also removed validations on id or selfie images

## [1.0.5] - 2020-08-05
* Fixed jobs to make sure all job information is related to one thread and instance of all the classes
* Added smile services endpoint to assist with validation
* Fixed possible crash when  passing null callback_url to WebApi  class

## [1.0.6] - 2020-09-16
* Fixed the empty  add methods on parameters to the server, should be able to handle empty cases and throw errors if empty or  null  
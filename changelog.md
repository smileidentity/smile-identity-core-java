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

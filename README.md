# Income Tax View and Change Microservice

[![Build Status](https://travis-ci.org/hmrc/income-tax-view-change.svg)](https://travis-ci.org/hmrc/income-tax-view-change) [ ![Download](https://api.bintray.com/packages/hmrc/releases/income-tax-view-change/images/download.svg) ](https://bintray.com/hmrc/releases/income-tax-view-change/_latestVersion)

This is the protected backend for the Quarterly Reporting Service (MTD ITSA). 

Frontend: https://github.com/hmrc/income-tax-view-change-frontend
Stub: https://github.tools.tax.service.gov.uk/hmrc/itvc-dynamic-stub


## APIs

### **GET** /income-tax-view-change/estimated-tax-liability/**:nino**/**:year**/**:calcType**

Where:

* **:nino** is a valid NINO in format XX999999X, for example: "AB123456C"
* **:year** is a four-digit tax year in format YYYY, for example: (for 06/04/2016 to 05/04/2017 supply "2017")
* **:calcType** should currently always be set to "it" for Income Tax

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{   
   "calcID": "00abcdef",
   "calcTimestamp": "2017-12-17T09:30:47Z",
   "calcAmount": 99999999.99
}
```
Where:
* **calcID** is 8 characters alphanumeric
* **calcTimestamp** is format date-time
* **calcAmount** is Number(10,2)

#### Error Responses

##### INVALID_NINO
* **Status**: 400
* **Body**: `{status: "400",meesage: "Submission has not passed validation. Invalid parameter NINO."}`

##### NOT_FOUND_NINO
* **Status**: 404
* **Body**: `{status: "400",meesage: "The remote endpoint has indicated that no data can be found for the given NINO."}`

#### SERVER_ERROR
* **Status**: 500
* **Body**: `{status: "400",meesage: "DES is currently experiencing problems that require live serviceintervention"}`

#### SERVICE_UNAVAILABLE
* **Status**: 503
* **Body**: `{status: "400",meesage: "Dependent systems are currently not responding"}`


Requirements
------------

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.


## Run the application


To update from Nexus and start all services from the RELEASE version instead of snapshot

```
sm --start ITVC_ALL -f
```


### To run the application locally execute the following:

Kill the service ```sm --stop INCOME_TAX_VIEW_CHANGE``` and run:
```
sbt 'run 9082'
```



## Test the application

To test the application execute

```
sbt clean scalastyle coverage test it:test coverageOff coverageReport
```



### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")

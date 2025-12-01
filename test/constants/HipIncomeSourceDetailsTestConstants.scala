/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package constants

import constants.BaseTestConstants._
import constants.HipBusinessDetailsTestConstants._
import constants.HipPropertyDetailsTestConstants._
import models.hip.core.{NinoErrorModel, NinoModel}
import models.hip.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel}
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse

object HipIncomeSourceDetailsTestConstants {

  val testIncomeSourceDetailsModel = IncomeSourceDetailsModel(
    nino = testNino,
    mtdbsa = testMtdId,
    yearOfMigration = Some("2019"),
    businesses = List(testBusinessDetailsModel, testMinimumBusinessDetailsModel),
    properties = List(testPropertyDetailsModel)
  )

  val testMinimumIncomeSourceDetailsModel = IncomeSourceDetailsModel(
    nino = testNino,
    mtdbsa = testMtdId,
    yearOfMigration = None,
    businesses = List(),
    properties = List()
  )

  val testHipIncomeSourceDetailsJson = Json.obj(
    "success" -> Json.obj(
    "taxPayerDisplayResponse" -> Json.obj(
      "safeId" -> "XAIT12345678908",
      "nino" -> testNino,
      "mtdId" -> testMtdId,
      "yearOfMigration" -> "2019",
      "businessData" -> Json.arr(testBusinessDetailsJson, testMinimumBusinessDetailsJson),
      "propertyData" -> Json.arr(testPropertyDetailsJson))
  ))

  val testIncomeSourceDetailsToJson = Json.parse("""{
               |  "nino" : "BB123456A",
               |  "mtdbsa" : "XIAT0000000000A",
               |  "yearOfMigration" : "2019",
               |  "businesses" : [ {
               |    "incomeSourceId" : "111111111111111",
               |    "incomeSource" : "Fruit Ltd",
               |    "accountingPeriod" : {
               |      "start" : "2017-06-01",
               |      "end" : "2018-05-31"
               |    },
               |    "tradingName" : "Test Business",
               |    "address" : {
               |      "addressLine1" : "Test Lane",
               |      "addressLine2" : "Test Unit",
               |      "addressLine3" : "Test Town",
               |      "addressLine4" : "Test City",
               |      "postCode" : "TE5 7TE",
               |      "countryCode" : "GB"
               |    },
               |    "contactDetails" : {
               |      "phoneNumber" : "01332752856",
               |      "mobileNumber" : "07782565326",
               |      "faxNumber" : "01332754256",
               |      "emailAddress" : "stephen@manncorpone.co.uk"
               |    },
               |    "contextualTaxYear" : "2015",
               |    "tradingStartDate" : "2017-01-01",
               |    "seasonal" : true,
               |    "cessation" : {
               |      "date" : "2017-06-01"
               |    },
               |    "paperless" : true,
               |    "firstAccountingPeriodEndDate" : "2016-01-01",
               |    "latencyDetails" : {
               |      "latencyEndDate" : "2022-01-01",
               |      "taxYear1" : "2022",
               |      "latencyIndicator1" : "A",
               |      "taxYear2" : "2023",
               |      "latencyIndicator2" : "Q"
               |    },
               |    "quarterTypeElection" : {
               |      "quarterReportingType" : "STANDARD",
               |      "taxYearofElection" : "2021"
               |    }
               |  }, {
               |    "incomeSourceId" : "111111111111111",
               |    "incomeSource" : "Fruit Ltd",
               |    "accountingPeriod" : {
               |      "start" : "2017-06-01",
               |      "end" : "2018-05-31"
               |    }
               |  } ],
               |  "properties" : [ {
               |    "incomeSourceId" : "111111111111111",
               |    "accountingPeriod" : {
               |      "start" : "2017-06-01",
               |      "end" : "2018-05-31"
               |    },
               |    "contactDetails" : {
               |      "emailAddress" : "stephen@manncorpone.co.uk"
               |    },
               |    "propertiesRented" : {
               |      "uk" : 3,
               |      "eea" : 2,
               |      "nonEea" : 1,
               |      "total" : 4
               |    },
               |    "cessation" : {
               |      "date" : "2017-06-01"
               |    },
               |    "paperless" : true,
               |    "firstAccountingPeriodEndDate" : "2016-01-01",
               |    "incomeSourceType" : "uk-property",
               |    "contextualTaxYear" : "2015",
               |    "tradingStartDate" : "2015-01-01",
               |    "latencyDetails" : {
               |      "latencyEndDate" : "2022-01-01",
               |      "taxYear1" : "2022",
               |      "latencyIndicator1" : "A",
               |      "taxYear2" : "2023",
               |      "latencyIndicator2" : "Q"
               |    },
               |    "quarterTypeElection" : {
               |      "quarterReportingType" : "STANDARD",
               |      "taxYearofElection" : "2021"
               |    }
               |  } ]
               |}
               |""".stripMargin
  )

  val testHipMinimumIncomeSourceDetailsJson = Json.obj(
    "success" -> Json.obj(
    "taxPayerDisplayResponse" -> Json.obj(
      "nino" -> testNino,
      "mtdId" -> testMtdId
    )
  ))

  val testNinoModel = NinoModel(testNino)
  val testIncomeSourceDetailsError = IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
  val testNinoError = NinoErrorModel(Status.INTERNAL_SERVER_ERROR, "Dummy error message")

  val successResponse = HttpResponse(Status.OK, testHipIncomeSourceDetailsJson, Map.empty)
  val badJson = HttpResponse(Status.OK, Json.toJson("{}"), Map.empty)
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
  val notFoundBadResponse = HttpResponse(Status.NOT_FOUND, "Dummy error message")
}

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

import constants.HipAccountingPeriodTestConstants._
import constants.HipCessationTestConstants._
import constants.HipContactDetailsTestConstants._
import models.hip.incomeSourceDetails.{LatencyDetails, PropertiesRentedModel, PropertyDetailsModel, QuarterTypeElection}
import play.api.libs.json.Json

import java.time.LocalDate

object HipPropertyDetailsTestConstants {

  val testPropertiesRentedModel = PropertiesRentedModel(
    uk = Some(3),
    eea = Some(2),
    nonEea = Some(1),
    total = Some(4)
  )

  val testPropertyDetailsModel = PropertyDetailsModel(
    incomeSourceId = "111111111111111",
    accountingPeriod = testAccountingPeriodModel,
    contactDetails = Some(testSomeContactDetailsModel),
    propertiesRented = Some(testPropertiesRentedModel),
    cessation = Some(testCessationModel),
    paperless = Some(true),
    firstAccountingPeriodEndDate = Some(LocalDate.of(2016, 1, 1)),
    incomeSourceType = Some("uk-property"),
    contextualTaxYear = Some("2015"),
    tradingStartDate = Some(LocalDate.parse("2015-01-01")),
    latencyDetails = Some(LatencyDetails(
      latencyEndDate = LocalDate.of(2022, 1, 1),
      taxYear1 = "2022",
      latencyIndicator1 = "A",
      taxYear2 = "2023",
      latencyIndicator2 = "Q")),
    cashOrAccruals = Some(true),
    quarterTypeElection = Some(QuarterTypeElection("STANDARD", "2021"))
  )

  val testMinimumPropertyDetailsModel = PropertyDetailsModel(
    incomeSourceId = "111111111111111",
    accountingPeriod = testAccountingPeriodModel,
    contactDetails = None,
    propertiesRented = None,
    cessation = None,
    paperless = None,
    firstAccountingPeriodEndDate = None,
    incomeSourceType = None,
    contextualTaxYear = None,
    tradingStartDate = None,
    latencyDetails = None,
    cashOrAccruals = Some(true),
    quarterTypeElection = None
  )


  val testPropertiesRentedJson = Json.obj(
    "numPropRented" -> 4,
    "numPropRentedUK" -> 3,
    "numPropRentedEEA" -> 2,
    "numPropRentedNONEEA" -> 1
  )

  val testPropertiesRentedJsonString = Json.obj(
    "numPropRented" -> "4",
    "numPropRentedUK" -> "3",
    "numPropRentedEEA" -> "2",
    "numPropRentedNONEEA" -> "1"
  )

  val testPropertiesRentedToJson = Json.obj(
    "uk" -> 3,
    "eea" -> 2,
    "nonEea" -> 1,
    "total" -> 4
  )


  val testPropertyDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accPeriodSDate" -> "2017-06-01",
    "accPeriodEDate" -> "2018-05-31",
    "email" -> "stephen@manncorpone.co.uk",
    "numPropRentedUK" -> 3,
    "numPropRentedEEA" -> 2,
    "numPropRentedNONEEA" -> 1,
    "numPropRented" -> 4,
    "cessationDate" -> "2017-06-01",
    "paperLessFlag" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "incomeSourceType" -> "02",
    "contextualTaxYear" -> "2015",
    "tradingSDate" -> "2015-01-01",
    "latencyDetails" -> Json.obj(
      "latencyEndDate" -> "2022-01-01",
      "taxYear1" -> "2022",
      "latencyIndicator1" -> "A",
      "taxYear2" -> "2023",
      "latencyIndicator2" -> "Q"),
    "cashOrAccrualsFlag" -> true,
    "quarterTypeElection" -> Json.obj(
      "quarterReportingType" -> "STANDARD",
      "taxYearofElection" -> "2021"
    )
  )

  val testPropertyDetailsJsonString = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accPeriodSDate" -> "2017-06-01",
    "accPeriodEDate" -> "2018-05-31",
    "email" -> "stephen@manncorpone.co.uk",
    "numPropRentedUK" -> "3",
    "numPropRentedEEA" -> "2",
    "numPropRentedNONEEA" -> "1",
    "numPropRented" -> "4",
    "cessationDate" -> "2017-06-01",
    "paperLessFlag" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "incomeSourceType" -> "02",
    "contextualTaxYear" -> "2015",
    "tradingSDate" -> "2015-01-01",
    "latencyDetails" -> Json.obj(
      "latencyEndDate" -> "2022-01-01",
      "taxYear1" -> "2022",
      "latencyIndicator1" -> "A",
      "taxYear2" -> "2023",
      "latencyIndicator2" -> "Q"),
    "cashOrAccrualsFlag" -> true,
    "quarterTypeElection" -> Json.obj(
      "quarterReportingType" -> "STANDARD",
      "taxYearofElection" -> "2021"
    )
  )

  val testPropertyDetailsToJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriod" -> testAccountingPeriodToJson,
    "contactDetails" -> testSomeContactDetailsJson,
    "propertiesRented" -> testPropertiesRentedToJson,
    "cessation" -> testCessationToJson,
    "paperless" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "incomeSourceType" -> "property-unspecified",
    "contextualTaxYear" -> "2015",
    "tradingStartDate" -> "2015-01-01",
    "latencyDetails" -> Json.obj(
      "latencyEndDate" -> "2022-01-01",
      "taxYear1" -> "2022",
      "latencyIndicator1" -> "A",
      "taxYear2" -> "2023",
      "latencyIndicator2" -> "Q"),
    "cashOrAccruals" -> true,
    "quarterTypeElection" -> Json.obj(
      "quarterReportingType" -> "STANDARD",
      "taxYearofElection" -> "2021"
    )
  )

  val testMinimumPropertyDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "cashOrAccruals" -> true
  )


}

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

import constants.AccountingPeriodTestConstants._
import constants.AddressDetailsTestConstants._
import constants.CessationTestConstants._
import constants.ContactDetailsTestConstants._
import models.incomeSourceDetails.{BusinessDetailsModel, LatencyDetails, QuarterTypeElection}
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

object BusinessDetailsTestConstants {

  val testBusinessDetailsModel: BusinessDetailsModel =
    BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      incomeSource = Some("Fruit Ltd"),
      accountingPeriod = testAccountingPeriodModel,
      tradingName = Some("Test Business"),
      address = Some(testAddressModel),
      contactDetails = Some(testContactDetailsModel),
      contextualTaxYear = Some("2015"),
      tradingStartDate = Some(LocalDate.parse("2017-01-01")),
      cashOrAccruals = false,
      seasonal = Some(true),
      cessation = Some(testCessationModel),
      paperless = Some(true),
      firstAccountingPeriodEndDate = Some(LocalDate.of(2016, 1, 1)),
      latencyDetails = Some(LatencyDetails(
        latencyEndDate = LocalDate.of(2022, 1, 1),
        taxYear1 = "2022",
        latencyIndicator1 = "A",
        taxYear2 = "2023",
        latencyIndicator2 = "Q")),
      quarterTypeElection = Some(QuarterTypeElection("STANDARD", "2021"))
    )


  val testMinimumBusinessDetailsModel: BusinessDetailsModel = BusinessDetailsModel(
    incomeSourceId = "111111111111111",
    incomeSource = Some("Fruit Ltd"),
    accountingPeriod = testAccountingPeriodModel,
    tradingName = None,
    address = None,
    contactDetails = None,
    contextualTaxYear = None,
    tradingStartDate = None,
    cashOrAccruals = true,
    seasonal = None,
    cessation = None,
    paperless = None,
    firstAccountingPeriodEndDate = None,
    latencyDetails = None,
    quarterTypeElection = None
  )

  val testBusinessDetailsJson: JsObject = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "incomeSource" -> "Fruit Ltd",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "tradingName" -> "Test Business",
    "businessAddressDetails" -> testAddressJson,
    "businessContactDetails" -> testContactDetailsJson,
    "contextualTaxYear" -> "2015",
    "tradingStartDate" -> "2017-01-01",
    "cashOrAccruals" -> false,
    "seasonal" -> true,
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "latencyDetails" -> Json.obj(
      "latencyEndDate" -> "2022-01-01",
      "taxYear1" -> "2022",
      "latencyIndicator1" -> "A",
      "taxYear2" -> "2023",
      "latencyIndicator2" -> "Q"
    ),
    "quarterTypeElection" -> Json.obj(
      "quarterReportingType" -> "STANDARD",
      "taxYearofElection" -> "2021"
    )
  )

  val testBusinessDetailsToJson: JsObject = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "incomeSource" -> "Fruit Ltd",
    "accountingPeriod" -> testAccountingPeriodToJson,
    "tradingName" -> "Test Business",
    "address" -> testAddressToJson,
    "contactDetails" -> testContactDetailsJson,
    "contextualTaxYear" -> "2015",
    "tradingStartDate" -> "2017-01-01",
    "cashOrAccruals" -> false,
    "seasonal" -> true,
    "cessation" -> testCessationToJson,
    "paperless" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "latencyDetails" -> Json.obj(
      "latencyEndDate" -> "2022-01-01",
      "taxYear1" -> "2022",
      "latencyIndicator1" -> "A",
      "taxYear2" -> "2023",
      "latencyIndicator2" -> "Q"),
    "quarterTypeElection" -> Json.obj(
      "quarterReportingType" -> "STANDARD",
      "taxYearofElection" -> "2021"
    )
  )

  val testMinimumBusinessDetailsJson: JsObject = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "incomeSource" -> "Fruit Ltd",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "cashOrAccruals" -> true
  )

  val testMinimumBusinessDetailsToJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "incomeSource" -> "Fruit Ltd",
    "accountingPeriod" -> testAccountingPeriodToJson,
    "cashOrAccruals" -> true
  )

}

/*
 * Copyright 2020 HM Revenue & Customs
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

package assets

import java.time.LocalDate

import assets.AccountingPeriodTestConstants._
import assets.AddressDetailsTestConstants._
import assets.CessationTestConstants._
import assets.ContactDetailsTestConstants._
import models.incomeSourceDetails.BusinessDetailsModel
import play.api.libs.json.Json

object BusinessDetailsTestConstants {

  val testBusinessDetailsModel =
    BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = testAccountingPeriodModel,
      tradingName = Some("Test Business"),
      address = Some(testAddressModel),
      contactDetails = Some(testContactDetailsModel),
      tradingStartDate = Some(LocalDate.parse("2017-01-01")),
      cashOrAccruals = Some("cash"),
      seasonal = Some(true),
      cessation = Some(testCessationModel),
      paperless = Some(true)
    )


  val testMinimumBusinessDetailsModel = BusinessDetailsModel(
    incomeSourceId = "111111111111111",
    accountingPeriod = testAccountingPeriodModel,
    tradingName = None,
    address = None,
    contactDetails = None,
    tradingStartDate = None,
    cashOrAccruals = None,
    seasonal = None,
    cessation = None,
    paperless = None
  )

  val testBusinessDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate"->"2017-06-01",
    "accountingPeriodEndDate"->"2018-05-31",
    "tradingName"->"Test Business",
    "businessAddressDetails" -> testAddressJson,
    "businessContactDetails" -> testContactDetailsJson,
    "tradingStartDate"->"2017-01-01",
    "cashOrAccruals"->"cash",
    "seasonal" -> true,
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true
  )

  val testBusinessDetailsToJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriod"-> testAccountingPeriodToJson,
    "tradingName"->"Test Business",
    "address" -> testAddressToJson,
    "contactDetails" -> testContactDetailsJson,
    "tradingStartDate"->"2017-01-01",
    "cashOrAccruals"->"cash",
    "seasonal" -> true,
    "cessation" -> testCessationToJson,
    "paperless" -> true
  )

  val testMinimumBusinessDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate"->"2017-06-01",
    "accountingPeriodEndDate"->"2018-05-31"
  )

  val testMinimumBusinessDetailsToJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriod"-> testAccountingPeriodToJson
  )

}

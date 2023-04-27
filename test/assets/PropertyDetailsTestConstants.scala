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

package assets

import java.time.LocalDate

import assets.AccountingPeriodTestConstants._
import assets.CessationTestConstants._
import assets.ContactDetailsTestConstants._
import models.incomeSourceDetails.{PropertiesRentedModel, PropertyDetailsModel}
import play.api.libs.json.Json

object PropertyDetailsTestConstants {

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
    incomeSourceType = Some("property-unspecified"),
    tradingStartDate = Some(LocalDate.parse("2015-01-01"))
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
    tradingStartDate = None
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
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "emailAddress" -> "stephen@manncorpone.co.uk",
    "numPropRentedUK" -> 3,
    "numPropRentedEEA" -> 2,
    "numPropRentedNONEEA" -> 1,
    "numPropRented" -> 4,
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "incomeSourceType" -> "property-unspecified",
    "tradingStartDate" -> "2015-01-01"
  )

  val testPropertyDetailsJsonString = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31",
    "emailAddress" -> "stephen@manncorpone.co.uk",
    "numPropRentedUK" -> "3",
    "numPropRentedEEA" -> "2",
    "numPropRentedNONEEA" -> "1",
    "numPropRented" -> "4",
    "cessationDate" -> "2017-06-01",
    "cessationReason" -> "Dummy reason",
    "paperLess" -> true,
    "firstAccountingPeriodEndDate" -> "2016-01-01",
    "incomeSourceType" -> "property-unspecified",
    "tradingStartDate" -> "2015-01-01"
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
    "tradingStartDate" -> "2015-01-01"
  )

  val testMinimumPropertyDetailsJson = Json.obj(
    "incomeSourceId" -> "111111111111111",
    "accountingPeriodStartDate" -> "2017-06-01",
    "accountingPeriodEndDate" -> "2018-05-31"
  )


}

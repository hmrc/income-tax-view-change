/*
 * Copyright 2017 HM Revenue & Customs
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

import assets.BaseIntegrationTestConstants.testMtdRef
import models.core.{AccountingPeriodModel, AddressModel, ContactDetailsModel}
import models.incomeSourceDetails.BusinessDetailsModel
import play.api.libs.json.{JsValue, Json}

object BusinessDetailsIntegrationTestConstants {

  val testBusinessModel = List(
    BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = AccountingPeriodModel(
        start = LocalDate.parse("2017-06-01"),
        end = LocalDate.parse("2018-05-31")),
      tradingName = Some("Test Business"),
      address = Some(AddressModel(
        addressLine1 = "Test Lane",
        addressLine2 = Some("Test Unit"),
        addressLine3 = Some("Test Town"),
        addressLine4 = Some("Test City"),
        postCode = Some("TE5 7TE"),
        countryCode = "GB")),
      contactDetails = Some(ContactDetailsModel(
        phoneNumber = Some("01332752856"),
        mobileNumber = Some("07782565326"),
        faxNumber = Some("01332754256"),
        emailAddress = Some("stephen@manncorpone.co.uk"))),
      tradingStartDate = Some(LocalDate.parse("2017-01-01")),
      cashOrAccruals = Some("cash"),
      seasonal = Some(true),
      cessation = None,
      paperless = Some(true),
      incomeSourceStartDate = Some(LocalDate.of(2016, 1, 1))
    )
  )

  def successResponse(nino: String): JsValue = {

    Json.obj(
      "safeId" -> "XAIT12345678908",
      "nino" -> nino,
      "mtdbsa" -> testMtdRef,
      "propertyIncome" -> false,
      "businessData" -> Json.arr(
        Json.obj(
          "incomeSourceId" -> "111111111111111",
          "accountingPeriodStartDate" -> "2017-06-01",
          "accountingPeriodEndDate" -> "2018-05-31",
          "tradingName" -> "Test Business",
          "businessAddressDetails" -> Json.obj(
            "addressLine1" -> "Test Lane",
            "addressLine2" -> "Test Unit",
            "addressLine3" -> "Test Town",
            "addressLine4" -> "Test City",
            "postalCode" -> "TE5 7TE",
            "countryCode" -> "GB"
          ),
          "businessContactDetails" -> Json.obj(
            "phoneNumber" -> "01332752856",
            "mobileNumber" -> "07782565326",
            "faxNumber" -> "01332754256",
            "emailAddress" -> "stephen@manncorpone.co.uk"
          ),
          "tradingStartDate" -> "2017-01-01",
          "cashOrAccruals" -> "cash",
          "seasonal" -> true,
          "paperLess" -> true,
          "incomeSourceStartDate" -> "2016-01-01"
        )
      )
    )

  }

  def failureResponse(code: String, reason: String): JsValue =
    Json.obj(
      "code" -> code,
      "reason" -> reason
    )

}

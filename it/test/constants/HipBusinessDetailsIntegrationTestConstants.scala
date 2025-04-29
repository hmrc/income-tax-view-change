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

package constants

import constants.BaseIntegrationTestConstants.testMtdRef
import models.hip.core.{AccountingPeriodModel, AddressModel, ContactDetailsModel}
import models.hip.incomeSourceDetails.{BusinessDetailsModel, LatencyDetails, PropertyDetailsModel, QuarterTypeElection}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object HipBusinessDetailsIntegrationTestConstants {

  val testBusinessModel = List(
    BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      incomeSource = Some("Fruit Ltd"),
      accountingPeriod = AccountingPeriodModel(
        start = LocalDate.parse("2017-06-01"),
        end = LocalDate.parse("2018-05-31")),
      tradingName = Some("Test Business"),
      address = Some(AddressModel(
        addressLine1 = Some("Test Lane"),
        addressLine2 = Some("Test Unit"),
        addressLine3 = Some("Test Town"),
        addressLine4 = Some("Test City"),
        postCode = Some("TE5 7TE"),
        countryCode = Some("GB"))),
      contactDetails = Some(ContactDetailsModel(
        phoneNumber = Some("01332752856"),
        mobileNumber = Some("07782565326"),
        faxNumber = Some("01332754256"),
        emailAddress = Some("stephen@manncorpone.co.uk"))),
      contextualTaxYear = Some("2015"),
      tradingStartDate = Some(LocalDate.parse("2017-01-01")),
      cashOrAccruals = false,
      seasonal = Some(true),
      cessation = None,
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
  )

  val testPropertyDetailsModel =
    PropertyDetailsModel(
      incomeSourceId = "2222222222",
      accountingPeriod = AccountingPeriodModel(
        start = LocalDate.parse("2017-06-01"),
        end = LocalDate.parse("2018-05-31")),
      contactDetails = None,
      propertiesRented = None,
      cessation = None,
      paperless = Some(true),
      firstAccountingPeriodEndDate = Some(LocalDate.parse("2017-06-01")),
      incomeSourceType = None,
      contextualTaxYear = None,
      tradingStartDate = None,
      latencyDetails = None,
      cashOrAccruals = true,
      quarterTypeElection = None
    )

  def successResponseHip(nino: String): JsValue = {
    Json.obj(
      "success" -> Json.obj(
      "taxPayerDisplayResponse" -> successResponse(nino)
    ))
  }

  def successResponse(nino: String): JsValue = {

    Json.obj(
      "safeId" -> "XAIT12345678908",
      "nino" -> nino,
      "mtdbsa" -> testMtdRef,
      "mtdId" -> testMtdRef,
      "propertyIncome" -> true,
      "businessData" -> Json.arr(
        Json.obj(
          "incomeSourceId" -> "111111111111111",
          "accPeriodSDate" -> "2017-06-01",
          "accPeriodEDate" -> "2018-05-31",
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
            "telephone" -> "01332752856",
            "mobileNo" -> "07782565326",
            "faxNo" -> "01332754256",
            "email" -> "stephen@manncorpone.co.uk"
          ),
          "contextualTaxYear" -> "2015",
          "tradingSDate" -> "2017-01-01",
          "cashOrAccrualsFlag" -> false,
          "seasonalFlag" -> true,
          "paperLessFlag" -> true,
          "firstAccountingPeriodEndDate" -> "2016-01-01",
          "latencyDetails" -> Json.obj(
            "latencyEndDate" -> "2022-01-01",
            "taxYear1" -> "2022",
            "latencyIndicator1" -> "A",
            "taxYear2" -> "2023",
            "latencyIndicator2" -> "Q")
        )
      ),
      "propertyData" -> Json.arr(
        Json.obj(
          "incomeSourceId" -> "2222222222",
          "accPeriodSDate" -> "2017-06-01",
          "accPeriodEDate" -> "2018-05-31",
          "paperLessFlag" -> true,
          "firstAccountingPeriodEndDate" -> "2017-06-01",
          "cashOrAccrualsFlag" -> false
        )
      )
    )

  }

  def failureResponse(code: String, reason: String): JsValue = {
    Json.obj("error" ->
    Json.obj(
      "code" -> code,
      "message" -> reason
    ))
  }

  def jsonSuccessOutput(): JsValue = {
    Json.parse(
      """{
        |    "nino": "BB123456A",
        |    "mtdbsa": "123456789012345",
        |    "businesses": [
        |        {
        |            "incomeSourceId": "111111111111111",
        |            "accountingPeriod": {
        |                "start": "2017-06-01",
        |                "end": "2018-05-31"
        |            },
        |            "tradingName": "Test Business",
        |            "address": {
        |                "addressLine1": "Test Lane",
        |                "addressLine2": "Test Unit",
        |                "addressLine3": "Test Town",
        |                "addressLine4": "Test City",
        |                "postCode": "TE5 7TE",
        |                "countryCode": "GB"
        |            },
        |            "contactDetails": {
        |                "phoneNumber": "01332752856",
        |                "mobileNumber": "07782565326",
        |                "faxNumber": "01332754256",
        |                "emailAddress": "stephen@manncorpone.co.uk"
        |            },
        |            "contextualTaxYear": "2015",
        |            "tradingStartDate": "2017-01-01",
        |            "cashOrAccruals": false,
        |            "seasonal": true,
        |            "paperless": true,
        |            "firstAccountingPeriodEndDate": "2016-01-01",
        |            "latencyDetails": {
        |                "latencyEndDate": "2022-01-01",
        |                "taxYear1": "2022",
        |                "latencyIndicator1": "A",
        |                "taxYear2": "2023",
        |                "latencyIndicator2": "Q"
        |            }
        |        }
        |    ],
        |    "properties": [
        |        {
        |            "incomeSourceId": "2222222222",
        |            "accountingPeriod": {
        |                "start": "2017-06-01",
        |                "end": "2018-05-31"
        |            },
        |            "paperless": true,
        |            "firstAccountingPeriodEndDate": "2017-06-01",
        |            "cashOrAccruals": false
        |        }
        |    ]
        |}""".stripMargin)
  }

}

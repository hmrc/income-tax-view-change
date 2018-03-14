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

package helpers

import java.time.LocalDate

import models._
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status

object IntegrationTestConstants {

  val testMtditidEnrolmentKey = "HMRC-MTD-IT"
  val testMtditidEnrolmentIdentifier = "MTDITID"
  val testMtditid = "XAITSA123456"

  val testNinoEnrolmentKey = "HMRC-NI"
  val testNinoEnrolmentIdentifier = "NINO"
  val testNino = "BB123456A"
  val testMtdRef = "123456789012345"
  val testYear = "2018"
  val testCalcType = "it"

  val lastTaxCalculation = LastTaxCalculation("01234567", "2017-07-06T12:34:56.789Z", 2345.67)

  val lastTaxCalculationError = LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")

  val ninoLookup = NinoModel(testNino)

  val ninoLookupError = IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Error Message")

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
      paperless = Some(true)
    )
  )

  val incomeSourceDetailsSuccess: IncomeSourceDetailsModel =
  IncomeSourceDetailsModel(
    nino = testNino,
    businesses = testBusinessModel,
    property = None
  )

  val incomeSourceDetailsError: IncomeSourceDetailsError = IncomeSourceDetailsError(500,"""{"code":"500","reason":"ISE"}""")
  val ninoError: NinoErrorModel = NinoErrorModel(500,"""{"code":"500","reason":"ISE"}""")

  object GetFinancialData {
    def successResponse(calcId: String, calcTimestamp: String, calcAmount: BigDecimal): JsValue =
      Json.parse(s"""
         |{
         |   "calcID": "$calcId",
         |   "calcTimestamp": "$calcTimestamp",
         |   "calcAmount": $calcAmount
         |}
      """.stripMargin)

    def failureResponse(code: String, reason: String): JsValue =
      Json.parse(s"""
         |{
         |   "code": "$code",
         |   "reason":"$reason"
         |}
      """.stripMargin)
  }

  object GetDesBusinessDetails {
    def successResponse(nino: String): JsValue = {

    val x = Json.parse(
      s"""{
           |"safeId":"XAIT12345678908",
           |"nino":"$nino",
           |"mtdbsa":"$testMtdRef",
           |"propertyIncome":false,
           |"businessData": [
           | {
           |   "incomeSourceId":"111111111111111",
           |   "accountingPeriodStartDate":"2017-06-01",
           |   "accountingPeriodEndDate":"2018-05-31",
           |   "tradingName":"Test Business",
           |   "businessAddressDetails":{
           |     "addressLine1":"Test Lane",
           |     "addressLine2":"Test Unit",
           |     "addressLine3":"Test Town",
           |     "addressLine4":"Test City",
           |     "postalCode":"TE5 7TE",
           |     "countryCode":"GB"
           |   },
           |   "businessContactDetails":{
           |     "phoneNumber":"01332752856",
           |     "mobileNumber":"07782565326",
           |     "faxNumber":"01332754256",
           |     "emailAddress":"stephen@manncorpone.co.uk"
           |   },
           |   "tradingStartDate":"2017-01-01",
           |   "cashOrAccruals":"cash",
           |   "seasonal":true,
           |   "paperLess":true
           |  }
           | ]
           |}
      """.stripMargin
      )
      println(s"\n\n\n$x\n\n\n")
      x
    }

    def failureResponse(code: String, reason: String): JsValue =
      Json.parse(s"""
                    |{
                    |   "code": "$code",
                    |   "reason":"$reason"
                    |}
      """.stripMargin)
  }
}

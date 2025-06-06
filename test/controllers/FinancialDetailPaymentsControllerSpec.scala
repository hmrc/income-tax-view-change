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

package controllers

import constants.FinancialDataTestConstants.{chargesResponseNoCodingDetails2, creditChargesResponse}
import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockFinancialDetailsConnector, MockMicroserviceAuthConnector}
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.time.LocalDate

class FinancialDetailPaymentsControllerSpec extends ControllerBaseSpec with MockFinancialDetailsConnector with MockMicroserviceAuthConnector {

  val paymentJson: JsArray = Json.arr(
    Json.obj(
      "reference" -> "paymentReference",
      "amount" -> 300,
      "outstandingAmount" -> 0,
      "documentDescription" -> "documentDescription",
      "method" -> "paymentMethod",
      "lot" -> "paymentLot",
      "lotItem" -> "paymentLotItem",
      "dueDate" -> LocalDate.parse("2022-06-23"),
      "documentDate" -> LocalDate.parse("2022-06-23"),
      "transactionId" -> "id",
      "mainType" -> "ITSA Cutover Credits"
    )
  )

  val paymentJsonCredit: JsArray = Json.arr(
    Json.obj(
      "reference" -> "paymentReference",
      "amount" -> -1000,
      "outstandingAmount" -> 0,
      "documentDescription" -> "documentDescription",
      "dueDate" -> LocalDate.parse("2018-03-29"),
      "documentDate" -> LocalDate.parse("2022-06-23"),
      "transactionId" -> "id",
      "mainType" -> "ITSA Cutover Credits",
      "mainTransaction" -> "4920"
    )
  )

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object TestFinancialDetailPaymentsController extends FinancialDetailPaymentsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents, microserviceAppConfig),
    cc = controllerComponents,
    financialDetailChargesService = mockFinancialDetailsService
  )

  val nino: String = "AA000000A"
  val from: String = "from"
  val to: String = "to"


  "getPaymentDetails" should {
    s"return $OK with the retrieved payment details from the charge details" when {
      "the connector returns the charge details" in {
        mockAuth()
        mockGetPayments(nino, from, to)(Right(creditChargesResponse))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe paymentJsonCredit
      }
      "the connector returns the charge details with no CodingDetails" in {
        mockAuth()
        mockListCharges(nino, from, to)(Right(chargesResponseNoCodingDetails2))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe paymentJsonCredit
      }
    }

    s"return $NOT_FOUND" when {
      "the connector returns an NOT_FOUND error" in {
        mockAuth()
        val errorJson = """{"code":"NO_DATA_FOUND","reason":"The remote endpoint has indicated that no data can be found."}"""
        mockGetPayments(nino, from, to)(Left(UnexpectedChargeResponse(NOT_FOUND, errorJson)))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe NOT_FOUND
        contentAsString(result) shouldBe errorJson
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockGetPayments(nino, from, to)(Left(UnexpectedChargeResponse(INTERNAL_SERVER_ERROR, "")))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve charge details to get payments"
      }
    }
  }
}

/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.FinancialDataTestConstants.{chargesResponse, chargesResponseNoCodingDetails}
import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockFinancialDetailsConnector, MockMicroserviceAuthConnector}
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FinancialDetailPaymentsControllerSpec extends ControllerBaseSpec with MockFinancialDetailsConnector with MockMicroserviceAuthConnector {

  val paymentJson: JsArray = Json.arr(
    Json.obj(
      "reference" -> "paymentReference",
      "amount" -> 300,
      "method" -> "paymentMethod",
      "lot" -> "paymentLot",
      "lotItem" -> "paymentLotItem",
      "date" -> "dueDate",
      "transactionId" -> "id"
    )
  )

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object TestFinancialDetailPaymentsController extends FinancialDetailPaymentsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents),
    cc = controllerComponents,
    financialDetailsConnector = mockFinancialDetailsConnector
  )

  val nino: String = "AA000000A"
  val from: String = "from"
  val to: String = "to"


  "getPaymentDetails" should {
    s"return $OK with the retrieved payment details from the charge details" when {
      "the connector returns the charge details" in {
        mockAuth()
        mockListCharges(nino, from, to)(Right(chargesResponse))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe paymentJson
      }
      "the connector returns the charge details with no CodingDetails" in {
        mockAuth()
        mockListCharges(nino, from, to)(Right(chargesResponseNoCodingDetails))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe paymentJson
      }
    }

    s"return $NOT_FOUND" when {
      "the connector returns an NOT_FOUND error" in {
        mockAuth()
        val errorJson = """{"code":"NO_DATA_FOUND","reason":"The remote endpoint has indicated that no data can be found."}"""
        mockListCharges(nino, from, to)(Left(UnexpectedChargeResponse(NOT_FOUND, errorJson)))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe NOT_FOUND
        contentAsString(result) shouldBe errorJson
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockListCharges(nino, from, to)(Left(UnexpectedChargeResponse(INTERNAL_SERVER_ERROR, "")))

        val result = TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest())

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve charge details to get payments"
      }
    }
  }
}

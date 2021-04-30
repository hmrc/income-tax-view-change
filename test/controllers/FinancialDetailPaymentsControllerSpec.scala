/*
 * Copyright 2021 HM Revenue & Customs
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

import assets.FinancialDataTestConstants.{documentDetail, financialDetail}
import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockFinancialDetailsConnector, MockMicroserviceAuthConnector}
import models.financialDetails.responses.ChargesResponse
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents

class FinancialDetailPaymentsControllerSpec extends ControllerBaseSpec with MockFinancialDetailsConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object TestFinancialDetailPaymentsController extends FinancialDetailPaymentsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents),
    cc = controllerComponents,
    financialDetailsConnector = mockFinancialDetailsConnector
  )

  val nino: String = "AA000000A"
  val from: String = "from"
  val to: String = "to"

  val chargesResponse: ChargesResponse = ChargesResponse(
    documentDetails = List(documentDetail),
    financialDetails = List(financialDetail)
  )

  "getPaymentDetails" should {
    s"return $OK with the retrieved payment details from the charge details" when {
      "the connector returns the charge details" in {
        mockAuth()
        mockListCharges(nino, from, to)(Right(chargesResponse))

        val result = await(TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest()))

        status(result) shouldBe OK
        jsonBodyOf(result) shouldBe Json.toJson(chargesResponse.financialDetails.flatMap(_.payments))
      }
    }

    s"return $NOT_FOUND" when {
      "the connector returns an NOT_FOUND error" in {
        mockAuth()
        val errorJson = """{"code":"NO_DATA_FOUND","reason":"The remote endpoint has indicated that no data can be found."}"""
        mockListCharges(nino, from, to)(Left(UnexpectedChargeResponse(NOT_FOUND, errorJson)))

        val result = await(TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest()))

        status(result) shouldBe NOT_FOUND
        bodyOf(result) shouldBe errorJson
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockListCharges(nino, from, to)(Left(UnexpectedChargeResponse(INTERNAL_SERVER_ERROR, "")))

        val result = await(TestFinancialDetailPaymentsController.getPaymentDetails(nino, from, to)(FakeRequest()))

        status(result) shouldBe INTERNAL_SERVER_ERROR
        bodyOf(result) shouldBe "Failed to retrieve charge details"
      }
    }
  }
}

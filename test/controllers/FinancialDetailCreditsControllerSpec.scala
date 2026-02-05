/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockFinancialDetailsConnector, MockMicroserviceAuthConnector}
import models.credits.CreditsModel
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK, contentAsJson, contentAsString, defaultAwaitTimeout, status, stubControllerComponents}
import utils.AChargesResponse

import java.time.LocalDate

class FinancialDetailCreditsControllerSpec extends ControllerBaseSpec with MockFinancialDetailsConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object FinancialDetailCreditsController extends FinancialDetailCreditsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents, microserviceAppConfig),
    cc = controllerComponents, mockFinancialDetailsService
  )

  val nino: String = "AA000000A"
  val from: String = "from"
  val to: String = "to"
  val documentId: String = "123456789"

  "getChargeDetails" should {
    s"return $OK with the retrieved charge details" when {

      "the connector returns the charge details" in {

        val chargesResponse = AChargesResponse()
          .withAvailableCredit(200.0)
          .withAllocatedFutureCredit(100.0)
          .withFirstRefundRequest(200.0)
          .withSecondRefundRequest(100.0)
          .withCutoverCredit("CUTOVER01", LocalDate.of(2024, 6, 20), -100.0)
          .withBalancingChargeCredit("BALANCING01", LocalDate.of(2024, 6, 19), -200)
          .withMfaCredit("MFA01", LocalDate.of(2024, 6, 18), -300)
          .withPayment("PAYMENT01", LocalDate.of(2024, 6, 17), LocalDate.of(2024, 6, 17), -400)
          .withRepaymentInterest("INTEREST01", LocalDate.of(2024, 6, 16), -500)
          .get()

        mockAuth()
        mockCredits(nino, from, to)(Right(chargesResponse))

        val result = FinancialDetailCreditsController.getCredits(nino, from, to)(FakeRequest())

        val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(creditsModel)
      }
    }

    s"return $NOT_FOUND" when {
      "the connector returns an NOT_FOUND error" in {
        mockAuth()
        val errorJson = """{"code":"NO_DATA_FOUND","reason":"The remote endpoint has indicated that no data can be found."}"""
        mockCredits(nino, from, to)(Left(UnexpectedChargeResponse(NOT_FOUND, errorJson)))

        val result = FinancialDetailCreditsController.getCredits(nino, from, to)(FakeRequest())

        status(result) shouldBe NOT_FOUND
        contentAsString(result) shouldBe errorJson
      }
    }
    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockCredits(nino, from, to)(Left(UnexpectedChargeResponse(INTERNAL_SERVER_ERROR, "")))

        val result = FinancialDetailCreditsController.getCredits(nino, from, to)(FakeRequest())

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve charge details"
      }
    }
  }
}

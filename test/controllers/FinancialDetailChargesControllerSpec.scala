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

package controllers

import assets.FinancialDataTestConstants.{charges1, charges2, validChargesJsonAfterWrites}
import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockFinancialDetailsConnector, MockMicroserviceAuthConnector}
import models.financialDetails.responses.ChargesResponse
import play.api.http.Status._
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents

class FinancialDetailChargesControllerSpec extends ControllerBaseSpec with MockFinancialDetailsConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object FinancialDetailChargesController extends FinancialDetailChargesController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents),
    cc = controllerComponents,
    financialDetailsConnector = mockFinancialDetailsConnector
  )

  val nino: String = "AA000000A"
  val from: String = "from"
  val to: String = "to"

  "getChargeDetails" should {
    s"return $OK with the retrieved charge details" when {
      "the connector returns the charge details" in {
        mockAuth()
        mockListCharges(nino, from, to)(Right(ChargesResponse(List(charges1, charges2))))

        val result = await(FinancialDetailChargesController.getChargeDetails(nino, from, to)(FakeRequest()))

        status(result) shouldBe OK
        jsonBodyOf(result) shouldBe validChargesJsonAfterWrites
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockListCharges(nino, from, to)(Left(UnexpectedChargeResponse))

        val result = await(FinancialDetailChargesController.getChargeDetails(nino, from, to)(FakeRequest()))

        status(result) shouldBe INTERNAL_SERVER_ERROR
        bodyOf(result) shouldBe "Failed to retrieve charge details"
      }
    }
  }
}

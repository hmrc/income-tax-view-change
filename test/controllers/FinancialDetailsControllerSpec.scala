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

import assets.FinancialDataTestConstants.{chargesResponse, chargesResponseNoCodingDetails}
import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockFinancialDetailsConnector, MockMicroserviceAuthConnector}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FinancialDetailsControllerSpec extends ControllerBaseSpec with MockFinancialDetailsConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object TestFinancialDetailsController extends FinancialDetailsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents, microserviceAppConfig),
    cc = controllerComponents,
    financialDetailsConnector = mockFinancialDetailsConnector
  )

  val nino: String = "AA000000A"


  "getOnlyOpenItems" should {
    s"return $OK with the retrieved charge details" when {
      "the connector returns the charge details" in {
        mockAuth()
        mockOnlyOpenItems(nino)(Right(chargesResponse))

        val result = TestFinancialDetailsController.getOnlyOpenItems(nino)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(chargesResponse)
      }
      "the connector returns the charge details with no CodingDetails" in {
        mockAuth()
        mockOnlyOpenItems(nino)(Right(chargesResponseNoCodingDetails))

        val result = TestFinancialDetailsController.getOnlyOpenItems(nino)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(chargesResponseNoCodingDetails)
      }
    }

    s"return $NOT_FOUND" when {
      "the connector returns an NOT_FOUND error" in {
        mockAuth()
        val errorJson = """{"code":"NO_DATA_FOUND","reason":"The remote endpoint has indicated that no data can be found."}"""
        mockOnlyOpenItems(nino)(Left(UnexpectedChargeResponse(NOT_FOUND, errorJson)))

        val result = TestFinancialDetailsController.getOnlyOpenItems(nino)(FakeRequest())

        status(result) shouldBe NOT_FOUND
        contentAsString(result) shouldBe errorJson
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockOnlyOpenItems(nino)(Left(UnexpectedChargeResponse(INTERNAL_SERVER_ERROR, "")))

        val result = TestFinancialDetailsController.getOnlyOpenItems(nino)(FakeRequest())

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve open items details"
      }
    }
  }
}

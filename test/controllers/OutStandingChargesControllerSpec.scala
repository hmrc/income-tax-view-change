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

import assets.OutStandingChargesConstant._
import connectors.httpParsers.OutStandingChargesHttpParser.UnexpectedOutStandingChargeResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockMicroserviceAuthConnector, MockOutStandingChargesConnector}
import models.outStandingCharges.OutstandingChargesSuccessResponse
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}

class OutStandingChargesControllerSpec extends ControllerBaseSpec with MockOutStandingChargesConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object OutStandingChargesController extends OutStandingChargesController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents),
    cc = controllerComponents,
    connector = mockOutStandingChargesConnector
  )

  val idType: String = "utr"
  val idNumber: Int = 1234567890
  val taxYearEndDate: String = "2020-04-05"

  "getOutStandingCharges" should {
    s"return $OK with the retrieved charge details" when {
      "the connector returns the charge details" in {
        mockAuth()
        mockListOutStandingCharges(idType, idNumber, taxYearEndDate)(Right(OutstandingChargesSuccessResponse(List(outStandingChargeModelOne, outStandingChargeModelTwo))))

        val result = OutStandingChargesController.listOutStandingCharges(idType, idNumber, taxYearEndDate)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe validMultipleOutStandingChargeResponseJson
      }
    }

    s"return $NOT_FOUND" when {
      "the connector returns an NOT_FOUND error" in {
        mockAuth()
        val errorJson = """{"code":"NO_DATA_FOUND","reason":"The remote endpoint has indicated that no data can be found."}"""
        mockListOutStandingCharges(idType, idNumber, taxYearEndDate)(Left(UnexpectedOutStandingChargeResponse(NOT_FOUND, errorJson)))

        val result = OutStandingChargesController.listOutStandingCharges(idType, idNumber, taxYearEndDate)(FakeRequest())

        status(result) shouldBe NOT_FOUND
        contentAsString(result) shouldBe errorJson
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockListOutStandingCharges(idType, idNumber, taxYearEndDate)(Left(UnexpectedOutStandingChargeResponse(INTERNAL_SERVER_ERROR, "")))

        val result = OutStandingChargesController.listOutStandingCharges(idType, idNumber, taxYearEndDate)(FakeRequest())

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve charges outstanding details"
      }
    }
  }

}

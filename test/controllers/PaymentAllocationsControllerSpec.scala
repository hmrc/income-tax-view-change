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

import connectors.httpParsers.PaymentAllocationsHttpParser.{NotFoundResponse, UnexpectedResponse}
import controllers.predicates.AuthenticationPredicate
import mocks.{MockMicroserviceAuthConnector, MockPaymentAllocationsConnector}
import models.paymentAllocations.{paymentAllocationsFull, paymentAllocationsWriteJsonFull}
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}

class PaymentAllocationsControllerSpec extends ControllerBaseSpec with MockPaymentAllocationsConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object PaymentAllocationsController extends PaymentAllocationsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents, microserviceAppConfig),
    cc = controllerComponents,
    paymentAllocationsConnector = mockPaymentAllocationsConnector
  )

  val nino: String = "AA000000A"
  val paymentLot: String = "paymentLot"
  val paymentLotItem: String = "paymentLotItem"

  "getPaymentAllocations" should {
    s"return $OK with the retrieved payment allocations" when {
      "the connector returns the payment allocations" in {
        mockAuth()
        mockGetPaymentAllocations(nino, paymentLot, paymentLotItem)(Right(paymentAllocationsFull))

        val result = PaymentAllocationsController.getPaymentAllocations(nino, paymentLot, paymentLotItem)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe paymentAllocationsWriteJsonFull
      }
    }
    s"return a $NOT_FOUND response" when {
      "the connector returns a NotFoundResponse" in {
        mockAuth()
        mockGetPaymentAllocations(nino, paymentLot, paymentLotItem)(Left(NotFoundResponse))

        val result = PaymentAllocationsController.getPaymentAllocations(nino, paymentLot, paymentLotItem)(FakeRequest())

        status(result) shouldBe NOT_FOUND
        contentAsString(result) shouldBe "No payment allocations found"
      }
    }
    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockGetPaymentAllocations(nino, paymentLot, paymentLotItem)(Left(UnexpectedResponse))

        val result = PaymentAllocationsController.getPaymentAllocations(nino, paymentLot, paymentLotItem)(FakeRequest())

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe "Failed to retrieve payment allocations"
      }
    }
  }

}

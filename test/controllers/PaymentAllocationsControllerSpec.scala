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

import connectors.httpParsers.PaymentAllocationsHttpParser.UnexpectedResponse
import controllers.predicates.AuthenticationPredicate
import mocks.{MockMicroserviceAuthConnector, MockPaymentAllocationsConnector}
import models.paymentAllocations.{AllocationDetail, PaymentAllocations}
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class PaymentAllocationsControllerSpec extends ControllerBaseSpec with MockPaymentAllocationsConnector with MockMicroserviceAuthConnector {

  val controllerComponents: ControllerComponents = stubControllerComponents()

  object PaymentAllocationsController extends PaymentAllocationsController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, controllerComponents),
    cc = controllerComponents,
    paymentAllocationsConnector = mockPaymentAllocationsConnector
  )

  val nino: String = "AA000000A"
  val paymentLot: String = "paymentLot"
  val paymentLotItem: String = "paymentLotItem"

  val paymentAllocations: PaymentAllocations = PaymentAllocations(
    amount = Some(1000.00),
    method = Some("method"),
    transactionDate = Some("transactionDate"),
    allocations = Seq(
      AllocationDetail(
        transactionId = Some("transactionId"),
        from = Some("from"),
        to = Some("to"),
        `type` = Some("type"),
        amount = Some(1500.00),
        clearedAmount = Some(500.00)
      )
    )
  )

  val paymentAllocationsJson: JsObject = Json.toJsObject(paymentAllocations)

  "getPaymentAllocations" should {
    s"return $OK with the retrieved payment allocations" when {
      "the connector returns the payment allocations" in {
        mockAuth()
        mockGetPaymentAllocations(nino, paymentLot, paymentLotItem)(Right(paymentAllocations))

        val result = await(PaymentAllocationsController.getPaymentAllocations(nino, paymentLot, paymentLotItem)(FakeRequest()))

        status(result) shouldBe OK
        jsonBodyOf(result) shouldBe paymentAllocationsJson
      }
    }
    s"return $INTERNAL_SERVER_ERROR" when {
      "the connector returns an error" in {
        mockAuth()
        mockGetPaymentAllocations(nino, paymentLot, paymentLotItem)(Left(UnexpectedResponse))

        val result = await(PaymentAllocationsController.getPaymentAllocations(nino, paymentLot, paymentLotItem)(FakeRequest()))

        status(result) shouldBe INTERNAL_SERVER_ERROR
        bodyOf(result) shouldBe "Failed to retrieve payment allocations"
      }
    }
  }

}

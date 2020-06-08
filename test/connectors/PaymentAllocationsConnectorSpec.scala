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

package connectors

import assets.PreviousCalculationTestConstants._
import connectors.httpParsers.PaymentAllocationsHttpParser.{PaymentAllocationsError, UnexpectedResponse}
import mocks.MockHttp
import models.paymentAllocations.{AllocationDetail, PaymentAllocations}
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestSupport

class PaymentAllocationsConnectorSpec extends TestSupport with MockHttp {

  object TestPaymentAllocationsConnector extends PaymentAllocationsConnector(mockHttpGet, microserviceAppConfig)

  val testPaymentLot: String = "testPaymentLot"
  val testPaymentLotItem: String = "testPaymentLotItem"
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

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

  "paymentAllocationsUrl" should {
    "return the correct url" in {
      val expectedUrl: String = s"${microserviceAppConfig.desUrl}/cross-regime/payment-allocation/NINO/$testNino/ITSA"
      val actualUrl: String = TestPaymentAllocationsConnector.paymentAllocationsUrl(testNino)

      actualUrl shouldBe expectedUrl
    }
  }

  "queryParameters" should {
    "return the correct formatted query parameters" in {
      val expectedQueryParameters: Seq[(String, String)] = Seq(
        "paymentLot" -> testPaymentLot,
        "paymentLotItem" -> testPaymentLotItem
      )
      val actualQueryParameters: Seq[(String, String)] = TestPaymentAllocationsConnector.queryParameters(
        paymentLot = testPaymentLot,
        paymentLotItem = testPaymentLotItem
      )

      actualQueryParameters shouldBe expectedQueryParameters
    }
  }

  "getPaymentAllocations" should {
    "return payment allocations" when {
      s"$OK is returned from the connector call with correct json" in {
        mockDesGet(
          url = TestPaymentAllocationsConnector.paymentAllocationsUrl(testNino),
          queryParameters = TestPaymentAllocationsConnector.queryParameters(testPaymentLot, testPaymentLotItem),
          headerCarrier = TestPaymentAllocationsConnector.desHeaderCarrier
        )(Right(paymentAllocations))

        val result = await(TestPaymentAllocationsConnector.getPaymentAllocations(testNino, testPaymentLot, testPaymentLotItem))

        result shouldBe Right(paymentAllocations)
      }
    }

    s"return an error" when {
      "something went wrong" in {
        mockDesGet[PaymentAllocationsError, PaymentAllocations](
          url = TestPaymentAllocationsConnector.paymentAllocationsUrl(testNino),
          queryParameters = TestPaymentAllocationsConnector.queryParameters(testPaymentLot, testPaymentLotItem),
          headerCarrier = TestPaymentAllocationsConnector.desHeaderCarrier
        )(Left(UnexpectedResponse))

        val result = await(TestPaymentAllocationsConnector.getPaymentAllocations(testNino, testPaymentLot, testPaymentLotItem))

        result shouldBe Left(UnexpectedResponse)
      }
    }
  }
}

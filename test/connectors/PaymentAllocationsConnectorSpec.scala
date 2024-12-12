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

package connectors

import connectors.httpParsers.PaymentAllocationsHttpParser.{NotFoundResponse, UnexpectedResponse}
import mocks.MockHttpV2
import models.paymentAllocations.paymentAllocationsFull
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestSupport

class PaymentAllocationsConnectorSpec extends TestSupport with MockHttpV2 {

  object TestPaymentAllocationsConnector extends PaymentAllocationsConnector(mockHttpClientV2, microserviceAppConfig)

  val testNino: String = "AA000000B"
  val testPaymentLot: String = "testPaymentLot"
  val testPaymentLotItem: String = "testPaymentLotItem"
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

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
        setupMockHttpGetWithHeaderCarrier(
          url = TestPaymentAllocationsConnector.paymentAllocationsUrl(testNino),
          headers = microserviceAppConfig.desAuthHeaders,
        )(
          Right(paymentAllocationsFull)
        )

        val result = TestPaymentAllocationsConnector
          .getPaymentAllocations(testNino, testPaymentLot, testPaymentLotItem).futureValue

        result shouldBe Right(paymentAllocationsFull)
      }
    }

    "return a not found response" when {
      s"$NOT_FOUND is returned from the connector call" in {
        setupMockHttpGetWithHeaderCarrier(
          url = TestPaymentAllocationsConnector.paymentAllocationsUrl(testNino),
          headers = microserviceAppConfig.desAuthHeaders
        )(
          Left(NotFoundResponse)
        )

        val result = TestPaymentAllocationsConnector.getPaymentAllocations(testNino, testPaymentLot, testPaymentLotItem).futureValue

        result shouldBe Left(NotFoundResponse)
      }
    }

    s"return an error" when {
      "something went wrong" in {
        setupMockHttpGetWithHeaderCarrier(
          url = TestPaymentAllocationsConnector.paymentAllocationsUrl(testNino),
          headers = microserviceAppConfig.desAuthHeaders
        )(
          Left(UnexpectedResponse)
        )

        val result = TestPaymentAllocationsConnector.getPaymentAllocations(testNino, testPaymentLot, testPaymentLotItem).futureValue

        result shouldBe Left(UnexpectedResponse)
      }
    }
  }
}

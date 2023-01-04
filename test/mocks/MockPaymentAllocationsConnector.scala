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

package mocks

import connectors.PaymentAllocationsConnector
import connectors.httpParsers.PaymentAllocationsHttpParser.PaymentAllocationsResponse
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockPaymentAllocationsConnector extends WordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockPaymentAllocationsConnector: PaymentAllocationsConnector = mock[PaymentAllocationsConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockPaymentAllocationsConnector)
  }

  def mockGetPaymentAllocations(nino: String, paymentLot: String, paymentLotItem: String)
                               (response: PaymentAllocationsResponse): Unit = {
    when(mockPaymentAllocationsConnector.getPaymentAllocations(
      nino = ArgumentMatchers.eq(nino),
      paymentLot = ArgumentMatchers.eq(paymentLot),
      paymentLotItem = ArgumentMatchers.eq(paymentLotItem)
    )(ArgumentMatchers.any())) thenReturn Future.successful(response)
  }

}

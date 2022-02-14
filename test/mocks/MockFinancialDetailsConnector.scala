/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.FinancialDetailsConnector
import connectors.httpParsers.ChargeHttpParser.ChargeResponse
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockFinancialDetailsConnector extends WordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockFinancialDetailsConnector: FinancialDetailsConnector = mock[FinancialDetailsConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFinancialDetailsConnector)
  }

  def mockListCharges(nino: String, from: String, to: String)
                     (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsConnector.getChargeDetails(
      nino = ArgumentMatchers.eq(nino),
      from = ArgumentMatchers.eq(from),
      to = ArgumentMatchers.eq(to)
    )(ArgumentMatchers.any(), ArgumentMatchers.any())) thenReturn Future.successful(response)
  }

  def mockSingleDocumentDetails(nino: String, documentId: String)
                               (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsConnector.getPaymentAllocationDetails(
      nino = ArgumentMatchers.eq(nino),
      documentId = ArgumentMatchers.eq(documentId)
    )(ArgumentMatchers.any(), ArgumentMatchers.any())) thenReturn Future.successful(response)
  }

  def mockOnlyOpenItems(nino: String)
                       (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsConnector.getOnlyOpenItems(
      nino = ArgumentMatchers.eq(nino)
    )(ArgumentMatchers.any(), ArgumentMatchers.any())) thenReturn Future.successful(response)
  }
}

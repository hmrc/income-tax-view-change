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

import connectors.FinancialDetailsConnector
import connectors.httpParsers.ChargeHttpParser.ChargeResponse
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.Future
import org.mockito.Mockito.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import services.FinancialDetailChargesService
import play.api.libs.json.Json

// TODO: Global EC to be removed ???
import scala.concurrent.ExecutionContext.Implicits.global

trait MockFinancialDetailsConnector extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {
  val mockFinancialDetailsConnector: FinancialDetailsConnector = mock(classOf[FinancialDetailsConnector])
  val mockFinancialDetailsService: FinancialDetailChargesService = mock(classOf[FinancialDetailChargesService])


  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFinancialDetailsConnector)
  }

  def mockListCharges(nino: String, from: String, to: String)
                     (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsService.getChargeDetails(
      ArgumentMatchers.eq(nino),
      ArgumentMatchers.eq(from),
      ArgumentMatchers.eq(to)
    )(ArgumentMatchers.any())) thenReturn Future (
      response match {
        case Right(obj) => Right(Json.toJson(obj))
        case Left(err) => Left(err)
      }
    )
  }

  def mockGetPayments(nino: String, from: String, to: String)
                     (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsService.getPayments(
      ArgumentMatchers.eq(nino),
      ArgumentMatchers.eq(from),
      ArgumentMatchers.eq(to)
    )(ArgumentMatchers.any())) thenReturn Future (
      response match {
        case Right(obj) => Right(Json.toJson(obj.payments))
        case Left(err) => Left(err)
      }
    )
    //Future.successful(response)
  }

  def mockSingleDocumentDetails(nino: String, documentId: String)
                               (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsService.getPaymentAllocationDetails(
      ArgumentMatchers.eq(nino),
      ArgumentMatchers.eq(documentId)
    )(ArgumentMatchers.any())) thenReturn Future (
      response match {
        case Right(obj) => Right(Json.toJson(obj))
        case Left(err) => Left(err)
      }
    )
  }

  def mockOnlyOpenItems(nino: String)
                       (response: ChargeResponse): Unit = {
    when(mockFinancialDetailsService.getOnlyOpenItems(
      ArgumentMatchers.eq(nino)
    )(ArgumentMatchers.any())) thenReturn Future (
      response match {
        case Right(obj) => Right(Json.toJson(obj))
        case Left(err) => Left(err)
      }
    )
  }
}

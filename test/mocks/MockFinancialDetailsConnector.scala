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

import connectors.hip.httpParsers.ChargeHipHttpParser.ChargeHipResponse
import models.credits.CreditsModel
import models.financialDetails.hip.model.ChargesHipResponse
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.Future
import org.mockito.Mockito.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import services.FinancialDetailService
import play.api.libs.json.Json

// TODO: Global EC to be removed ???
import scala.concurrent.ExecutionContext.Implicits.global

trait MockFinancialDetailsConnector extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {
    val mockFinancialDetailsService: FinancialDetailService = mock(classOf[FinancialDetailService])

    def mockListCharges(nino: String, from: String, to: String)
                     (response: ChargeHipResponse): Unit = {
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
                     (response: ChargeHipResponse): Unit = {
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

    def mockCredits(nino: String, from: String, to: String)
                     (response: ChargeHipResponse): Unit = {
    when(mockFinancialDetailsService.getCredits(
      ArgumentMatchers.eq(nino),
      ArgumentMatchers.eq(from),
      ArgumentMatchers.eq(to)
    )(ArgumentMatchers.any())) thenReturn Future (
      response match {
        case Right(charges: ChargesHipResponse) =>
          val creditsModel: CreditsModel = CreditsModel.fromHipChargesResponse(charges)
          Right(Json.toJson(creditsModel))
        case Left(err) =>
          Left(err)
      }
    )
  }

    def mockSingleDocumentDetails(nino: String, documentId: String)
                               (response: ChargeHipResponse): Unit = {
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
                       (response: ChargeHipResponse): Unit = {
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

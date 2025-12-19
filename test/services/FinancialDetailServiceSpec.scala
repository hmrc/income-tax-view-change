/*
 * Copyright 2025 HM Revenue & Customs
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

package services

import config.MicroserviceAppConfig
import connectors.hip.FinancialDetailsHipConnector
import connectors.hip.httpParsers.ChargeHipHttpParser.ChargeHipResponse
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, when}
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.Json
import utils.{FinancialDetailsHipDataHelper, TestSupport}

import scala.concurrent.Future

class FinancialDetailServiceSpec  extends TestSupport with FinancialDetailsHipDataHelper{

  val mockFinancialDetailsHipConnector: FinancialDetailsHipConnector = mock(classOf[FinancialDetailsHipConnector])
  val mockAppConfig: MicroserviceAppConfig = mock(classOf[MicroserviceAppConfig])

  object ServiceUnderTest
    extends FinancialDetailService(mockFinancialDetailsHipConnector, mockAppConfig)

  def setupMockGetPayment(nino: String, fromDate: String, toDate: String)
                         (response: ChargeHipResponse): OngoingStubbing[Future[ChargeHipResponse]] = {
    when(
      mockFinancialDetailsHipConnector.getChargeDetails(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(fromDate),
        ArgumentMatchers.eq(toDate)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }

  def setUpMockPaymentAllocationDetails(nino: String, documentId: String)
                         (response: ChargeHipResponse): OngoingStubbing[Future[ChargeHipResponse]] = {
    when(
      mockFinancialDetailsHipConnector.getPaymentAllocationDetails(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(documentId)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }

  "Call getChargeDetails" should {
    "return success response with Json" when {
      "provided with the correct params" in {
        setupMockGetPayment(testNino, testFromDate, testToDate)(successResponse)
        val expected = ServiceUnderTest.getChargeDetails(testNino, testFromDate, testToDate).futureValue
        expected shouldBe successResponse.map(Json.toJson(_))
      }
    }
  }

  "Call getPayments" should {
    "return success response with Json" when {
      "correct params provided" in {
        setupMockGetPayment(testNino, testFromDate, testToDate)(successResponse)
        val expected = ServiceUnderTest.getPayments(testNino, testFromDate, testToDate).futureValue
        // TODO: atm ~.payments field is empty: need to fix dataChargeHipHttpParser
        expected shouldBe successResponse.map(x => Json.toJson(x.payments))
      }
    }
  }

  "Call getPaymentAllocationDetails" should {
    "return success response with Json" when {
      "correct params provided" in {
        setUpMockPaymentAllocationDetails(testNino, testDocumentId)(successResponse)
        val expected = ServiceUnderTest.getPaymentAllocationDetails(testNino, testDocumentId).futureValue
        expected shouldBe successResponse.map(Json.toJson(_))
      }
    }
  }

}

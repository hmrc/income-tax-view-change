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
import connectors.FinancialDetailsConnector
import connectors.hip.FinancialDetailsHipConnector
import connectors.hip.httpParsers.ChargeHipHttpParser.ChargeHipResponse
import models.hip.GetFinancialDetailsHipApi
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, when}
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.Json
import utils.{FinancialDetailsHipDataHelper, TestSupport}

import scala.concurrent.Future

// TODO: implement service spec~
class FinancialDetailChargesServiceSpec  extends TestSupport with FinancialDetailsHipDataHelper{

  val mockFinancialDetailsHipConnector: FinancialDetailsHipConnector = mock(classOf[FinancialDetailsHipConnector])
  val mockFinancialDetailsConnector: FinancialDetailsConnector = mock(classOf[FinancialDetailsConnector])
  val mockAppConfig: MicroserviceAppConfig = mock(classOf[MicroserviceAppConfig])

  object ServiceUnderTest
    extends FinancialDetailChargesService(mockFinancialDetailsConnector, mockFinancialDetailsHipConnector, mockAppConfig)

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

  def setHipConfigOn(): OngoingStubbing[Boolean] = {
    when(mockAppConfig.hipFeatureSwitchEnabled(GetFinancialDetailsHipApi))
      .thenReturn(true)
  }

  "Hip::call getPayments" should {
    "return success response with Json" when {
      "correct params provided" in {
          val fromDate: String = "2018-01-01"
          val toDate: String = "2017-01-01"

        setHipConfigOn()
        setupMockGetPayment(testNino, fromDate, toDate)(successResponse)
        val expected = ServiceUnderTest.getChargeDetails(testNino, fromDate, toDate).futureValue
        expected shouldBe successResponse.map(Json.toJson(_))
      }
    }

  }

}

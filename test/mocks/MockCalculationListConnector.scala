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

import connectors.CalculationListConnector
import connectors.httpParsers.ChargeHttpParser.HttpGetResult
import models.calculationList.CalculationListResponseModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import org.mockito.Mockito.mock
import scala.concurrent.Future

trait MockCalculationListConnector extends WordSpecLike with Matchers with OptionValues  with BeforeAndAfterEach {

  val mockCalculationListConnector: CalculationListConnector = mock(classOf[CalculationListConnector])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCalculationListConnector)
  }

  def setupMockGetCalculationList(nino: String, taxYear: String)
                                 (response: HttpGetResult[CalculationListResponseModel]): OngoingStubbing[Future[HttpGetResult[CalculationListResponseModel]]] = {
    when(
      mockCalculationListConnector.getCalculationList(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(taxYear)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }


  def setupMockGetCalculationList2324(nino: String, taxYear: String)
                                     (response: HttpGetResult[CalculationListResponseModel]): OngoingStubbing[Future[HttpGetResult[CalculationListResponseModel]]] = {
    when(
      mockCalculationListConnector.getCalculationListTYS(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(taxYear)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }
}

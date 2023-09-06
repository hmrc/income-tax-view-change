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

import connectors.httpParsers.CalculationListHttpParser.HttpGetResult
import models.calculationList.CalculationListResponseModel
import org.mockito.stubbing.OngoingStubbing
import org.mockito.ArgumentMatchers
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import services.CalculationListService
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockCalculationListService extends WordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockCalculationListService: CalculationListService = mock[CalculationListService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCalculationListService)
  }

  def setupMockGetCalculationList(nino: String, taxYear: String)
                                     (response: HttpGetResult[CalculationListResponseModel]):
  OngoingStubbing[Future[HttpGetResult[CalculationListResponseModel]]] = {
    when(
      mockCalculationListService.getCalculationList(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(taxYear)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }

  def setupMockGetCalculationListTYS(nino: String, taxYear: String)
                                 (response: HttpGetResult[CalculationListResponseModel]):
  OngoingStubbing[Future[HttpGetResult[CalculationListResponseModel]]] = {
    when(
      mockCalculationListService.getCalculationListTYS(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(taxYear)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }
}

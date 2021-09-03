/*
 * Copyright 2021 HM Revenue & Customs
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

import assets.BaseTestConstants.mtdRef
import models.core.NinoResponse
import models.incomeSourceDetails.IncomeSourceDetailsResponseModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import org.scalatestplus.mockito.MockitoSugar
import services.IncomeSourceDetailsService

import scala.concurrent.Future


trait MockIncomeSourceDetailsService extends WordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockIncomeSourceDetailsService: IncomeSourceDetailsService = mock[IncomeSourceDetailsService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIncomeSourceDetailsService)
  }

  def setupMockIncomeSourceDetailsResponse(mtdRef: String)(response: IncomeSourceDetailsResponseModel):
  OngoingStubbing[Future[IncomeSourceDetailsResponseModel]] = when(mockIncomeSourceDetailsService.getIncomeSourceDetails(
    ArgumentMatchers.eq(mtdRef))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockNinoResponse(mtdRef: String)(response: NinoResponse):
  OngoingStubbing[Future[NinoResponse]] = when(mockIncomeSourceDetailsService.getNino(
    ArgumentMatchers.eq(mtdRef))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def mockIncomeSourceDetailsResponse(desResponse: IncomeSourceDetailsResponseModel):
  OngoingStubbing[Future[IncomeSourceDetailsResponseModel]] = setupMockIncomeSourceDetailsResponse(mtdRef)(desResponse)

  def mockNinoResponse(desResponse: NinoResponse):
  OngoingStubbing[Future[NinoResponse]] = setupMockNinoResponse(mtdRef)(desResponse)
}

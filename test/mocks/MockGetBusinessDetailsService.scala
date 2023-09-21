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

import assets.BaseTestConstants.testNino
import models.incomeSourceDetails.IncomeSourceDetailsResponseModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import org.mockito.Mockito.mock
import services.GetBusinessDetailsService

import scala.concurrent.Future


trait MockGetBusinessDetailsService extends WordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockGetBusinessDetailsService: GetBusinessDetailsService = mock(classOf[GetBusinessDetailsService])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockGetBusinessDetailsService)
  }

  def setupMockIncomeSourceDetailsResponse(testNino: String)(response: IncomeSourceDetailsResponseModel):
  OngoingStubbing[Future[IncomeSourceDetailsResponseModel]] = when(mockGetBusinessDetailsService.getBusinessDetails(
    ArgumentMatchers.eq(testNino))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def mockIncomeSourceDetailsResponse(desResponse: IncomeSourceDetailsResponseModel):
  OngoingStubbing[Future[IncomeSourceDetailsResponseModel]] = setupMockIncomeSourceDetailsResponse(testNino)(desResponse)

}

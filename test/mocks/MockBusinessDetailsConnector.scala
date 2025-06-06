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

import connectors.BusinessDetailsConnector
import constants.BaseTestConstants.{mtdRef, testNino}
import models.incomeSourceDetails.{BusinessDetailsAccessType, IncomeSourceDetailsResponseModel, MtdId, Nino}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.Future


trait MockBusinessDetailsConnector extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockBusinessDetailsConnector: BusinessDetailsConnector = mock(classOf[BusinessDetailsConnector])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockBusinessDetailsConnector)
  }

  def setupMockGetBusinessDetailsResult(accessMode: BusinessDetailsAccessType)(response: IncomeSourceDetailsResponseModel)
  : OngoingStubbing[Future[IncomeSourceDetailsResponseModel]] = {
    val mtdRefOrNino = accessMode match {
      case Nino => testNino
      case MtdId => mtdRef
    }
    when(mockBusinessDetailsConnector.getBusinessDetails(
      ArgumentMatchers.eq(mtdRefOrNino), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))
  }

  def mockGetBusinessDetailsResult(incomeSourceDetailsResponse: IncomeSourceDetailsResponseModel,
                                   accessMode: BusinessDetailsAccessType): OngoingStubbing[Future[IncomeSourceDetailsResponseModel]] =
    setupMockGetBusinessDetailsResult(accessMode)(incomeSourceDetailsResponse)
}

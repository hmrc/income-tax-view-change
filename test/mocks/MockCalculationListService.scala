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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import services.CalculationListService

import scala.concurrent.Future

trait MockCalculationListService extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockCalculationListService: CalculationListService = mock(classOf[CalculationListService])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCalculationListService)
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

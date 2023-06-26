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

import connectors.httpParsers.PreviousCalculationHttpParser.HttpGetResult
import models.PreviousCalculation.PreviousCalculationModel
import org.mockito.stubbing.OngoingStubbing
import org.mockito.ArgumentMatchers
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import services.PreviousCalculationService
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockPreviousCalculationService extends WordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockCalculationService: PreviousCalculationService = mock[PreviousCalculationService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCalculationService)
  }

  def setupMockGetPreviousCalculation(nino: String, year: String)
                                     (response: HttpGetResult[PreviousCalculationModel]):
  OngoingStubbing[Future[HttpGetResult[PreviousCalculationModel]]] =
    when(
      mockCalculationService.getPreviousCalculation(
        ArgumentMatchers.eq(nino),
        ArgumentMatchers.eq(year)
      )(ArgumentMatchers.any(), ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
}
/*
 * Copyright 2018 HM Revenue & Customs
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

import models.PreviousCalculation.PreviousCalculationModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import services.CalculationService
import uk.gov.hmrc.play.test.UnitSpec
import connectors.httpParsers.CalculationHttpParser.HttpGetResult

import scala.concurrent.Future

trait MockCalculationService extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockCalculationService: CalculationService = mock[CalculationService]

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

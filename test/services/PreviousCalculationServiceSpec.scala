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

package services

import mocks.MockPreviousCalculationConnector
import models.PreviousCalculation._
import utils.TestSupport
import assets.PreviousCalculationTestConstants._
import models.errors.ErrorResponse

class PreviousCalculationServiceSpec extends TestSupport with MockPreviousCalculationConnector {

  object TestPreviousCalculationService extends PreviousCalculationService(mockCalculationConnector)

  "The CalculationService.getPreviousCalculation method" should {

    "Return a PreviousCalculationModel when a success response is returned from the Connector" in {

      val successResponse: Either[Nothing, PreviousCalculationModel] = Right(previousCalculationFull)

      setupMockGetPreviousCalculation(testNino,
        testYear)(successResponse)

      val actual: Either[ErrorResponse, PreviousCalculationModel] = TestPreviousCalculationService.getPreviousCalculation(
        testNino,
        testYear
      ).futureValue

      actual shouldBe successResponse
    }

    "Return Error when a single error is returned from the Connector" in {

      setupMockGetPreviousCalculation(testNino, testYear)(badRequestSingleError)

      val actual: Either[ErrorResponse, PreviousCalculationModel] = TestPreviousCalculationService.getPreviousCalculation(
        testNino,
        testYear
      ).futureValue

      actual shouldBe badRequestSingleError
    }

    "Return a MultiError when multiple error responses are returned from the Connector" in {

      setupMockGetPreviousCalculation(testNino, testYear)(badRequestMultiError)

      val actual: Either[ErrorResponse, PreviousCalculationModel] = TestPreviousCalculationService.getPreviousCalculation(
        testNino,
        testYear
      ).futureValue

      actual shouldBe badRequestMultiError
    }
  }
}
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

import constants.BaseTestConstants.{testNino, testTaxYearEnd, testTaxYearRange}
import constants.CalculationListDesTestConstants.{badRequestMultiError, badRequestSingleError, calculationListFull}
import mocks.MockCalculationListConnector
import models.calculationList.CalculationListResponseModel
import models.errors.ErrorResponse
import utils.TestSupport

class CalculationListServiceSpec extends TestSupport with MockCalculationListConnector {

  object TestCalculationListService extends CalculationListService(mockCalculationListConnector)

  "CalculationListService.getCalculationList" should {
    "return a CalculationListResponseModel" when {
      "a success response is received from the Connector" in {
        val successResponse: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)

        setupMockGetCalculationList(testNino, testTaxYearEnd)(successResponse)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationList(
          testNino,
          testTaxYearEnd
        ).futureValue

        expected shouldBe successResponse
      }
    }
    "return a single error" when {
      "a single error response is received from the Connector" in {
        setupMockGetCalculationList(testNino, testTaxYearEnd)(badRequestSingleError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationList(
          testNino,
          testTaxYearEnd
        ).futureValue

        expected shouldBe badRequestSingleError
      }
    }
    "return a multi error" when {
      "multiple error responses are received from the Connector" in {
        setupMockGetCalculationList(testNino, testTaxYearEnd)(badRequestMultiError)
        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationList(
          testNino,
          testTaxYearEnd
        ).futureValue

        expected shouldBe badRequestMultiError
      }
    }
  }
  "CalculationListService.getCalculationListTYS" should {
    "return a CalculationListResponseModel" when {
      "a success response is received from the Connector" in {
        val successResponse: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)

        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(successResponse)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe successResponse
      }
    }
    "return a single error" when {
      "a single error response is received from the Connector" in {
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(badRequestSingleError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe badRequestSingleError
      }
    }
    "return a multi error" when {
      "multiple error responses are received from the Connector" in {
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(badRequestMultiError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe badRequestMultiError
      }
    }
  }

}

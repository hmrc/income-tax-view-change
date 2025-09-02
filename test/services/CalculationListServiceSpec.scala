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

import config.MicroserviceAppConfig
import constants.BaseTestConstants.{testNino, testTaxYearRange}
import constants.CalculationListDesTestConstants.{badRequestMultiError, badRequestSingleError, calculationListFull}
import mocks.MockCalculationListConnector
import mocks.hip.MockCalculationListHipConnector
import models.calculationList.CalculationListResponseModel
import models.errors.ErrorResponse
import models.hip.GetCalcListTYSHipApi
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import utils.TestSupport

class CalculationListServiceSpec extends TestSupport with MockCalculationListConnector with MockCalculationListHipConnector {

  val mockAppConfig: MicroserviceAppConfig = mock[MicroserviceAppConfig]
  object TestCalculationListService extends CalculationListService(mockCalculationListConnector, mockCalculationListHipConnector, mockAppConfig)

  "CalculationListService.getCalculationListTYS" should {
    "return a CalculationListResponseModel" when {
      "a success response is received from the IF Connector" in {
        val successResponse: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)

        when(mockAppConfig.hipFeatureSwitchEnabled(GetCalcListTYSHipApi)).thenReturn(false)
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(successResponse)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe successResponse
      }

      "a success response is received from the HIP Connector" in {
        val successResponse: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)

        when(mockAppConfig.hipFeatureSwitchEnabled(GetCalcListTYSHipApi)).thenReturn(true)
        setupMockGetCalculationListHipTYS(testNino, testTaxYearRange)(successResponse)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe successResponse
      }
    }
    "return a single error" when {
      "a single error response is received from the IF Connector" in {
        when(mockAppConfig.hipFeatureSwitchEnabled(GetCalcListTYSHipApi)).thenReturn(false)
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(badRequestSingleError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe badRequestSingleError
      }

      "a single error response is received from the HIP Connector" in {
        when(mockAppConfig.hipFeatureSwitchEnabled(GetCalcListTYSHipApi)).thenReturn(true)
        setupMockGetCalculationListHipTYS(testNino, testTaxYearRange)(badRequestSingleError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe badRequestSingleError
      }
    }
    "return a multi error" when {
      "multiple error responses are received from the IF Connector" in {
        when(mockAppConfig.hipFeatureSwitchEnabled(GetCalcListTYSHipApi)).thenReturn(false)
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(badRequestMultiError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe badRequestMultiError
      }

      "multiple error responses are received from the HIP Connector" in {
        when(mockAppConfig.hipFeatureSwitchEnabled(GetCalcListTYSHipApi)).thenReturn(true)
        setupMockGetCalculationListHipTYS(testNino, testTaxYearRange)(badRequestMultiError)

        val expected: Either[ErrorResponse, CalculationListResponseModel] = TestCalculationListService.getCalculationListTYS(
          testNino,
          testTaxYearRange
        ).futureValue

        expected shouldBe badRequestMultiError
      }
    }
  }

}

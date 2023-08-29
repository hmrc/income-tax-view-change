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

package connectors

import assets.BaseTestConstants.{testNino, testTaxYearEnd, testTaxYearRange}
import assets.CalculationListTestConstants.{badRequestSingleError, calculationListFull}
import connectors.httpParsers.CalculationListHttpParser
import mocks.MockHttp
import models.calculationList.CalculationListResponseModel
import models.errors.ErrorResponse
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.when
import utils.TestSupport

import scala.concurrent.Future

class CalculationListConnectorSpec extends TestSupport with MockHttp {

  object TestCalculationListConnector extends CalculationListConnector(mockHttpGet, microserviceAppConfig)
  val url1404 = s"${microserviceAppConfig.desUrl}/income-tax/list-of-calculation-results/$testNino?taxYear=$testTaxYearEnd"
  val url1896 = s"${microserviceAppConfig.desUrl}/income-tax/view/calculations/liability/$testTaxYearRange/$testNino"

  "The CalculationListConnector" should {
    "format API URLs correctly" when {
      "getCalculationListUrl is called" in {
        TestCalculationListConnector.getCalculationListUrl(testNino, testTaxYearEnd) shouldBe url1404
      }
      "getCalculationList2324Url is called" in {
        TestCalculationListConnector.getCalculationListTYSUrl(testNino, testTaxYearRange) shouldBe url1896
      }
    }
    "return a CalculationList model" when {
      val successResponse: Either[Nothing, CalculationListResponseModel] =
        Right(calculationListFull)
      "calling getCalculationList with a valid NINO" in {
        when(mockHttpGet.GET[CalculationListHttpParser.HttpGetResult[CalculationListResponseModel]](contains(url1404),
          ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(successResponse))

        TestCalculationListConnector.getCalculationList(testNino, testTaxYearEnd).futureValue shouldBe successResponse
      }
      "calling getCalculationList2324 with a valid NINO and tax year" in {
        when(mockHttpGet.GET[CalculationListHttpParser.HttpGetResult[CalculationListResponseModel]](contains(url1896),
          ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(successResponse))

        TestCalculationListConnector.getCalculationListTYS(testNino, testTaxYearRange).futureValue shouldBe successResponse
      }
    }
    "return an ErrorResponse model" when {
      "calling getCalculationList and a non-success response is received" in {
        when(mockHttpGet.GET[Either[ErrorResponse, Nothing]](contains(url1404),
          ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(badRequestSingleError))

        TestCalculationListConnector.getCalculationList(testNino, testTaxYearEnd).futureValue shouldBe badRequestSingleError
      }
      "calling getCalculationList2324 and a non-success response is received" in {
        when(mockHttpGet.GET[Either[ErrorResponse, Nothing]](contains(url1896),
          ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(badRequestSingleError))
        val result = TestCalculationListConnector.getCalculationListTYS(testNino, testTaxYearRange)
        result.futureValue shouldBe badRequestSingleError
      }
    }
  }

}

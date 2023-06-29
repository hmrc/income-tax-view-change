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

import assets.PreviousCalculationTestConstants._
import connectors.httpParsers.PreviousCalculationHttpParser
import mocks.MockHttp
import models.PreviousCalculation._
import models.errors.ErrorResponse
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.when
import utils.TestSupport

import scala.concurrent.Future

class PreviousCalculationConnectorSpec extends TestSupport with MockHttp {

  val successResponse: Either[Nothing, PreviousCalculationModel] =
    Right(previousCalculationFull)

  object TestPreviousCalculationConnector extends PreviousCalculationConnector(mockHttpGet, microserviceAppConfig)

  "The PreviousCalculationConnector" should {

    "format the request url correctly for previous-tax-calculation DES requests" in {
      lazy val actualUrl: String =
        TestPreviousCalculationConnector.getPreviousCalculationUrl(testNino, testYear)
      lazy val expectedUrl: String = s"${microserviceAppConfig.desUrl}/income-tax/previous-calculation/" +
        s"$testNino?year=$testYear"
      actualUrl shouldBe expectedUrl
    }

    "when calling the getPreviousCalculation" when {
      "calling with a valid nino and year the success response received" should {

        "return a PreviousCalculationModel model" in {
          when(mockHttpGet.GET[PreviousCalculationHttpParser.HttpGetResult[PreviousCalculationModel]](contains(s"""/income-tax/previous-calculation/$testNino?year=$testYear"""),
            ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(successResponse))
          val result: Future[PreviousCalculationHttpParser.HttpGetResult[PreviousCalculationModel]] =
            TestPreviousCalculationConnector.getPreviousCalculation(nino = testNino, year = testYear)
          result.futureValue shouldBe successResponse
        }
      }
    }

    "calling for a user with non-success response received, single error" should {

      "return a Error model" in {
        when(mockHttpGet.GET[Either[ErrorResponse, Nothing]](contains(s"""/income-tax/previous-calculation/$testNino?year=$testYear"""),
          ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(badRequestSingleError))
        val result: Future[PreviousCalculationHttpParser.HttpGetResult[PreviousCalculationModel]] =
          TestPreviousCalculationConnector.getPreviousCalculation(
            nino = testNino,
            year = testYear
          )
        result.futureValue shouldBe badRequestSingleError
      }
    }

    "calling for a user with non-success response received, multi error" should {

      "return a MultiError model" in {
        when(mockHttpGet.GET[Either[ErrorResponse, Nothing]](contains(s"""/income-tax/previous-calculation/$testNino?year=$testYear"""),
          ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(badRequestMultiError))
        val result: Future[PreviousCalculationHttpParser.HttpGetResult[PreviousCalculationModel]] =
          TestPreviousCalculationConnector.getPreviousCalculation(
            nino = testNino,
            year = testYear
          )
        result.futureValue shouldBe badRequestMultiError
      }
    }
  }
}

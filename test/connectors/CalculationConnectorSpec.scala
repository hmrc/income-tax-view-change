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

package connectors

import utils.TestSupport
import connectors.httpParsers.CalculationHttpParser
import mocks.MockHttp
import models.PreviousCalculation._
import assets.PreviousCalculationTestConstants._

import scala.concurrent.Future

class CalculationConnectorSpec extends TestSupport with MockHttp {

  val successResponse: Either[Nothing, PreviousCalculationModel] =
    Right(previousCalculationFull)

  object TestCalculationConnector extends CalculationConnector(mockHttpGet, microserviceAppConfig)

  "The CalculationConnector" should {

    "format the request url correctly for previous-tax-calculation DES requests" in {
      lazy val actualUrl: String =
        TestCalculationConnector.getPreviousCalculationUrl(testNino, testYear)
      lazy val expectedUrl: String = s"${microserviceAppConfig.desUrl}/income-tax/previous-calculation/" +
        s"$testNino?year=$testYear"
      actualUrl shouldBe expectedUrl
    }

    "when calling the getPreviousCalculation" when {
      "calling with a valid nino and year the success response received" should {

        "return a PreviousCalculationModel model" in {
          setupMockHttpFutureGet(TestCalculationConnector.getPreviousCalculationUrl(testNino, testYear))(successResponse)
          val result: Future[CalculationHttpParser.HttpGetResult[PreviousCalculationModel]] =
            TestCalculationConnector.getPreviousCalculation(nino = testNino, year = testYear)
          await(result) shouldBe successResponse
        }
      }
    }

    "calling for a user with non-success response received, single error" should {

      "return a Error model" in {
        setupMockHttpFutureGet(TestCalculationConnector.getPreviousCalculationUrl(testNino,
          testYear))(badRequestSingleError)
        val result: Future[CalculationHttpParser.HttpGetResult[PreviousCalculationModel]] =
          TestCalculationConnector.getPreviousCalculation(
            nino = testNino,
            year = testYear
          )
        await(result) shouldBe badRequestSingleError
      }
    }

    "calling for a user with non-success response received, multi error" should {

      "return a MultiError model" in {
        setupMockHttpFutureGet(TestCalculationConnector.getPreviousCalculationUrl(testNino,
          testYear))(badRequestMultiError)
        val result: Future[CalculationHttpParser.HttpGetResult[PreviousCalculationModel]] =
          TestCalculationConnector.getPreviousCalculation(
            nino = testNino,
            year = testYear
          )
        await(result) shouldBe badRequestMultiError
      }
    }
  }
}

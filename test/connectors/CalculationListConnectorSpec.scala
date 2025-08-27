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

import constants.BaseTestConstants.{testNino, testTaxYearRange}
import constants.CalculationListDesTestConstants._
import mocks.MockHttpV2
import models.errors.{ErrorResponse, UnexpectedJsonFormat}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListConnectorSpec extends TestSupport with MockHttpV2 {

  object TestCalculationListConnector extends CalculationListConnector(mockHttpClientV2, microserviceAppConfig)
  val url1896 = s"${microserviceAppConfig.desUrl}/income-tax/view/calculations/liability/$testTaxYearRange/$testNino"
  lazy val mockUrl1896: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(url1896, microserviceAppConfig.getIFHeaders("1896"))(_)
  lazy val mockUrl1896Failed: Either[ErrorResponse, Nothing] => Unit =
    setupMockHttpGetWithHeaderCarrier(url1896, microserviceAppConfig.getIFHeaders("1896"))(_)

  "The CalculationListConnector" should {
    "format API URLs correctly" when {
      "getCalculationListTYSUrl is called" in {
        TestCalculationListConnector.getCalculationListTYSUrl(testNino, testTaxYearRange) shouldBe url1896
      }
    }

    "return a CalculationList model" when {

      "calling getCalculationListTYS with a valid NINO and tax year" in {
        mockUrl1896(successResponse)
        TestCalculationListConnector.getCalculationListTYS(testNino, testTaxYearRange).futureValue shouldBe successResponse
      }
    }

    "return an ErrorResponse model" when {
      "calling getCalculationListTYS and a non-success response is received" in {
        mockUrl1896Failed(Left(UnexpectedJsonFormat))
        val result = TestCalculationListConnector.getCalculationListTYS(testNino, testTaxYearRange)
        result.futureValue shouldBe unexpectedJsonFormat
      }
    }
  }
}

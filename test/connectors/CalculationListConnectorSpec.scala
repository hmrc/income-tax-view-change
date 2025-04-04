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
import assets.CalculationListDesTestConstants._
import mocks.MockHttpV2
import models.errors.{ErrorResponse, UnexpectedJsonFormat}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListConnectorSpec extends TestSupport with MockHttpV2 {

  object TestCalculationListConnector extends CalculationListConnector(mockHttpClientV2, microserviceAppConfig)
  val platform: String = if (microserviceAppConfig.useGetCalcListIFPlatform) microserviceAppConfig.ifUrl else microserviceAppConfig.desUrl
  val url1404 = s"$platform/income-tax/list-of-calculation-results/$testNino?taxYear=$testTaxYearEnd"
  val url1896 = s"${microserviceAppConfig.desUrl}/income-tax/view/calculations/liability/$testTaxYearRange/$testNino"
  val header1404: Seq[(String, String)] = if (microserviceAppConfig.useGetCalcListIFPlatform) microserviceAppConfig.getIFHeaders("1404") else microserviceAppConfig.desAuthHeaders

  lazy val mockUrl1404: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(url1404, microserviceAppConfig.getIFHeaders("1404"))(_)
  lazy val mockUrl1404Failed: Either[ErrorResponse, Nothing] => Unit =
    setupMockHttpGetWithHeaderCarrier(url1404, microserviceAppConfig.getIFHeaders("1404"))(_)
  lazy val mockUrl1896: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(url1896, microserviceAppConfig.getIFHeaders("1896"))(_)
  lazy val mockUrl1896Failed: Either[ErrorResponse, Nothing] => Unit =
    setupMockHttpGetWithHeaderCarrier(url1896, microserviceAppConfig.getIFHeaders("1896"))(_)

  "The CalculationListConnector" should {
    "format API URLs correctly" when {
      "getCalculationListUrl is called" in {
        TestCalculationListConnector.getCalculationListUrl(testNino, testTaxYearEnd) shouldBe url1404
      }

      "getCalculationListTYSUrl is called" in {
        TestCalculationListConnector.getCalculationListTYSUrl(testNino, testTaxYearRange) shouldBe url1896
      }
    }

    "format API Headers correctly" when {
      "getHeaders is called" in {
        TestCalculationListConnector.getHeaders(api = "1404") shouldBe header1404
      }
    }

    "return a CalculationList model" when {

      "calling getCalculationList with a valid NINO" in {
       mockUrl1404(successResponse)
        TestCalculationListConnector.getCalculationList(testNino, testTaxYearEnd).futureValue shouldBe successResponse
      }

      "calling getCalculationListTYS with a valid NINO and tax year" in {
        mockUrl1896(successResponse)
        TestCalculationListConnector.getCalculationListTYS(testNino, testTaxYearRange).futureValue shouldBe successResponse
      }
    }

    "return an ErrorResponse model" when {
      "calling getCalculationList and a non-success response is received" in {
        mockUrl1404Failed(Left(UnexpectedJsonFormat))
        TestCalculationListConnector.getCalculationList(testNino, testTaxYearEnd).futureValue shouldBe unexpectedJsonFormat
      }

      "calling getCalculationListTYS and a non-success response is received" in {
        mockUrl1896Failed(Left(UnexpectedJsonFormat))
        val result = TestCalculationListConnector.getCalculationListTYS(testNino, testTaxYearRange)
        result.futureValue shouldBe unexpectedJsonFormat
      }
    }
  }
}

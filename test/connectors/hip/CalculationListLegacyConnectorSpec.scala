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

package connectors.hip

import constants.BaseTestConstants.{testNino, testTaxYearEnd}
import constants.CalculationListTestConstants._
import mocks.MockHttpV2
import models.hipErrors.{ErrorResponse, UnexpectedJsonResponse}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListLegacyConnectorSpec extends TestSupport with MockHttpV2 {

  object TestCalculationListLegacyConnector$ extends CalculationListLegacyConnector(mockHttpClientV2, microserviceAppConfig)
  val platform: String = microserviceAppConfig.hipUrl
  val url1404 = s"$platform/itsd/calculations/liability/$testNino?taxYear=$testTaxYearEnd"
  val header1404: Seq[(String, String)] = microserviceAppConfig.getHIPHeaders("get-legacy-calc-list-1404", hc.requestId)

  lazy val mockUrl1404: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(url1404, header1404)(_)
  lazy val mockUrl1404Failed: Either[ErrorResponse, Nothing] => Unit =
    setupMockHttpGetWithHeaderCarrier(url1404, header1404)(_)

  "The CalculationListLegacyConnector" should {
    "format API URLs correctly" when {
      "getCalculationListUrl is called" in {
        TestCalculationListLegacyConnector$.getCalculationListUrl(testNino, testTaxYearEnd) shouldBe url1404
      }
    }
    "correlation id is in the right format" when {
      "getHipHeaders is called" in {
        val regex = "^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"
        header1404.last._2.matches(regex) shouldBe true
      }
    }

    "return a CalculationList model" when {
      "calling getCalculationList with a valid NINO" in {
       mockUrl1404(successResponse)
        TestCalculationListLegacyConnector$.getCalculationList(testNino, testTaxYearEnd).futureValue shouldBe successResponse
      }
    }

    "return an ErrorResponse model" when {
      "calling getCalculationList and a non-success response is received" in {
        mockUrl1404Failed(Left(UnexpectedJsonResponse))
        TestCalculationListLegacyConnector$.getCalculationList(testNino, testTaxYearEnd).futureValue shouldBe Left(UnexpectedJsonResponse)
      }
    }
  }
}

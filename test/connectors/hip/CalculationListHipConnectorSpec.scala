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

import constants.BaseTestConstants.{testNino, testTaxYearRange}
import constants.CalculationListTestConstants._
import mocks.MockHttpV2
import models.hip.{ErrorResponse, GetCalcListTYSHipApi, UnexpectedJsonResponse}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListHipConnectorSpec extends TestSupport with MockHttpV2 {

  object TestCalculationListHipConnector extends CalculationListHipConnector(mockHttpClientV2, microserviceAppConfig)
  val platform: String = microserviceAppConfig.hipUrl
  val url5624 = s"$platform/itsa/income-tax/v1/$testTaxYearRange/view/calculations/liability/$testNino"
  val header5624: Seq[(String, String)] = microserviceAppConfig.getHIPHeaders(GetCalcListTYSHipApi)

  lazy val mockUrl5624: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(url5624, header5624)(_)
  lazy val mockUrl5624Failed: Either[ErrorResponse, Nothing] => Unit =
    setupMockHttpGetWithHeaderCarrier(url5624, header5624)(_)

  "The CalculationListHipConnector" should {
    "format API URLs correctly" when {
      "getCalculationListTYSUrl is called" in {
        TestCalculationListHipConnector.getCalculationListTYSHipUrl(testNino, testTaxYearRange) shouldBe url5624
      }
    }
    "correlation id is in the right format" when {
      "getHipHeaders is called" in {
        val regex = "^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"
        header5624.last._2.matches(regex) shouldBe true
      }
    }

    "return a CalculationList model" when {
      "calling getCalculationListTYS with a valid NINO" in {
       mockUrl5624(successResponse)
        TestCalculationListHipConnector.getCalculationListTYS(testNino, testTaxYearRange).futureValue shouldBe successResponse
      }
    }

    "return an ErrorResponse model" when {
      "calling getCalculationList and a non-success response is received" in {
        mockUrl5624Failed(Left(UnexpectedJsonResponse))
        TestCalculationListHipConnector.getCalculationListTYS(testNino, testTaxYearRange).futureValue shouldBe Left(UnexpectedJsonResponse)
      }
    }
  }
}

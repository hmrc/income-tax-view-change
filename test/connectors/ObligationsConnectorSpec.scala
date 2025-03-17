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

import constants.BaseTestConstants.testNino
import constants.ObligationsTestConstants._
import mocks.MockHttpV2
import models.obligations.ObligationsResponseModel
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class ObligationsConnectorSpec extends TestSupport with MockHttpV2{

  trait Setup {

    val connector: ObligationsConnector = new ObligationsConnector(
      mockHttpClientV2,
      microserviceAppConfig
    )

    val desUrl: String = microserviceAppConfig.desUrl
    val dateFrom = "2020-04-06"
    val dateTo = "2021-04-05"

    val headers: Seq[(String, String)] = microserviceAppConfig.desAuthHeaders
    val getOpenObligationsUrl = s"$desUrl/enterprise/obligation-data/nino/$testNino/ITSA?status=O"
    val getAllObligationsDateRangeUrl = s"$desUrl/enterprise/obligation-data/nino/$testNino/ITSA?from=$dateFrom&to=$dateTo"

    val mockSuccessGetOpenObligations = setupMockHttpGetWithHeaderCarrier[HttpResponse](getOpenObligationsUrl, headers)(_)
    val mockSuccessGetOpenObligationsDateRange = setupMockHttpGetWithHeaderCarrier[HttpResponse](getAllObligationsDateRangeUrl, headers)(_)

    val mockFailedGetOpenObligations = setupMockHttpGetWithHeaderCarrier[HttpResponse](getOpenObligationsUrl, headers)(_)
    val mockFailedGetOpenObligationsDateRange = setupMockHttpGetWithHeaderCarrier[HttpResponse](getAllObligationsDateRangeUrl, headers)(_)

  }

  "getOpenObligationsUrl" should {

    "return a valid URL" when {

      "called for open obligations" in new Setup {
        connector.getOpenObligationsUrl(testNino) shouldBe
          s"http://localhost:9084/enterprise/obligation-data/nino/$testNino/ITSA?status=O"
      }
    }
  }

  "getOpenObligations connector method" should {
    s"return a obligations model when the endpoint returns Status: $OK" when {
      "called for open obligations" in new Setup {
        mockSuccessGetOpenObligations(successResponse)
        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testObligations
      }
    }

    "return a report deadlines error model" when {
      s"the connector does not receive a Status:$OK response" in new Setup {
        mockFailedGetOpenObligations(badResponse)
        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testReportDeadlinesError
      }

      "the json received can not be parsed into an obligations model" in new Setup {
        mockFailedGetOpenObligations(badJson)
        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testReportDeadlinesErrorJson
      }

      "the http call failed and returned a future failed" in new Setup {
        val exceptionMessage: String = "Exception message"
        setupMockFailedHttpV2Get(getOpenObligationsUrl, exceptionMessage)
        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testReportDeadlinesErrorFutureFailed(exceptionMessage)
      }
    }
  }

  "getPreviousObligations" should {
    "return an obligations model" when {
      s"$OK is return with valid json" in new Setup {
        mockSuccessGetOpenObligationsDateRange(successResponse)
        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue

        result shouldBe testObligations
      }
    }

    "return a report deadline error model" when {
      s"$OK is returned but the json is invalid" in new Setup {
        mockFailedGetOpenObligationsDateRange(badJson)

        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue

        result shouldBe testReportDeadlinesErrorJson
      }

      s"a status which is not $OK is returned" in new Setup {
        mockFailedGetOpenObligationsDateRange(badResponse)
        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue

        result shouldBe testReportDeadlinesError
      }

      s"there was a problem making the call" in new Setup {
        val exception = "test exception"
        setupMockFailedHttpV2Get(getAllObligationsDateRangeUrl, exception)
        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue

        result shouldBe testReportDeadlinesErrorFutureFailed(exception)
      }
    }
  }
}

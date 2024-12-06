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

import assets.BaseTestConstants.testNino
import assets.ObligationsTestConstants._
import models.obligations.ObligationsResponseModel
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, eq => matches}
import org.mockito.Mockito._
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpClient, HttpResponse}
import utils.TestSupport

import scala.annotation.nowarn
import scala.concurrent.Future

class ObligationsConnectorSpec extends TestSupport {

  trait Setup {
    //TODO: Remove suppression annotation after upgrading this file to use HttpClientV2
    @nowarn("cat=deprecation")
    val httpClient: HttpClient = mock(classOf[HttpClient])

    val connector: ObligationsConnector = new ObligationsConnector(
      httpClient,
      microserviceAppConfig
    )
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
        when(httpClient.GET[HttpResponse](matches(connector.getOpenObligationsUrl(testNino)),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))
          (any(), any(), any()))
          .thenReturn(Future.successful(successResponse))

        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testObligations
      }

    }

    "return a report deadlines error model" when {
      s"the connector does not receive a Status:$OK response" in new Setup {
        when(httpClient.GET[HttpResponse](matches(connector.getOpenObligationsUrl(testNino)),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.successful(badResponse))

        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testReportDeadlinesError
      }
      "the json received can not be parsed into an obligations model" in new Setup {
        when(httpClient.GET[HttpResponse](matches(connector.getOpenObligationsUrl(testNino)),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.successful(badJson))

        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testReportDeadlinesErrorJson
      }
      "the http call failed and returned a future failed" in new Setup {
        val exceptionMessage: String = "Exception message"

        when(httpClient.GET[HttpResponse](matches(connector.getOpenObligationsUrl(testNino)),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.failed(new Exception(exceptionMessage)))

        val result: ObligationsResponseModel = connector.getOpenObligations(testNino).futureValue

        result shouldBe testReportDeadlinesErrorFutureFailed(exceptionMessage)
      }
    }
  }

  "getPreviousObligations" should {
    "return an obligations model" when {
      s"$OK is return with valid json" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getAllObligationsDateRangeUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.successful(successResponse))

        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, "2020-04-06", "2021-04-05").futureValue

        result shouldBe testObligations
      }
    }
    "return a report deadline error model" when {
      s"$OK is returned but the json is invalid" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getAllObligationsDateRangeUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.successful(badJson))

        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, "2020-04-06", "2021-04-05").futureValue

        result shouldBe testReportDeadlinesErrorJson
      }
      s"a status which is not $OK is returned" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getAllObligationsDateRangeUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.successful(badResponse))

        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, "2020-04-06", "2021-04-05").futureValue

        result shouldBe testReportDeadlinesError
      }
      s"there was a problem making the call" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getAllObligationsDateRangeUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), ArgumentMatchers.eq[Seq[(String, String)]](microserviceAppConfig.desAuthHeaders))(any(), any(), any()))
          .thenReturn(Future.failed(new Exception("test exception")))

        val result: ObligationsResponseModel = connector.getAllObligationsWithinDateRange(testNino, "2020-04-06", "2021-04-05").futureValue

        result shouldBe testReportDeadlinesErrorFutureFailed("test exception")
      }
    }
  }

}
/*
 * Copyright 2021 HM Revenue & Customs
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
import assets.ReportDeadlinesTestConstants._
import models.reportDeadlines.ReportDeadlinesResponseModel
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, eq => matches}
import play.api.http.Status._
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.TestSupport
import org.mockito.Mockito._

import java.time.LocalDate
import scala.concurrent.Future

class ReportDeadlinesConnectorSpec extends TestSupport {

  trait Setup {
    val httpClient: HttpClient = mock[HttpClient]

    val headerCarrier: HeaderCarrier = hc.copy(
      authorization = Some(Authorization(s"Bearer ${microserviceAppConfig.desToken}"))
    ).withExtraHeaders("Environment" -> microserviceAppConfig.desEnvironment)

    val connector: ReportDeadlinesConnector = new ReportDeadlinesConnector(
      httpClient,
      microserviceAppConfig
    )
  }

  "getReportDeadlinesUrl" should {

    "return a valid URL" when {

      "called for open obligations" in new Setup {
        connector.getReportDeadlinesUrl(testNino, openObligations = true) shouldBe
          s"http://localhost:9084/enterprise/obligation-data/nino/$testNino/ITSA?status=O"
      }

      "called for fulfilled obligations" in new Setup {
        val date: LocalDate = LocalDate.now()
        connector.getReportDeadlinesUrl(testNino, openObligations = false) shouldBe
          s"http://localhost:9084/enterprise/obligation-data/nino/$testNino/ITSA?status=F&from=${date.minusDays(365)}&to=$date"
      }
    }
  }

  "getReportDeadlines" should {
    s"return a report deadlines model when the endpoint returns Status: $OK" when {
      "called for open obligations" in new Setup {
        when(httpClient.GET[HttpResponse](matches(connector.getReportDeadlinesUrl(testNino, openObligations = true)), any(), any())
          (any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.successful(successResponse))

        val result: ReportDeadlinesResponseModel = await(connector.getReportDeadlines(testNino, openObligations = true))

        result shouldBe testObligations
      }
      "called for fulfilled obligations" in new Setup {
        when(httpClient.GET[HttpResponse](matches(connector.getReportDeadlinesUrl(testNino, openObligations = false)),
          any(), any())(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.successful(successResponse))

        val result: ReportDeadlinesResponseModel = await(connector.getReportDeadlines(testNino, openObligations = false))

        result shouldBe testObligations
      }
    }

    "return a report deadlines error model" when {
      s"the connector does not receive a Status:$OK response" in new Setup {
        when(httpClient.GET[HttpResponse](matches(connector.getReportDeadlinesUrl(testNino, openObligations = true)),
          any(), any())(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.successful(badResponse))

        val result: ReportDeadlinesResponseModel = await(connector.getReportDeadlines(testNino, openObligations = true))

        result shouldBe testReportDeadlinesError
      }
      "the json received can not be parsed into an obligations model" in new Setup {
        when(httpClient.GET[HttpResponse](matches(connector.getReportDeadlinesUrl(testNino, openObligations = true)),
          any(), any())(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.successful(badJson))

        val result: ReportDeadlinesResponseModel = await(connector.getReportDeadlines(testNino, openObligations = true))

        result shouldBe testReportDeadlinesErrorJson
      }
      "the http call failed and returned a future failed" in new Setup {
        val exceptionMessage: String = "Exception message"

        when(httpClient.GET[HttpResponse](matches(connector.getReportDeadlinesUrl(testNino, openObligations = true)),
          any(), any())(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.failed(new Exception(exceptionMessage)))

        val result: ReportDeadlinesResponseModel = await(connector.getReportDeadlines(testNino, openObligations = true))

        result shouldBe testReportDeadlinesErrorFutureFailed(exceptionMessage)
      }
    }
  }

  "getPreviousObligations" should {
    "return an obligations model" when {
      s"$OK is return with valid json" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getPreviousObligationsUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), any())(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.successful(successResponse))

        val result: ReportDeadlinesResponseModel = await(connector.getPreviousObligations(testNino, "2020-04-06", "2021-04-05"))

        result shouldBe testObligations
      }
    }
    "return a report deadline error model" when {
      s"$OK is returned but the json is invalid" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getPreviousObligationsUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), any())(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.successful(badJson))

        val result: ReportDeadlinesResponseModel = await(connector.getPreviousObligations(testNino, "2020-04-06", "2021-04-05"))

        result shouldBe testReportDeadlinesErrorJson
      }
      s"a status which is not $OK is returned" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getPreviousObligationsUrl(testNino, "2020-04-06", "2021-04-05")),
          any(), any())(any(), ArgumentMatchers.eq(headerCarrier), any()))
          .thenReturn(Future.successful(badResponse))

        val result: ReportDeadlinesResponseModel = await(connector.getPreviousObligations(testNino, "2020-04-06", "2021-04-05"))

        result shouldBe testReportDeadlinesError
      }
      s"there was a problem making the call" in new Setup {
        when(httpClient.GET[HttpResponse](
          matches(connector.getPreviousObligationsUrl(testNino, "2020-04-06", "2021-04-05")), any(), any()
        )(any(), ArgumentMatchers.eq[HeaderCarrier](headerCarrier), any()))
          .thenReturn(Future.failed(new Exception("test exception")))

        val result: ReportDeadlinesResponseModel = await(connector.getPreviousObligations(testNino, "2020-04-06", "2021-04-05"))

        result shouldBe testReportDeadlinesErrorFutureFailed("test exception")
      }
    }
  }

}
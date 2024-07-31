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

package controllers

import assets.BaseTestConstants._
import assets.ObligationsTestConstants._
import connectors.ObligationsConnector
import controllers.predicates.AuthenticationPredicate
import mocks.MockMicroserviceAuthConnector
import org.mockito.ArgumentMatchers.{any, eq => matches}
import org.mockito.Mockito.{mock, when}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.MissingBearerToken
import utils.TestSupport

import scala.concurrent.Future


class ObligationsControllerSpec extends TestSupport with MockMicroserviceAuthConnector {

  trait Setup {
    val cc: ControllerComponents = stubControllerComponents()
    val obligationsConnector: ObligationsConnector = mock(classOf[ObligationsConnector])
    val authenticationPredicate: AuthenticationPredicate = new AuthenticationPredicate(mockMicroserviceAuthConnector, cc, microserviceAppConfig)

    val controller = new ObligationsController(
      authenticationPredicate,
      obligationsConnector,
      cc
    )
  }

  "getOpenObligations" should {
    s"return ${Status.OK} with valid report deadlines" in new Setup {
      mockAuth()
      when(obligationsConnector.getOpenObligations(matches(testNino))(any()))
        .thenReturn(Future.successful(testObligations))

      val result: Future[Result] = controller.getOpenObligations(testNino)(FakeRequest())

      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
      contentAsJson(result) shouldBe Json.toJson(testObligations)
    }

    "return the status of the error model when the connector returns one" in new Setup {
      mockAuth()
      when(obligationsConnector.getOpenObligations(matches(testNino))(any()))
        .thenReturn(Future.successful(testReportDeadlinesError))

      val result: Future[Result] = controller.getOpenObligations(testNino)(FakeRequest())

      status(result) shouldBe testReportDeadlinesError.status
      contentType(result) shouldBe Some("application/json")
      contentAsJson(result) shouldBe Json.toJson(testReportDeadlinesError)
    }

    s"return ${Status.UNAUTHORIZED} when called by an unauthorised user" in new Setup {
      mockAuth(Future.failed(new MissingBearerToken))
      val result: Future[Result] = controller.getOpenObligations(testNino)(FakeRequest())

      status(result) shouldBe Status.UNAUTHORIZED
    }
  }

  "getPreviousObligations" should {
    s"return ${Status.OK}" when {
      "valid obligations are returned from the connector" in new Setup {
        mockAuth()
        when(obligationsConnector.getAllObligationsWithinDateRange(matches(testNino), matches("2020-04-06"), matches("2021-04-05"))(any()))
          .thenReturn(Future.successful(testObligations))

        val result: Future[Result] = controller.getAllObligations(nino = testNino, from = "2020-04-06", to = "2021-04-05")(FakeRequest())

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(testObligations)
        contentType(result) shouldBe Some(JSON)
      }
    }
    "return the status of the error model" when {
      "an error model is returned from the connector" in new Setup {
        mockAuth()
        when(obligationsConnector.getAllObligationsWithinDateRange(matches(testNino), matches("2020-04-06"), matches("2021-04-05"))(any()))
          .thenReturn(Future.successful(testReportDeadlinesError))

        val result: Future[Result] = controller.getAllObligations(nino = testNino, from = "2020-04-06", to = "2021-04-05")(FakeRequest())

        status(result) shouldBe testReportDeadlinesError.status
        contentAsJson(result) shouldBe Json.toJson(testReportDeadlinesError)
        contentType(result) shouldBe Some(JSON)
      }
    }
    s"return ${Status.UNAUTHORIZED}" when {
      "called by an unauthenticated user" in new Setup {
        mockAuth(Future.failed(new MissingBearerToken))

        val result: Future[Result] = controller.getAllObligations(nino = testNino, from = "2020-04-06", to = "2021-04-05")(FakeRequest())

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }

}

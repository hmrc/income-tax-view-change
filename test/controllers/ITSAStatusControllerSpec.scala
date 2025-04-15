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

import constants.BaseTestConstants.{testNino, testTaxYearRange}
import constants.ITSAStatusTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockITSAStatusConnector, MockMicroserviceAuthConnector}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, contentType, defaultAwaitTimeout, status, stubControllerComponents}
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class ITSAStatusControllerSpec extends ControllerBaseSpec with MockITSAStatusConnector with MockMicroserviceAuthConnector {

  object TestITSAStatusController extends ITSAStatusController(
    authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig), mockCC,
    connector = mockITSAStatusConnector
  )

  lazy val mockCC: ControllerComponents = stubControllerComponents()

  def fakeGetRequest(): FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  def callGetITSAStatus: Future[Result] = {
    TestITSAStatusController.getITSAStatus(testNino, testTaxYearRange, futureYears = true, history = true)(fakeGetRequest())
  }

  "The ITSAStatusController" should {

    "return a valid ITSA status response" when {

      "called by an authenticated user and ITSAStatusConnector gives a valid response" in {
        mockAuth()
        mockGetITSAStatus(Right(List(successITSAStatusResponseModel)))
        lazy val result = callGetITSAStatus

        contentType(result) shouldBe Some("application/json")
        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(successITSAStatusListResponseJson)

      }

      "called by an authenticated user and ITSAStatusConnector gives an error response" in {
        mockAuth()
        mockGetITSAStatus(Left(errorITSAStatusNotFoundError))
        lazy val result = callGetITSAStatus

        contentType(result) shouldBe Some("application/json")
        status(result) shouldBe errorITSAStatusNotFoundError.status
        contentAsJson(result) shouldBe Json.toJson(errorITSAStatusNotFoundError)
      }

      "called by an authenticated user and ITSAStatusConnector gives an invalid JSON response" in {
        mockAuth()
        mockGetITSAStatus(Left(badJsonErrorITSAStatusError))
        lazy val result = callGetITSAStatus

        contentType(result) shouldBe Some("application/json")
        status(result) shouldBe badJsonErrorITSAStatusError.status
        contentAsJson(result) shouldBe Json.toJson(badJsonErrorITSAStatusError)
      }
    }

    "return an UNAUTHORIZED response" when {

      "called by an unauthenticated user" in {
        mockAuth(Future.failed(new MissingBearerToken))
        lazy val result = callGetITSAStatus

        status(result) shouldBe UNAUTHORIZED
      }
    }
  }
}

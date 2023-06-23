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

import assets.ITSAStatusTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockITSAStatusConnector, MockMicroserviceAuthConnector}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class ITSAStatusControllerSpec extends ControllerBaseSpec with MockITSAStatusConnector with MockMicroserviceAuthConnector {
  def fakeGetRequest(): FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  "The ITSAStatusController" when {
    lazy val mockCC = stubControllerComponents()
    "called with an Authenticated user" when {

      object TestITSAStatusController extends ITSAStatusController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig), mockCC,
        connector = mockITSAStatusConnector
      )

      def callGetITSAStatus = TestITSAStatusController.getITSAStatus("", "", true, true)(fakeGetRequest())

      "ITSAStatusConnector gives a valid response" should {
        mockAuth()
        mockGetITSAStatus(Right(List(successITSAStatusResponseModel)))
        val result = callGetITSAStatus
        checkContentTypeOf(result)("application/json")
        checkStatusOf(result)(OK)
        checkJsonBodyOf(result)(successITSAStatusListResponseJson)
      }

      "ITSAStatusConnector gives a error response" should {
        mockAuth()
        mockGetITSAStatus(Left(errorITSAStatusNotFoundError))
        val result = callGetITSAStatus
        checkContentTypeOf(result)("application/json")
        checkStatusOf(result)(errorITSAStatusNotFoundError.status)
        checkJsonBodyOf(result)(Json.toJson(errorITSAStatusNotFoundError))
      }

      "ITSAStatusConnector gives a invalid json response" should {
        mockAuth()
        mockGetITSAStatus(Left(badJsonErrorITSAStatusError))
        lazy val result = callGetITSAStatus
        checkContentTypeOf(result)("application/json")
        checkStatusOf(result)(badJsonErrorITSAStatusError.status)
        checkJsonBodyOf(result)(badJsonErrorITSAStatusError)
      }

      "called with an Unauthenticated user" should {
        mockAuth(Future.failed(new MissingBearerToken))
        lazy val result = callGetITSAStatus
        checkStatusOf(result)(UNAUTHORIZED)
      }

    }
  }
}

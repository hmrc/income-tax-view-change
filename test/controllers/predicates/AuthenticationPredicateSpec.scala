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

package controllers.predicates

import controllers.ControllerBaseSpec
import mocks.MockMicroserviceAuthConnector
import play.api.http.Status
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.{AffinityGroup, ConfidenceLevel, MissingBearerToken}

import uk.gov.hmrc.auth.core.syntax.retrieved.authSyntaxForRetrieved

import scala.concurrent.Future

class AuthenticationPredicateSpec extends ControllerBaseSpec with MockMicroserviceAuthConnector {

  "The AuthenticationPredicate.authenticated method" should {

    lazy val mockCC = stubControllerComponents()
    object TestAuthenticationPredicate extends AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig)

    def result(authenticationPredicate: AuthenticationPredicate): Future[Result] = authenticationPredicate.async {
      request =>
        Future.successful(Ok)
    }.apply(FakeRequest())

    "called with an Unauthenticated user (No Bearer Token in Header)" when {
      mockAuth(Future.failed(new MissingBearerToken))
      val futureResult = result(TestAuthenticationPredicate)
      whenReady(futureResult){ res =>
        checkStatusOf(res)(Status.UNAUTHORIZED)
      }
    }

    "called with an authenticated user (Some Bearer Token in Header)" when {
      mockAuth()
      val futureRes = result(TestAuthenticationPredicate)
      whenReady(futureRes){ res =>
        checkStatusOf(res)(Status.OK)
      }
    }

    "called with low confidence level" when {
      mockAuth(Future.successful(Some(AffinityGroup.Individual) and ConfidenceLevel.L50))
      val futureResult = result(TestAuthenticationPredicate)
      whenReady(futureResult){  res =>
        checkStatusOf(res)(Status.UNAUTHORIZED)
      }
    }

    "agent called with low confidence level" when {
      mockAuth(Future.successful(Some(AffinityGroup.Agent) and ConfidenceLevel.L50))
      val futureResult = result(TestAuthenticationPredicate)
      whenReady(futureResult){ res =>
        checkStatusOf(res)(Status.OK)
      }
    }
  }
}

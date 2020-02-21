/*
 * Copyright 2020 HM Revenue & Customs
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

import config.MicroserviceAuthConnector
import controllers.ControllerBaseSpec
import mocks.{MockAuthorisedUser, MockUnauthorisedUser, MockAuthorisedFunctions}
import play.api.http.Status
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents

import scala.concurrent.Future

class AuthenticationPredicateSpec extends ControllerBaseSpec {

  "The AuthenticationPredicate.authenticated method" when {

    lazy val mockCC = stubControllerComponents()
    val authConnector: MicroserviceAuthConnector = mock[MicroserviceAuthConnector]

    def result(authenticationPredicate: AuthenticationPredicate): Future[Result] = authenticationPredicate.async {
      implicit request =>
        Future.successful(Ok)
    }.apply(FakeRequest())

    "called with an Unauthenticated user (No Bearer Token in Header)" should {
      object TestUnauthenticationPredicate extends AuthenticationPredicate(authConnector, mockCC)
      checkStatusOf(result(TestUnauthenticationPredicate))(Status.UNAUTHORIZED)
    }

    "called with an authenticated user (Some Bearer Token in Header)" should {
      object TestAuthenticationPredicate extends AuthenticationPredicate(authConnector, mockCC)
      checkStatusOf(result(TestAuthenticationPredicate))(Status.OK)
    }
  }
}

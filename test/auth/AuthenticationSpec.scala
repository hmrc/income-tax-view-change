/*
 * Copyright 2017 HM Revenue & Customs
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

package auth

import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.mvc.Results.Ok
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.Authorization
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


class AuthenticationSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  object TestAuthentication extends Authentication(MockAuthorisedFunctions)

  implicit val hc = HeaderCarrier()
  implicit val authorisedHc = HeaderCarrier(authorization = Some(Authorization("Some Bearer Token")))

  "The Authentication.authenticated method" when {

    "called with an Unauthenticated user (No Bearer Token in Header)" should {
      "return Unauthorised (401)" in {
        val result = TestAuthentication.authenticated(Future.successful(Ok))(hc, implicitly[ExecutionContext])
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "called with an authenticated user (Some Bearer Token in Header)" should {
      "return OK (200)" in {
        val result = TestAuthentication.authenticated(Future.successful(Ok))(authorisedHc, implicitly[ExecutionContext])
        status(result) shouldBe Status.OK
      }
    }
  }


}

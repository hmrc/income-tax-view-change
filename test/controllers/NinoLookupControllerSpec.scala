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

package controllers

import assets.TestConstants.BusinessDetails._
import assets.TestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockAuthorisedUser, MockNinoLookupService, MockUnauthorisedUser}
import play.api.http.Status
import play.api.test.FakeRequest


class NinoLookupControllerSpec extends ControllerBaseSpec with MockNinoLookupService {

  "The NinoLookupController.getNino action" when {

    "called with an Authenticated user" when {

      object TestNinoLookupController extends NinoLookupController(
        authentication = new AuthenticationPredicate(MockAuthorisedUser),
        ninoLookupService = mockNinoLookupService
      )

      "a valid response from the Nino Lookup Service" should {

        mockNinoLookupResponse(testNinoModel)
        lazy val result = TestNinoLookupController.getNino(mtdRef)(FakeRequest())

        checkStatusOf(result)(Status.OK)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(testNinoModel)
      }

      "an invalid response from the Estimated Tax Liability Service" should {

        mockNinoLookupResponse(testDesResponseError)
        lazy val result = TestNinoLookupController.getNino(mtdRef)(FakeRequest())

        checkStatusOf(result)(Status.INTERNAL_SERVER_ERROR)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(testDesResponseError)
      }
    }

    "called with an Unauthenticated user" should {

      object TestNinoLookupController extends NinoLookupController(
        authentication = new AuthenticationPredicate(MockUnauthorisedUser),
        ninoLookupService = mockNinoLookupService
      )

      lazy val result = TestNinoLookupController.getNino(mtdRef)(FakeRequest())

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }
  }
}

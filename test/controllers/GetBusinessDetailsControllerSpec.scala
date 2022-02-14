/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.BaseTestConstants.testNino
import assets.IncomeSourceDetailsTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockGetBusinessDetailsService, MockMicroserviceAuthConnector}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class GetBusinessDetailsControllerSpec extends ControllerBaseSpec with MockGetBusinessDetailsService with MockMicroserviceAuthConnector {

  "The GetBusinessDetailsController" when {
    lazy val mockCC = stubControllerComponents()

    "getBusinessDetails called with an Authenticated user" when {

      object TestGetBusinessDetailsController extends GetBusinessDetailsController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC),
        getBusinessDetailsService = mockGetBusinessDetailsService, mockCC
      )

      "a valid response from the GetBusinessDetailsService" should {

        mockIncomeSourceDetailsResponse(testIncomeSourceDetailsModel)
        mockAuth(Future.successful())
        lazy val result = TestGetBusinessDetailsController.getBusinessDetails(testNino)(FakeRequest())

        checkStatusOf(result)(Status.OK)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(testIncomeSourceDetailsModel)
      }

      "an invalid response from the IncomeSourceDetailsService" should {

        mockIncomeSourceDetailsResponse(testIncomeSourceDetailsError)
        mockAuth(Future.successful())
        lazy val result = TestGetBusinessDetailsController.getBusinessDetails(testNino)(FakeRequest())

        checkStatusOf(result)(Status.INTERNAL_SERVER_ERROR)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(testIncomeSourceDetailsError)
      }
    }

    "called with an Unauthenticated user" should {

      object TestGetBusinessDetailsController extends GetBusinessDetailsController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC),
        getBusinessDetailsService = mockGetBusinessDetailsService, mockCC
      )

      mockAuth(Future.failed(new MissingBearerToken))
      lazy val result = TestGetBusinessDetailsController.getBusinessDetails(testNino)(FakeRequest())

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }
  }
}

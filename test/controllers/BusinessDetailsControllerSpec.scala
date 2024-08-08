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

import assets.BaseTestConstants.testNino
import assets.IncomeSourceDetailsTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockBusinessDetailsService, MockMicroserviceAuthConnector}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class BusinessDetailsControllerSpec extends ControllerBaseSpec with MockBusinessDetailsService with MockMicroserviceAuthConnector {

  "The BusinessDetailsController" when {


    "getBusinessDetails called with an Authenticated user" when {
      val mockCC = stubControllerComponents()
      object TestBusinessDetailsController extends BusinessDetailsController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig),
        businessDetailsService = mockBusinessDetailsService, mockCC
      )

      "a valid response from the BusinessDetailsService" should {
        mockIncomeSourceDetailsResponse(testIncomeSourceDetailsModel)
        mockAuth()
        val futureResult = TestBusinessDetailsController.getBusinessDetails(testNino)(FakeRequest())
        whenReady(futureResult) { result =>
          checkStatusOf(result)(Status.OK)
          checkContentTypeOf(result)("application/json")
          checkJsonBodyOf(result)(testIncomeSourceDetailsModel)
        }
      }

      "an invalid response from the IncomeSourceDetailsService" should {
        mockIncomeSourceDetailsResponse(testIncomeSourceDetailsError)
        mockAuth()
        val futureResult = TestBusinessDetailsController.getBusinessDetails(testNino)(FakeRequest())
        whenReady(futureResult) { result =>
          checkStatusOf(result)(Status.INTERNAL_SERVER_ERROR)
          checkContentTypeOf(result)("application/json")
          checkJsonBodyOf(result)(testIncomeSourceDetailsError)
        }
      }
    }

    "called with an unauthenticated user" should {
      val mockCC = stubControllerComponents()
      object TestBusinessDetailsController extends BusinessDetailsController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig),
        businessDetailsService = mockBusinessDetailsService, mockCC
      )

      mockAuth(Future.failed(new MissingBearerToken))
      val futureResult = TestBusinessDetailsController.getBusinessDetails(testNino)(FakeRequest())
      whenReady(futureResult) { result =>
        checkStatusOf(result)(Status.UNAUTHORIZED)
      }
    }
  }
}

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

import assets.UpdateIncomeSourceTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockMicroserviceAuthConnector, MockUpdateIncomeSourceConnector}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, UNAUTHORIZED}
import play.api.libs.json.Json
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class UpdateIncomeSourceControllerSpec extends ControllerBaseSpec with MockUpdateIncomeSourceConnector with MockMicroserviceAuthConnector {

  "The UpdateIncomeSourceController" when {

    "called with an Authenticated user" when {

      val mockCC = stubControllerComponents()

      object TestUpdateIncomeSourceController extends UpdateIncomeSourceController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig), mockCC,
        connector = mockUpdateIncomeSourceConnector
      )

      "UpdateIncomeSourceConnector gives a valid response" should {
        mockAuth()
        mockUpdateIncomeSource(successResponse, OK)
        val futureResult = TestUpdateIncomeSourceController.updateIncomeSource()(fakeRequestPut(requestJson))
        whenReady(futureResult){ result =>
          checkContentTypeOf(result)("application/json")
          checkStatusOf(result)(OK)
          checkJsonBodyOf(result)(successResponse)
        }
      }

      "UpdateIncomeSourceConnector gives a error response" should {
        mockAuth()
        mockUpdateIncomeSource(failureResponse, INTERNAL_SERVER_ERROR)
        val futureResult = TestUpdateIncomeSourceController.updateIncomeSource()(fakeRequestPut(requestJson))
        whenReady(futureResult) { result =>
          checkContentTypeOf(result)("application/json")
          checkStatusOf(result)(INTERNAL_SERVER_ERROR)
          checkJsonBodyOf(result)(failureResponse)
        }
      }

      "UpdateIncomeSourceConnector gives a invalid json response" should {
        mockAuth()
        mockUpdateIncomeSource(badJsonResponse, INTERNAL_SERVER_ERROR)
        val futureResult = TestUpdateIncomeSourceController.updateIncomeSource()(fakeRequestPut(requestJson))
        whenReady(futureResult) { result =>
          checkContentTypeOf(result)("application/json")
          checkStatusOf(result)(INTERNAL_SERVER_ERROR)
          checkJsonBodyOf(result)(badJsonResponse)
        }
      }

      "invoked with invalid request" should {
        mockAuth()
        val futureResult = TestUpdateIncomeSourceController.updateIncomeSource()(fakeRequestPut(Json.obj()))
        whenReady(futureResult) { result =>
          checkContentTypeOf(result)("application/json")
          checkStatusOf(result)(BAD_REQUEST)
          checkJsonBodyOf(result)(badRequestError)
        }
      }

      "called with an Unauthenticated user" should {
        mockAuth(Future.failed(new MissingBearerToken))
        val futureResult = TestUpdateIncomeSourceController.updateIncomeSource()(fakeRequestPut(requestJson))
        whenReady(futureResult) { result =>
          checkStatusOf(result)(UNAUTHORIZED)
        }
      }

    }
  }
}

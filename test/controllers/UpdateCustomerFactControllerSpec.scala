/*
 * Copyright 2026 HM Revenue & Customs
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

import controllers.predicates.AuthenticationPredicate
import mocks.{MockMicroserviceAuthConnector, MockUpdateCustomerFactConnector}
import models.hip.updateCustomerFact.UpdateCustomerFactSuccess
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.Json
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.{ExecutionContext, Future}

class UpdateCustomerFactControllerSpec
  extends ControllerBaseSpec
    with MockUpdateCustomerFactConnector
    with MockMicroserviceAuthConnector {

  "The UpdateCustomerFactController" when {

    "called with an Authenticated user" should {

      val mockCC = stubControllerComponents()
      implicit val ec: ExecutionContext = mockCC.executionContext

      object TestUpdateCustomerFactController extends UpdateCustomerFactController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig),
        cc = mockCC,
        connector = mockUpdateCustomerFactConnector
      )

      mockAuth()
      mockUpdateCustomerFactsToConfirmed("testMtdId", UpdateCustomerFactSuccess(Json.obj("processingDateTime" -> "2022-01-31T09:26:17Z"), "123"))

      val futureResult = TestUpdateCustomerFactController.updateKnownFacts("testMtdId")(fakeRequest)

      whenReady(futureResult) { result =>
        checkStatusOf(result)(OK)
        checkJsonBodyOf(result)(
          Json.parse("""{"processingDateTime":"2022-01-31T09:26:17Z"}""")
        )
      }
    }

    "called with an Unauthenticated user" should {

      val mockCC = stubControllerComponents()

      object TestUpdateCustomerFactController extends UpdateCustomerFactController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig),
        cc = mockCC,
        connector = mockUpdateCustomerFactConnector
      )

      mockAuth(Future.failed(new MissingBearerToken))

      val futureResult = TestUpdateCustomerFactController.updateKnownFacts("testMtdId")(fakeRequest)

      whenReady(futureResult) { result =>
        checkStatusOf(result)(UNAUTHORIZED)
      }
    }
  }
}

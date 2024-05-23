/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.ClaimToAdjustPoaConnector
import controllers.predicates.AuthenticationPredicate
import mocks.MockMicroserviceAuthConnector
import models.claimToAdjustPoa.ClaimToAdjustPoaApiResponse.SuccessResponse
import models.claimToAdjustPoa.ClaimToAdjustPoaResponse.{ClaimToAdjustPoaResponse, ErrorResponse}
import models.claimToAdjustPoa.{ClaimToAdjustPoaRequest, MainIncomeLower}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status.{BAD_REQUEST, CREATED, INTERNAL_SERVER_ERROR}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}

import scala.concurrent.Future

class ClaimToAdjustPoaControllerSpec extends ControllerBaseSpec with MockMicroserviceAuthConnector {

  val mockCC: ControllerComponents = stubControllerComponents()
  val authPredicate = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig)
  val mockConnector = mock[ClaimToAdjustPoaConnector]

  object TestClaimToAdjustPoaController extends ClaimToAdjustPoaController(authPredicate,mockCC, mockConnector)

  "ClaimToAdjustPoaConnector.submitClaimToAdjustPoa" should {
    s"return $INTERNAL_SERVER_ERROR" when {
      "user is authenticated and ClaimToAdjustPoaConnector returns an error response" in {
        mockAuth()
        when(mockConnector.postClaimToAdjustPoa(any())(any()))
          .thenReturn(Future.successful(ClaimToAdjustPoaResponse(INTERNAL_SERVER_ERROR, Left(ErrorResponse("Error message")))))

        val result = TestClaimToAdjustPoaController.submitClaimToAdjustPoa()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              ClaimToAdjustPoaRequest(
                "AA000000A",
                "2024",
                1000.0,
                MainIncomeLower
              )
            )
          )
        )

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.obj("message" -> "Error message")
      }
    }

    s"return $BAD_REQUEST" when {
      "an invalid json body is sent" in {
        mockAuth()
        when(mockConnector.postClaimToAdjustPoa(any())(any()))
          .thenReturn(Future.successful(ClaimToAdjustPoaResponse(BAD_REQUEST, Left(ErrorResponse("INVALID_PAYLOAD")))))

        val result = TestClaimToAdjustPoaController.submitClaimToAdjustPoa()(
          fakePostRequest.withJsonBody(
            Json.toJson(Json.obj()
            )
          )
        )

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.obj("message" -> "Could not validate request")
      }
    }

    s"return $CREATED" when {
      "a ClaimToAdjustPoa model as json body is sent" in {
        mockAuth()

        when(mockConnector.postClaimToAdjustPoa(any())(any()))
          .thenReturn(Future.successful(
            ClaimToAdjustPoaResponse(
              status = CREATED,
              claimToAdjustPoaBody = Right(SuccessResponse("2024-01-31T09:27:17Z")))))

        val result = TestClaimToAdjustPoaController.submitClaimToAdjustPoa()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              ClaimToAdjustPoaRequest(
                "AA000000A",
                "2024",
                1000.0,
                MainIncomeLower
              )
            )
          )
        )

        status(result) shouldBe CREATED
        contentAsJson(result) shouldBe
          Json.toJson(
            Json.obj("processingDate" -> "2024-01-31T09:27:17Z")
          )
      }
    }
  }
}

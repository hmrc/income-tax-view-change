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

package controllers

import assets.PreviousCalculationTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockCalculationService, MockMicroserviceAuthConnector}
import models.PreviousCalculation._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class PreviousCalculationControllerSpec extends ControllerBaseSpec with MockCalculationService with MockMicroserviceAuthConnector {

  val successResponse: Either[Nothing, PreviousCalculationModel] = Right(previousCalculationFull)

  "The GET PreviousCalculationController.getPreviousCalculation method" when {
    lazy val mockCC = stubControllerComponents()
    "called by an authenticated user" which {

      object PreviousCalculationController extends PreviousCalculationController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC),
        mockCalculationService, mockCC
      )

      "is requesting previous calculations details" should {

        "for a successful response from the CalculationService," should {
          lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino,
            testYear)(fakeRequest))


          "return a status of 200 (OK)" in {
            mockAuth(Future.successful())
            setupMockGetPreviousCalculation(testNino,
              testYear)(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the transformed des return data" in {
            jsonBodyOf(result) shouldBe responseJsonFullWithoutExtra
          }
        }

        "for a bad request with single error from the CalculationService," should {
          lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino,
            testYear)(fakeRequest))


          "return a status of 400 (BAD_REQUEST)" in {
            mockAuth(Future.successful())
            setupMockGetPreviousCalculation(testNino,
              testYear)(badRequestSingleError)

            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            jsonBodyOf(result) shouldBe Json.toJson(singleError)
          }
        }

        "for an invalid nino " should {
          lazy val result: Result =
            await(PreviousCalculationController.getPreviousCalculation(badNino,
              testYear)(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            mockAuth(Future.successful())
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the invalid nino error message" in {
            jsonBodyOf(result) shouldBe Json.toJson(InvalidNino)
          }
        }

        "for a bad request with multiple errors from the CalculationService," should {
          lazy val result: Result =
            await(PreviousCalculationController.getPreviousCalculation(testNino,
              testYear)(fakeRequest))


          "return a status of 400 (BAD_REQUEST)" in {
            mockAuth(Future.successful())
            setupMockGetPreviousCalculation(testNino,
              testYear)(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            jsonBodyOf(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "for a bad request with single error from the CalculationService," should {
        lazy val result: Result =
          await(PreviousCalculationController.getPreviousCalculation(testNino,
            testYear)(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          mockAuth(Future.successful())
          setupMockGetPreviousCalculation(testNino,
            testYear)(badRequestSingleError)

          status(result) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the single error message" in {

          jsonBodyOf(result) shouldBe Json.toJson(singleError)
        }
      }
    }

    "called with an Unauthenticated user" should {

      object PreviousCalculationController extends PreviousCalculationController(
        authentication = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC),
        calculationService = mockCalculationService, mockCC
      )
      mockAuth(Future.failed(new MissingBearerToken))
      lazy val result = PreviousCalculationController.getPreviousCalculation(testNino,
        testYear)(fakeRequest)


      checkStatusOf(result)(Status.UNAUTHORIZED)
    }

  }
}

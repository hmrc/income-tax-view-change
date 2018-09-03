/*
 * Copyright 2018 HM Revenue & Customs
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
import mocks.{MockAuthorisedUser, MockCalculationService, MockUnauthorisedUser}
import models.PreviousCalculation._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import assets.PreviousCalculationTestConstants._

class PreviousCalculationControllerSpec extends ControllerBaseSpec with MockCalculationService {

  val successResponse: Either[Nothing, PreviousCalculationModel] = Right(previousCalculationFull)

  "The GET PreviousCalculationController.getPreviousCalculation method" when {

    "called by an authenticated user" which {

      object PreviousCalculationController extends PreviousCalculationController(
        authentication = new AuthenticationPredicate(MockAuthorisedUser),
        mockCalculationService
      )

      "is requesting previous calculations details" should {

        "for a successful response from the CalculationService," should {

          lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino,
            testYear)(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetPreviousCalculation(testNino,
              testYear)(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the transformed des return data" in {
            jsonBodyOf(result) shouldBe responseJsonFull
          }
        }

        "for a bad request with single error from the CalculationService," should {

          lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino,
            testYear)(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
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
        authentication = new AuthenticationPredicate(MockUnauthorisedUser),
        calculationService = mockCalculationService
      )

      lazy val result = PreviousCalculationController.getPreviousCalculation(testNino,
        testYear)(fakeRequest)

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }

  }
}

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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest

class PreviousCalculationControllerSpec extends ControllerBaseSpec with MockCalculationService {

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val success: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = Some(22.56), calcTimestamp = Some("2008-05-01"), crystallised = Some(true),
        calcResult = Some(CalcResult(500.68, Some(EoyEstimate(125.63))))))

  val singleError = Error(code = "CODE", reason = "ERROR MESSAGE")
  val multiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2" +
        "", reason = "ERROR MESSAGE 2")
    )
  )

  val transformedJson: JsValue = Json.parse(
    """
      |{"calcOutput":{
      |"calcID":"1",
      |"calcAmount":22.56,
      |"calcTimestamp":"2008-05-01",
      |"crystallised":true,
      |"calcResult":{"incomeTaxNicYtd":500.68,"eoyEstimate":{"incomeTaxNicAmount":125.63}}}}
    """.stripMargin)

  val successResponse: Either[Nothing, PreviousCalculationModel] = Right(success)
  val badRequestSingleError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, singleError))
  val badRequestMultiError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, multiError))
  val testNino: String = "AA000000B"
  val testYear: String = "2018"
  val badNino: String = "55F"

  "The GET PreviousCalculationController.getPreviousCalculation method" when {

    "called by an authenticated user" which {

      object PreviousCalculationController extends PreviousCalculationController(
        authentication = new AuthenticationPredicate(MockAuthorisedUser),
        mockCalculationService
      )

      "is requesting VAT details" should {

        "for a successful response from the CalculationService," should {

          lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino, testYear)(fakeRequest))

          "return a status of 200 (OK)" in {
            setupMockGetPreviousCalculation(testNino, testYear)(successResponse)
            status(result) shouldBe Status.OK
          }

          "return a json body with the transformed des return data" in {
            jsonBodyOf(result) shouldBe transformedJson
          }
        }

        "for a bad request with single error from the CalculationService," should {

          lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino, testYear)(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetPreviousCalculation(testNino, testYear)(badRequestSingleError)

            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the single error message" in {

            jsonBodyOf(result) shouldBe Json.toJson(singleError)
          }
        }

        "for an invalid nino " should {

          lazy val result: Result =
            await(PreviousCalculationController.getPreviousCalculation(badNino, testYear)(fakeRequest))
          "return a status of 400 (BAD_REQUEST)" in {
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the invalid nino error message" in {
            jsonBodyOf(result) shouldBe Json.toJson(InvalidNino)
          }
        }

        "for a bad request with multiple errors from the CalculationService," should {

          lazy val result: Result =
            await(PreviousCalculationController.getPreviousCalculation(testNino, testYear)(fakeRequest))

          "return a status of 400 (BAD_REQUEST)" in {
            setupMockGetPreviousCalculation(testNino, testYear)(badRequestMultiError)
            status(result) shouldBe Status.BAD_REQUEST
          }

          "return a json body with the multiple error messages" in {
            jsonBodyOf(result) shouldBe Json.toJson(multiError)
          }
        }

      }

      "for a bad request with single error from the CalculationService," should {
        lazy val result: Result =
          await(PreviousCalculationController.getPreviousCalculation(testNino, testYear)(fakeRequest))
        "return a status of 400 (BAD_REQUEST)" in {
          setupMockGetPreviousCalculation(testNino, testYear)(badRequestSingleError)

          status(result) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the single error message" in {

          jsonBodyOf(result) shouldBe Json.toJson(singleError)
        }
      }

      "for a bad request with multiple errors from the CalculationService," should {

        lazy val result: Result = await(PreviousCalculationController.getPreviousCalculation(testNino, testYear)(fakeRequest))

        "return a status of 400 (BAD_REQUEST)" in {
          setupMockGetPreviousCalculation(testNino, testYear)(badRequestMultiError)

          status(result) shouldBe Status.BAD_REQUEST
        }

        "return a json body with the multiple error messages" in {

          jsonBodyOf(result) shouldBe Json.toJson(multiError)
        }
      }
    }

    "called with an Unauthenticated user" should {

      object PreviousCalculationController extends PreviousCalculationController(
        authentication = new AuthenticationPredicate(MockUnauthorisedUser),
        calculationService = mockCalculationService
      )

      lazy val result = PreviousCalculationController.getPreviousCalculation(testNino, testYear)(fakeRequest)

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }

  }
}

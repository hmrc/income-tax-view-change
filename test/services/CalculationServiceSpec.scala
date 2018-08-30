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

package services

import mocks.MockCalculationConnector
import models.PreviousCalculation._
import play.api.http.Status
import utils.TestSupport

class CalculationServiceSpec extends TestSupport with MockCalculationConnector {

  object TestCalculationService extends CalculationService(mockCalculationConnector)

  val testNino = "555555555"
  val testYear = "2018"

  "The VatReturnService.getVatReturns method" should {

    "Return a VatReturn when a success response is returned from the Connector" in {
      val testPreviousCalculation: PreviousCalculationModel =
        PreviousCalculationModel(
          CalcOutput(calcID = "1", calcAmount = Some(22.56), calcTimestamp = Some("2008-05-01"), crystallised = Some(true),
            calcResult = Some(CalcResult(500.68, Some(EoyEstimate(125.63))))))

      val successResponse: Either[Nothing, PreviousCalculationModel] = Right(testPreviousCalculation)


      setupMockGetPreviousCalculation(testNino, testYear)(successResponse)

      val actual: Either[ErrorResponse, PreviousCalculationModel] = await(TestCalculationService.getPreviousCalculation(
        testNino,
        testYear
      ))

      actual shouldBe successResponse
    }

    "Return Error when a single error is returned from the Connector" in {

      val singleErrorResponse: Either[ErrorResponse, Nothing] =
        Left(ErrorResponse(Status.BAD_REQUEST, Error("CODE", "MESSAGE")))

      setupMockGetPreviousCalculation(testNino, testYear)(singleErrorResponse)

      val actual: Either[ErrorResponse, PreviousCalculationModel] = await(TestCalculationService.getPreviousCalculation(
        testNino,
        testYear
      ))

      actual shouldBe singleErrorResponse
    }

    "Return a MultiError when multiple error responses are returned from the Connector" in {

      val multiErrorResponse: Either[ErrorResponse, Nothing] = Left(ErrorResponse(Status.BAD_REQUEST, MultiError(Seq(
        Error("CODE 1", "MESSAGE 1"),
        Error("CODE 2", "MESSAGE 2")
      ))))

      setupMockGetPreviousCalculation(testNino, testYear)(multiErrorResponse)

      val actual: Either[ErrorResponse, PreviousCalculationModel] = await(TestCalculationService.getPreviousCalculation(
        testNino,
        testYear
      ))

      actual shouldBe multiErrorResponse
    }
  }
}
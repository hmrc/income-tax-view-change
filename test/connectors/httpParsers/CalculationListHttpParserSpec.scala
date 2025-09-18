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

package connectors.httpParsers

import constants.CalculationListDesTestConstants.{badRequestSingleError, calculationListFull, calculationListMin, hipNotFoundErrorResponse, jsonResponseFull, jsonResponseMin, jsonSingleError}
import connectors.httpParsers.CalculationListHttpParser.CalculationListReads
import models.calculationList.CalculationListResponseModel
import models.errors.{Error, ErrorResponse, InvalidJsonResponse, UnexpectedResponse}
import play.api.http.Status
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListHttpParserSpec extends TestSupport {
  "The CalculationListLegacyHttpParser" should {
    "return an instance of CalculationList" when {
      "HTTP response is 200 and matches schema" in {
        val httpResponse: HttpResponse = HttpResponse(Status.OK, jsonResponseFull, Map.empty)

        val expected: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
      "HTTP response is 200 and matches schema (minimal response)" in {
        val httpResponse: HttpResponse = HttpResponse(Status.OK, jsonResponseMin, Map.empty)

        val expected: Either[Nothing, CalculationListResponseModel] = Right(calculationListMin)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
    }
    "return an error" when {
      "HTTP response is 400 BAD_REQUEST" in {
        val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, jsonSingleError, Map.empty)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual badRequestSingleError
      }
      "HTTP response is 500 INTERNAL_SERVER_ERROR" in {
        val httpResponse: HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, jsonSingleError, Map.empty)
        val expected: Either[UnexpectedResponse.type, Nothing] = Left(UnexpectedResponse)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
      "HTTP response is 404 NOT_FOUND" in {
        val httpResponse: HttpResponse = HttpResponse(Status.NOT_FOUND, hipNotFoundErrorResponse, Map.empty)
        val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(NOT_FOUND, Error("NOT_FOUND", "Resource not found")))
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
      "HTTP response has unexpected status" in {
        val httpResponse: HttpResponse = HttpResponse(Status.SEE_OTHER, "")
        val expected: Either[UnexpectedResponse.type, Nothing] = Left(UnexpectedResponse)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
      "JSON response is invalid" in {
        val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, "this isn't json", Map.empty)
        val expected: Either[InvalidJsonResponse.type, Nothing] = Left(InvalidJsonResponse)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
    }
  }
}

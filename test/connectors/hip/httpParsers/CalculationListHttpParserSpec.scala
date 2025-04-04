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

package connectors.hip.httpParsers

import assets.CalculationListTestConstants._
import connectors.hip.httpParsers.CalculationListHttpParser.CalculationListReads
import models.calculationList.CalculationListResponseModel
import models.hipErrors.{ErrorResponse, UnexpectedJsonResponse}
import play.api.http.Status
import play.api.http.Status.{BAD_GATEWAY, BAD_REQUEST, NOT_FOUND, UNAUTHORIZED}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListHttpParserSpec extends TestSupport {
  "The CalculationListHttpParser" should {
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
        val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, badRequestErrorResponse, Map.empty)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(BAD_REQUEST, badRequestErrorResponse))
      }
      "HTTP response is 400 BAD_REQUEST with errorCode and description" in {
        val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, badRequestErrorResponse2, Map.empty)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(BAD_REQUEST, badRequestErrorResponse2))
      }
      "HTTP response is 400 BAD_REQUEST unexpected response" in {
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", badRequestUnexpectedJson)

        result shouldEqual Left(UnexpectedJsonResponse)
      }
      "HTTP response is 401 UNAUTHORIZED" in {
        val httpResponse: HttpResponse = HttpResponse(UNAUTHORIZED, unAuthorizedErrorResponse, Map.empty)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(UNAUTHORIZED, unAuthorizedErrorResponse))
      }
      "HTTP response is 401 UNAUTHORIZED unexpected response" in {
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", unauthorizedUnexpectedJson)

        result shouldEqual Left(UnexpectedJsonResponse)
      }
      "HTTP response is 404 NOT_FOUND" in {
        val httpResponse: HttpResponse = HttpResponse(NOT_FOUND, notFoundErrorResponse, Map.empty)
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(NOT_FOUND, notFoundErrorResponse))
      }
      "HTTP response is 404 NOT_FOUND unexpected response" in {
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", notFoundUnexpectedJson)

        result shouldEqual Left(UnexpectedJsonResponse)
      }
      "HTTP response is 502 BAD_GATEWAY" in {
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", badGatewayJson)

        result shouldEqual Left(UnexpectedJsonResponse)
      }
      "HTTP response is 404 NOT_FOUND unexpected response" in {
        val result: CalculationListHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", notFoundUnexpectedJson)

        result shouldEqual Left(UnexpectedJsonResponse)
      }
    }
  }
}

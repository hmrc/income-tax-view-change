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
import connectors.hip.httpParsers.CalculationListLegacyHttpParser.CalculationListReads
import models.calculationList.CalculationListResponseModel
import models.hipErrors.{BadGatewayResponse, CustomResponse, ErrorResponse, UnexpectedJsonResponse, UnexpectedResponse}
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, SERVICE_UNAVAILABLE, UNAUTHORIZED}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CalculationListLegacyHttpParserSpec extends TestSupport {
  "The CalculationListLegacyHttpParser" should {
    "return an instance of CalculationList" when {
      "HTTP response is 200 and matches schema" in {
        val httpResponse: HttpResponse = HttpResponse(Status.OK, jsonResponseFull, Map.empty)

        val expected: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
      "HTTP response is 200 and matches schema (minimal response)" in {
        val httpResponse: HttpResponse = HttpResponse(Status.OK, jsonResponseMin, Map.empty)

        val expected: Either[Nothing, CalculationListResponseModel] = Right(calculationListMin)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual expected
      }
    }
    "return an error" when {
      "HTTP response is 400 BAD_REQUEST" in {
        val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, badRequestErrorResponse, Map.empty)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(BAD_REQUEST, badRequestErrorResponse))
      }
      "HTTP response is 400 BAD_REQUEST with errorCode and description" in {
        val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, badRequestErrorResponse2, Map.empty)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(BAD_REQUEST, badRequestErrorResponse2))
      }
      "HTTP response is 400 BAD_REQUEST unexpected response" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", badRequestUnexpectedJson)

        result shouldEqual Left(UnexpectedJsonResponse)
      }
      "HTTP response is 401 UNAUTHORIZED" in {
        val httpResponse: HttpResponse = HttpResponse(UNAUTHORIZED, unAuthorizedErrorResponse, Map.empty)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(UNAUTHORIZED, unAuthorizedErrorResponse))
      }
      "HTTP response is 401 UNAUTHORIZED unexpected response" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", unauthorizedUnexpectedJson)

        result shouldEqual Left(ErrorResponse(UNAUTHORIZED, Json.toJson(CustomResponse("Unexpected Unauthorized or Not found error"))))
      }
      "HTTP response is 404 NOT_FOUND" in {
        val httpResponse: HttpResponse = HttpResponse(NOT_FOUND, notFoundErrorResponse, Map.empty)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(NOT_FOUND, notFoundErrorResponse))
      }
      "HTTP response is 404 NOT_FOUND unexpected response" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", notFoundUnexpectedJson)

        result shouldEqual Left(ErrorResponse(NOT_FOUND, Json.toJson(CustomResponse("Unexpected Unauthorized or Not found error"))))
      }
      "HTTP response is 502 BAD_GATEWAY" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", badGatewayJson)

        result shouldEqual Left(BadGatewayResponse)
      }
      "HTTP response is 500 INTERNAL_SERVER_ERROR" in {
        val httpResponse: HttpResponse = HttpResponse(INTERNAL_SERVER_ERROR, internalServerErrorResponse, Map.empty)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(INTERNAL_SERVER_ERROR, internalServerErrorResponse))
      }
      "HTTP response is 500 INTERNAL_SERVER_ERROR unexpected response" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", badResponse)

        result shouldEqual Left(UnexpectedJsonResponse)
      }

      "HTTP response is 503 SERVICE_UNAVAILABLE" in {
        val httpResponse: HttpResponse = HttpResponse(SERVICE_UNAVAILABLE, internalServerErrorResponse, Map.empty)
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", httpResponse)

        result shouldEqual Left(ErrorResponse(SERVICE_UNAVAILABLE, internalServerErrorResponse))
      }
      "HTTP response is 503 SERVICE_UNAVAILABLE unexpected response" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", serviceUnavailableResponse)

        result shouldEqual Left(UnexpectedJsonResponse)
      }

      "HTTP response with non-json response" in {
        val result: CalculationListLegacyHttpParser.HttpGetResult[CalculationListResponseModel] = CalculationListReads.read("", "", nonJsonResponse)

        result shouldEqual Left(UnexpectedResponse)
      }
    }
  }
}

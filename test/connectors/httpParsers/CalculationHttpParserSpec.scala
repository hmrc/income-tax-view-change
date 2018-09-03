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

package connectors.httpParsers

import utils.TestSupport
import connectors.httpParsers.CalculationHttpParser.PreviousCalculationReads
import models.PreviousCalculation._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import assets.PreviousCalculationTestConstants._

class CalculationHttpParserSpec extends TestSupport {

  "The CalculationHttpParser" when {

    "the http response status is 200 OK and matches expected Schema when fully populated" should {

      val httpResponse: HttpResponse = HttpResponse(Status.OK, responseJson = Some(
        responseJsonFull
      ))

      val expected: Either[Nothing, PreviousCalculationModel] = Right(previousCalculationFull)
      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return a PreviousCalculationModel instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK and matches expected Schema with minimal data" should {

      val httpResponse: HttpResponse = HttpResponse(Status.OK, responseJson = Some(
        responseJsonMinimum
      ))

      val expected: Either[Nothing, PreviousCalculationModel] = Right(previousCalculationMinimum)
      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return a PreviousCalculationModel instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK and matches expected Schema with no eoyData data" should {

      val httpResponse: HttpResponse = HttpResponse(Status.OK, responseJson = Some(
        responseJsonNoEoy
      ))

      val expected: Either[Nothing, PreviousCalculationModel] = Right(testPreviousCalculationNoEoy)
      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return a PreviousCalculationModel instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK but the response is not as expected" should {

      val httpResponse: HttpResponse = HttpResponse(Status.OK, responseJson = Some(Json.obj("invalid" -> "data")))

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(jsonSingleError)
      )

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] =
        PreviousCalculationReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual badRequestSingleError
      }
    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(jsonMultipleErrors)
      )

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return a MultiError" in {
        result shouldEqual badRequestMultiError
      }

    }

    "the http response status is 400 BAD_REQUEST (Unexpected Json Returned)" should {

      val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(Json.obj("foo" -> "bar")))

      val expected: Either[UnexpectedJsonFormat.type, Nothing] = Left(UnexpectedJsonFormat)

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the json is in an invalid format" should {

      val httpResponse: HttpResponse = HttpResponse(Status.BAD_REQUEST, responseString = Some("Banana"))

      val expected: Either[InvalidJsonResponse.type, Nothing] = Left(InvalidJsonResponse)

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return an UnexpectedJsonFormat instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 500 Internal Server Error" should {

      val httpResponse: HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR,
        responseJson = Some(jsonSingleError)
      )

      val expected: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
        Status.INTERNAL_SERVER_ERROR,
        singleError
      ))

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is unexpected" should {

      val httpResponse: HttpResponse = HttpResponse(Status.SEE_OTHER)

      val expected: Either[UnexpectedResponse.type, Nothing] = Left(UnexpectedResponse)

      val result: CalculationHttpParser.HttpGetResult[PreviousCalculationModel] = PreviousCalculationReads.read("", "", httpResponse)

      "return a Error instance" in {
        result shouldEqual expected
      }
    }
  }
}

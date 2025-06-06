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

import connectors.hip.httpParsers.ChargeHipHttpParser.{ChargeHipReads, ChargeHipResponse}
import connectors.httpParsers.ChargeHttpParser.{UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import constants.FinancialDataTestConstants.{testChargeHipResponse, validHipChargesJson}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class ChargeHipHttpParserSpec extends TestSupport {

  "ChargesHttpParser" should {
    "return a charge" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(OK, json = Json.toJson(validHipChargesJson), headers = Map.empty)
        val expectedResult: ChargeHipResponse = Right(testChargeHipResponse)
        val actualResult: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }

    s"return $UnexpectedChargeResponse" when {

      def jsonBodyETMPError(errorCode: String): String = Json.parse(
        s"""
          |{
          | "errors" : {
          |   "code": "$errorCode",
          |   "text" : "error code message",
          |   "processingDate" : "fakeTimestamp"
          | }
          |}
          |""".stripMargin).toString()

      val jsonBodySAPError: String = Json.parse(
        """
          |{
          | "error" : {
          |   "code": "500",
          |   "message" : "error code message",
          |   "logId" : "fakeLog"
          | }
          |}
          |""".stripMargin
      ).toString()

      "a 422 status is returned with a resource not found error code" in {
        val httpResponse: HttpResponse = HttpResponse(UNPROCESSABLE_ENTITY, body = jsonBodyETMPError("005"))
        val expectedResult: ChargeHipResponse = Left(UnexpectedChargeResponse(NOT_FOUND, jsonBodyETMPError("005")))
        val actualResult: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }

      "a 422 status is returned without a resource not found error code" in {
        val httpResponse: HttpResponse = HttpResponse(UNPROCESSABLE_ENTITY, body = jsonBodyETMPError("006"))
        val expectedResult: ChargeHipResponse = Left(UnexpectedChargeResponse(UNPROCESSABLE_ENTITY, jsonBodyETMPError("006")))
        val actualResult: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }

      "a 422 status is returned with the other error json body" in {


        val httpResponse: HttpResponse = HttpResponse(UNPROCESSABLE_ENTITY, body = jsonBodySAPError)
        val expectedResult: ChargeHipResponse = Left(UnexpectedChargeResponse(UNPROCESSABLE_ENTITY, jsonBodySAPError))
        val actualResult: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }

      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST, body = "Bad request")
        val expectedResult: ChargeHipResponse = Left(UnexpectedChargeResponse(BAD_REQUEST, "Bad request"))
        val actualResult: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedChargeErrorResponse" when {
      s"$OK is returned without mandatory fields in json" in {
        val httpResponse: HttpResponse = HttpResponse(OK, body = "{}")

        val expectedResponse: ChargeHipResponse = Left(UnexpectedChargeErrorResponse)
        val actualResponse: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResponse shouldBe expectedResponse
      }
      "a non 200 or 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(INTERNAL_SERVER_ERROR, body = "{}")

        val expectedResult: ChargeHipResponse = Left(UnexpectedChargeErrorResponse)
        val actualResult: ChargeHipResponse = ChargeHipReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }
}

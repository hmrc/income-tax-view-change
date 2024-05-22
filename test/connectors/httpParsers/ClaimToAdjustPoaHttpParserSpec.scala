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

package connectors.httpParsers

import connectors.httpParsers.ClaimToAdjustPoaHttpParser.ClaimToAdjustPoaResponseReads
import models.claimToAdjustPoa.ClaimToAdjustPoaApiResponse.SuccessResponse
import models.claimToAdjustPoa.ClaimToAdjustPoaResponse.{ClaimToAdjustPoaResponse, ErrorResponse}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, CREATED, INTERNAL_SERVER_ERROR}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

class ClaimToAdjustPoaHttpParserSpec extends AnyWordSpec with Matchers {

  val testHttpVerb = "POST"
  val testUri = "/test"

  val testValidRequest: JsObject = Json.obj(
    "successResponse" -> Json.obj(
      "processingDate" -> "2024-01-31T09:27:17Z"
    ))

  val testInvalidRequest: JsObject = Json.obj(
    "successResponse" -> "Success"
  )

  val testFailureJson: JsObject = Json.obj(
    "failures" -> Json.arr(Json.obj(
      "code" -> "INVALID_PAYLOAD",
      "reason" -> "Submission has not passed validation. Invalid payload."
    )
    ))

  val testInvalidFailureJson: JsObject = Json.obj(
    "failures" -> Json.obj(
      "code" -> "INVALID_PAYLOAD",
      "reason" -> "Submission has not passed validation. Invalid payload."
    )
  )

  "ClaimToAdjustPoaResponseReads" when {
    "read" should {
      "parse CREATED status and valid json data" when {
        "json is valid" in {
          val httpResponse = HttpResponse(CREATED, json = testValidRequest, headers = Map.empty)
          lazy val result = ClaimToAdjustPoaResponseReads.read(testHttpVerb, testUri, httpResponse)
          result shouldBe ClaimToAdjustPoaResponse(CREATED, Right(SuccessResponse("2024-01-31T09:27:17Z")))
        }
        "json is invalid" in {
          val httpResponse = HttpResponse(CREATED, json = testInvalidRequest, headers = Map.empty)
          lazy val result = ClaimToAdjustPoaResponseReads.read(testHttpVerb, testUri, httpResponse)
          result shouldBe ClaimToAdjustPoaResponse(INTERNAL_SERVER_ERROR,
            Left(ErrorResponse("Unable to parse the content of a success response from Claim To Adjust POA")))
        }
      }

      "parse error status and valid json data" when {
        "json is valid" in {
          val httpResponse = HttpResponse(BAD_REQUEST, json = testFailureJson, headers = Map.empty)
          lazy val result = ClaimToAdjustPoaResponseReads.read(testHttpVerb, testUri, httpResponse)
          result shouldBe ClaimToAdjustPoaResponse(BAD_REQUEST, Left(ErrorResponse("INVALID_PAYLOAD")))
        }

        "json is invalid" in {
          val httpResponse = HttpResponse(BAD_REQUEST, json = testInvalidFailureJson, headers = Map.empty)
          lazy val result = ClaimToAdjustPoaResponseReads.read(testHttpVerb, testUri, httpResponse)
          result shouldBe ClaimToAdjustPoaResponse(INTERNAL_SERVER_ERROR,
            Left(ErrorResponse("Unable to parse the content of a failure response from Claim To Adjust POA")))
        }
      }
    }
  }
}

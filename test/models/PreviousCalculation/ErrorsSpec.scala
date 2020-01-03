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

package models.PreviousCalculation

import utils.TestSupport
import play.api.http.Status
import play.api.libs.json.Json
import assets.PreviousCalculationTestConstants._

class ErrorsSpec extends TestSupport {

  "The Error model" should {

    "Serialize to Json as expected" in {
      Json.toJson(singleError) shouldBe jsonSingleError
    }

    "Deserialize to a Error as expected" in {
      jsonSingleError.as[Error] shouldBe singleError
    }
  }

  "The MultiError model" should {

    "Serialize to Json as expected" in {
      Json.toJson(multiError) shouldBe jsonMultipleErrors
    }

    "Deserialize to a MultiError as expected" in {
      jsonMultipleErrors.as[MultiError] shouldBe multiError
    }
  }

  "The UnexpectedResponse object" should {

    "Have the error status Internal Server Error (500)'" in {
      UnexpectedResponse.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Have the error message 'The downstream service responded with an unexpected response.'" in {
      UnexpectedResponse.error shouldBe Error(
        code = "UNEXPECTED_DOWNSTREAM_ERROR",
        reason = "The downstream service responded with an unexpected response.")
    }
  }

  "The InvalidJsonResponse object" should {

    "Have the error status Internal Server Error (500)'" in {
      InvalidJsonResponse.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Have the error message 'The response did not contain valid json.'" in {
      InvalidJsonResponse.error shouldBe Error(
        code = "INVALID_JSON",
        reason = "The downstream service responded with invalid json.")
    }

  }

  "The UnexpectedJsonFormat object" should {

    "Have the error status Internal Server Error (500)'" in {
      UnexpectedJsonFormat.status shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "Have the error message 'The DES response did not match the expected format'" in {
      UnexpectedJsonFormat.error shouldBe Error(
        code = "UNEXPECTED_JSON_FORMAT",
        reason = "The downstream service responded with json which did not match the expected format.")
    }
  }

  "The InvalidNino object" should {

    "Have the error status BAD_REQUEST (400)'" in {
      InvalidNino.code shouldBe "ERROR_NINO_INVALID"
    }

    "Have the error message 'The supplied NINO is invalid.'" in {
      InvalidNino.reason shouldBe "The supplied NINO is invalid."
    }
  }

  "The UnauthenticatedError object" should {

    "Have the error status UNAUTHORIZED (401)'" in {
      UnauthenticatedError.code shouldBe "UNAUTHENTICATED"
    }

    "Have the error message 'Not authenticated'" in {
      UnauthenticatedError.reason shouldBe "Not authenticated"
    }
  }

  "The ForbiddenError object" should {

    "Have the error status FORBIDDEN (403)'" in {
      ForbiddenError.code shouldBe "UNAUTHORISED"
    }

    "Have the error message 'Not authorised'" in {
      ForbiddenError.reason shouldBe "Not authorised"
    }
  }
}

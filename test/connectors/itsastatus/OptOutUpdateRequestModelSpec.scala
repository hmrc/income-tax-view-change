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

package connectors.itsastatus

import connectors.itsastatus.OptOutUpdateRequestModel._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class OptOutUpdateRequestModelSpec extends AnyWordSpec with Matchers {

  "the request model" should {
    val requestObject = OptOutUpdateRequest("2023-2024", optOutUpdateReason)
    val requestJson = Json.parse(
      """
        {
          "taxYear": "2023-2024",
          "updateReason": "10"
        }
        """.stripMargin)

    "read from json" in {
      requestJson.validate[OptOutUpdateRequest] shouldBe JsSuccess(requestObject)
    }
  }

  "the success model" should {
    val successObject = OptOutUpdateResponseSuccess("123")
    val successJson = Json.parse(
      """
        {
          "correlationId": "123",
          "statusCode": 204
        }
        """.stripMargin)

    "read from json" in {
      successJson.validate[OptOutUpdateResponseSuccess] shouldBe JsSuccess(successObject)
    }
  }

  "the failure model" should {

    val failureObject = OptOutUpdateResponseFailure.defaultFailure().copy(correlationId = "123")
    val failureJson = Json.parse(
      """
        {
          "correlationId": "123",
          "statusCode": 500,
          "failures": [{
          "code": "INTERNAL_SERVER_ERROR",
          "reason": "Request failed due to unknown error"
          }]
        }
        """.stripMargin)

    "read from json" in {
      failureJson.validate[OptOutUpdateResponseFailure] shouldBe JsSuccess(failureObject)
    }
  }

}
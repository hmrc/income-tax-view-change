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

import connectors.itsastatus.OptOutUpdateRequestModel.{OptOutUpdateRequest, OptOutUpdateResponseFailure, OptOutUpdateResponseSuccess, optOutUpdateReason}
import connectors.itsastatus.OptOutUpdateRequestModelSpec.{failureJson, failureObj, requestJson, requestObj, successJson, successObj}
import org.eclipse.jetty.http.HttpStatus.{INTERNAL_SERVER_ERROR_500, NO_CONTENT_204}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, JsValue, Json}

class OptOutUpdateRequestModelSpec extends AnyWordSpec with Matchers {

  "the request model" should {
    "read from json" in {
      Json.fromJson[OptOutUpdateRequest](requestJson) shouldBe JsSuccess(requestObj)
    }
    "write to json" in {
      Json.toJson(requestObj) shouldBe requestJson
    }
  }

  "the success model" should {
    "read from json" in {
      Json.fromJson[OptOutUpdateResponseSuccess](successJson) shouldBe JsSuccess(successObj)
    }
    "write to json" in {
      Json.toJson(successObj) shouldBe successJson
    }
  }

  "the failure model" should {
    "read from json" in {
      Json.fromJson[OptOutUpdateResponseFailure](failureJson) shouldBe JsSuccess(failureObj)
    }
    "write to json" in {
      Json.toJson(failureObj) shouldBe failureJson
    }
  }

}

object OptOutUpdateRequestModelSpec {

  private val requestObj = OptOutUpdateRequest("2023-2024", optOutUpdateReason)
  private val requestJson: JsValue = Json.obj(
    "taxYear" -> "2023-2024",
    "updateReason" -> "10",
  )

  private val successObj = OptOutUpdateResponseSuccess("123")
  private val successJson: JsValue = Json.obj(
    "correlationId" -> "123",
    "statusCode" -> NO_CONTENT_204,
  )

  private val failureObj = OptOutUpdateResponseFailure.defaultFailure().copy(correlationId = "123")
  private val failureJson: JsValue = Json.obj(
    "correlationId" -> "123",
    "statusCode" -> INTERNAL_SERVER_ERROR_500,
    "failures" -> Json.arr(
      Json.obj(
        "code" -> "INTERNAL_SERVER_ERROR",
        "reason" -> "Request failed due to unknown error",
      )
    )
  )

}
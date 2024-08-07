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

import play.api.libs.json.{Format, Json}
import play.mvc.Http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}

object OptOutUpdateRequestModel {

  val optOutUpdateReason: String = "10"

  case class OptOutUpdateRequest(taxYear: String, updateReason: String)
  sealed trait OptOutUpdateResponse {
    val statusCode: Int
  }
  case class OptOutUpdateResponseSuccess(correlationId: String, statusCode: Int = NO_CONTENT) extends OptOutUpdateResponse
  case class ErrorItem(code: String, reason: String)
  case class OptOutUpdateResponseFailure(correlationId: String, statusCode: Int, failures: List[ErrorItem]) extends OptOutUpdateResponse

  object OptOutUpdateResponseFailure {
    def defaultFailure(correlationId: String = "unknown",
                       message: String = "Request failed due to unknown error"): OptOutUpdateResponseFailure =
      OptOutUpdateResponseFailure(correlationId,
        INTERNAL_SERVER_ERROR,
        List(ErrorItem("INTERNAL_SERVER_ERROR", message))
      )
  }

  implicit val formatSuccess: Format[OptOutUpdateResponseSuccess] = Json.format[OptOutUpdateResponseSuccess]
  implicit val formatErrorItem: Format[ErrorItem] = Json.format[ErrorItem]
  implicit val formatFailure: Format[OptOutUpdateResponseFailure] = Json.format[OptOutUpdateResponseFailure]
  implicit val format: Format[OptOutUpdateRequest] = Json.format[OptOutUpdateRequest]

}
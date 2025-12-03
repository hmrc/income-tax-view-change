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

package models.hip

import play.api.http.Status.{BAD_GATEWAY, INTERNAL_SERVER_ERROR}
import play.api.libs.json.{Format, JsValue, Json}

sealed trait Errors

case class CustomResponse(message: String) extends Errors

object CustomResponse {
  implicit val format: Format[CustomResponse] = Json.format[CustomResponse]
}

case class Response(`type`: String, reason: String) extends Errors

object Response {
  implicit val format: Format[Response] = Json.format[Response]
}

case class FailureResponse(errorCode: String, errorDescription: String)

object FailureResponse {
  implicit val format: Format[FailureResponse] = Json.format[FailureResponse]
}

case class Failures(failures: Seq[Response])

object Failures {
  implicit val format: Format[Failures] = Json.format[Failures]
}

case class OriginFailuresResponse(origin: String, response: Failures)

object OriginFailuresResponse {
  implicit val format: Format[OriginFailuresResponse] = Json.format[OriginFailuresResponse]
}

case class OriginWithErrorCodeAndResponse(origin: String, response: Seq[FailureResponse])

object OriginWithErrorCodeAndResponse {
  implicit val format: Format[OriginWithErrorCodeAndResponse] = Json.format[OriginWithErrorCodeAndResponse]
}

case class ErrorResponse(status: Int, jsonError: JsValue)

object UnexpectedJsonResponse extends ErrorResponse(
  INTERNAL_SERVER_ERROR,
  Json.toJson(CustomResponse("Unexpected json response"))
)

object UnexpectedResponse extends ErrorResponse(
  INTERNAL_SERVER_ERROR,
  Json.toJson(CustomResponse("Unexpected response"))
)

object BadGatewayResponse extends ErrorResponse(
  BAD_GATEWAY,
  Json.toJson(CustomResponse("BAD_GATEWAY response"))
)

case class HipResponseError(code: String, text: String)

object HipResponseError {
  implicit val formats: Format[HipResponseError] = Json.format[HipResponseError]
}

case class HipResponseErrorsObject(errors: HipResponseError)

object HipResponseErrorsObject {
  implicit val formats: Format[HipResponseErrorsObject] = Json.format[HipResponseErrorsObject]
}
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

package connectors.hip.httpParsers.errorResponses

import models.hip.*
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

import scala.util.Try

trait ErrorResponseHttpParsers extends Logging {

  type HttpGetResult[T] = Either[ErrorResponse, T]

  protected def handleErrorResponse(httpResponse: HttpResponse): Left[ErrorResponse, Nothing] = {
    logger.debug(s"Body received: ${httpResponse.body}")
    Try(httpResponse.status match {
      case BAD_REQUEST =>
        httpResponse.json.validate[OriginFailuresResponse].orElse(httpResponse.json.validate[OriginWithErrorCodeAndResponse]).fold(
          invalid => {
            logger.error(s"Unexpected response with status code: $BAD_REQUEST, response: $invalid")
            Left(ErrorResponse.UnexpectedJsonResponse)
          },
          {
            case expected@(_: OriginFailuresResponse) =>
              logger.error(s"Bad request error response: $expected")
              Left(ErrorResponse.GenericError(BAD_REQUEST, Json.toJson(expected)))
            case expected@(_: OriginWithErrorCodeAndResponse) =>
              logger.error(s"Bad request error response: $expected")
              Left(ErrorResponse.GenericError(BAD_REQUEST, Json.toJson(expected)))
          }
        )
      case UNAUTHORIZED | NOT_FOUND =>
        Try(httpResponse.json).toOption match {
          case Some(json) =>
            json.validate[Seq[FailureResponse]].fold(
              invalid => {
                logger.error(s"Unexpected response with status code: ${httpResponse.status}, response: $invalid")
                Left(ErrorResponse.GenericError(httpResponse.status, Json.toJson(CustomResponse("Unexpected Unauthorized or Not found error"))))
              },
              expected => {
                logger.error(s"Unauthorised or Not found error response, status: ${httpResponse.status}, response: $expected")
                Left(ErrorResponse.GenericError(httpResponse.status, Json.toJson(expected)))

              }
            )
          case None =>
            logger.warn(s"Non-JSON error body received for ${httpResponse.status}: ${httpResponse.body}")
            Left(ErrorResponse.GenericError(httpResponse.status, Json.obj("error" -> httpResponse.body)))
        }
      case INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE =>
        httpResponse.json.validate[OriginFailuresResponse].fold(
          invalid => {
            logger.error(s"Unexpected response with status code: ${httpResponse.status}, response: $invalid")
            Left(ErrorResponse.UnexpectedJsonResponse)
          },
          expected => {
            logger.error(s"InternalServerError or ServiceUnavailable error response, status: ${httpResponse.status}, response: $expected")
            Left(ErrorResponse.GenericError(httpResponse.status, Json.toJson(expected)))
          }
        )
      case BAD_GATEWAY => Left(ErrorResponse.BadGatewayResponse)
      case UNPROCESSABLE_ENTITY => Left(ErrorResponse.UnprocessableData(httpResponse.body))
      case _ =>
        logger.error(s"Unexpected response with status code: ${httpResponse.status}, response: ${httpResponse.body}")
        Left(ErrorResponse.UnexpectedJsonResponse)

    }).getOrElse {
      logger.error(s"Non Json response returned with status code: ${httpResponse.status}, response: ${httpResponse.body}")
      Left(ErrorResponse.UnexpectedResponse)
    }
  }
}

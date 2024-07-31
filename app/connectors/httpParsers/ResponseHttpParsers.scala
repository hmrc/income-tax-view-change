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

import models.errors.{Error, ErrorResponse, InvalidJsonResponse, MultiError, UnexpectedJsonFormat}
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

import scala.util.{Failure, Success, Try}

trait ResponseHttpParsers extends Logging {

  type HttpGetResult[T] = Either[ErrorResponse, T]

  protected def handleErrorResponse(httpResponse: HttpResponse): Left[ErrorResponse, Nothing] = {
    logger.debug(s"Body received: ${httpResponse.body}")
    Left(Try(Json.parse(httpResponse.body)) match {
      case Success(json) =>
        json.asOpt[MultiError].orElse(json.asOpt[Error]) match {
          case Some(error) => ErrorResponse(httpResponse.status, error)
          case _ => UnexpectedJsonFormat
        }
      case Failure(_) => InvalidJsonResponse
    })
  }
}

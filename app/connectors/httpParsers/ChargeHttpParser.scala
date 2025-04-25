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

import models.financialDetails.responses.ChargesResponse
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ChargeHttpParser extends ResponseHttpParsers {

  sealed trait ChargeResponseError

  case object UnexpectedChargeErrorResponse extends ChargeResponseError

  case class UnexpectedChargeResponse(code: Int, response: String) extends ChargeResponseError

  type ChargeResponse = Either[ChargeResponseError, ChargesResponse]

  implicit object ChargeReads extends HttpReads[ChargeResponse] {
    override def read(method: String, url: String, response: HttpResponse): ChargeResponse = {
      response.status match {
        case OK =>
          logger.debug("successful: " + response.json)
          response.json.validate[ChargesResponse] match {
            case JsError(errors) =>
              logger.error("Unable to parse response into ChargesResponse - " + errors)
              Left(UnexpectedChargeErrorResponse)

            case JsSuccess(value, _) =>
              logger.info("successfully parsed response into ChargesResponse")
              println("BEEP1 " + value.codingDetails)
              Right(value)
          }
        case status if status >= 400 && status < 500 =>
          logger.error(s"$status returned from DES with body: ${response.body}")
          Left(UnexpectedChargeResponse(status, response.body))
        case status =>
          logger.error(s"Unexpected Response with status: $status")
          Left(UnexpectedChargeErrorResponse)
      }
    }
  }
}

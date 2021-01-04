/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.Logger
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ChargeHttpParser extends ResponseHttpParsers {

  sealed trait ChargeResponseError

  case object UnexpectedChargeResponse extends ChargeResponseError

  type ChargeResponse = Either[ChargeResponseError, ChargesResponse]

  implicit object ChargeReads extends HttpReads[ChargeResponse] {
    override def read(method: String, url: String, response: HttpResponse): ChargeResponse = {
      response.status match {
        case OK =>
          Logger.info(s"[ChargeReads][read] successfully parsed response to List[Charge]")
          Right(response.json.as[ChargesResponse])
        case status if status >= 400 && status < 500 =>
          Logger.error(s"[ChargeReads][read] $status returned from DES with body: ${response.body}")
          Left(UnexpectedChargeResponse)
        case status =>
          Logger.error(s"[ChargeReads][read] Unexpected Response with status: $status")
          Left(UnexpectedChargeResponse)
      }
    }
  }
}

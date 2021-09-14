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

import models.chargeHistoryDetail.ChargeHistorySuccessResponse
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ChargeHistoryHttpParser extends ResponseHttpParsers {

  sealed trait ChargeHistoryError

  case class UnexpectedChargeHistoryResponse(code: Int, response: String) extends ChargeHistoryError

  case object ChargeHistoryErrorResponse extends ChargeHistoryError

  type ChargeHistoryResponse = Either[ChargeHistoryError, ChargeHistorySuccessResponse]


  implicit object ChargeHistoryReads extends HttpReads[ChargeHistoryResponse] {
    override def read(method: String, url: String, response: HttpResponse): ChargeHistoryResponse = {
      response.status match {
        case OK =>
          logger.info(s"[ChargeHistoryResponse][read] successfully parsed response to List[ChargeHistory]")
          Right(response.json.as[ChargeHistorySuccessResponse])
        case status if status >= 400 && status < 500 =>
          logger.error(s"[ChargeHistoryResponse][read] $status returned from DES with body: ${response.body}")
          Left(UnexpectedChargeHistoryResponse(status, response.body))
        case status =>
          logger.error(s"[ChargeHistoryResponse][read] Unexpected Response with status: $status")
          Left(ChargeHistoryErrorResponse)
      }
    }
  }
}

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

import models.outStandingCharges.{OutStandingCharge, OutstandingChargesSuccessResponse}
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object OutStandingChargesHttpParser extends ResponseHttpParsers {

  sealed trait OutStandingChargesError

  case class UnexpectedOutStandingChargeResponse(code: Int, response: String) extends OutStandingChargesError

  case object OutStandingChargeErrorResponse extends OutStandingChargesError

  type OutStandingChargeResponse = Either[OutStandingChargesError, OutstandingChargesSuccessResponse]

  implicit object OutStandingChargesReads extends HttpReads[OutStandingChargeResponse] {
    override def read(method: String, url: String, response: HttpResponse): OutStandingChargeResponse = {
      response.status match {
        case OK =>
          val outstandingCharges = response.json.as[List[OutStandingCharge]]
          logger.info(s"[OutStandingChargesResponse][read] successfully parsed response to List[OutStandingCharge]")

          Right(OutstandingChargesSuccessResponse(outstandingCharges))
        case status if status >= 400 && status < 500 =>
          logger.error(s"[OutStandingChargesResponse][read] $status returned from DES with body: ${response.body}")
          Left(UnexpectedOutStandingChargeResponse(status, response.body))
        case status =>
          logger.error(s"[OutStandingChargesResponse][read] Unexpected Response with status: $status")
          Left(OutStandingChargeErrorResponse)
      }
    }
  }

}

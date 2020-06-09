/*
 * Copyright 2020 HM Revenue & Customs
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

import models.paymentAllocations.PaymentAllocations
import play.api.Logger
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object PaymentAllocationsHttpParser extends ResponseHttpParsers {

  sealed trait PaymentAllocationsError

  case object UnexpectedResponse extends PaymentAllocationsError

  type PaymentAllocationsResponse = Either[PaymentAllocationsError, PaymentAllocations]

  implicit object PaymentAllocationsReads extends HttpReads[PaymentAllocationsResponse] {
    override def read(method: String, url: String, response: HttpResponse): PaymentAllocationsResponse = {
      response.status match {
        case OK =>
          Logger.info(s"[PaymentAllocationsReads][read] successfully parsed response to PaymentAllocations")
          Right(response.json.as[PaymentAllocations])
        case status if status >= 400 && status < 500 =>
          Logger.error(s"[PaymentAllocationsReads][read] $status returned from DES with body: ${response.body}")
          Left(UnexpectedResponse)
        case status =>
          Logger.error(s"[PaymentAllocationsReads][read] Unexpected Response with status: $status")
          Left(UnexpectedResponse)
      }
    }
  }

}

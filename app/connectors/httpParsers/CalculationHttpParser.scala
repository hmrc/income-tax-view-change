/*
 * Copyright 2019 HM Revenue & Customs
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

import models.PreviousCalculation.{PreviousCalculationModel, UnexpectedJsonFormat, UnexpectedResponse}
import play.api.Logger
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CalculationHttpParser extends ResponseHttpParsers {

  implicit object PreviousCalculationReads extends HttpReads[HttpGetResult[PreviousCalculationModel]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[PreviousCalculationModel] = {
      response.status match {
        case OK =>
          response.json.validate[PreviousCalculationModel].fold(
            invalid => {
              Logger.warn("[PreviousCalculationReads][read] Json Error Parsing Successful DES Response")
              Logger.debug(s"[PreviousCalculationReads][read] DES Response: ${response.json}\nJson Errors: $invalid")

              Left(UnexpectedJsonFormat)
            },
            valid => Right(valid)
          )
        case status if status >= 400 && status < 600 =>
          Logger.debug(s"[PreviousCalculationReads][read] $status returned from DES")
          handleErrorResponse(response)

        case _ =>
          Logger.debug(s"[PreviousCalculationReads][read] Unexpected Response")
          Left(UnexpectedResponse)
      }
    }
  }
}

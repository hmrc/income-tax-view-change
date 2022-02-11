/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CalculationHttpParser extends ResponseHttpParsers {

  implicit object PreviousCalculationReads extends HttpReads[HttpGetResult[PreviousCalculationModel]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[PreviousCalculationModel] = {
      response.status match {
        case OK =>
          response.json.validate[PreviousCalculationModel].fold(
            invalid => {
              logger.error(s"[PreviousCalculationReads][read] could not parse to PreviousCalculationModel. Invalid: $invalid")
              Left(UnexpectedJsonFormat)
            },
            valid => {
              logger.info(s"[PreviousCalculationReads][read] successfully parsed response to PreviousCalculationModel")
              Right(valid)
            }
          )
        case status if status >= 400 && status < 500 =>
          logger.warn(s"[PreviousCalculationReads][read] $status returned from DES with body: ${response.body}")
          handleErrorResponse(response)

        case status =>
          logger.error(s"[PreviousCalculationReads][read] Unexpected Response with status: $status")
          Left(UnexpectedResponse)
      }
    }
  }
}

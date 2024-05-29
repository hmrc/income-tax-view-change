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

import models.calculationList.{CalculationListModel, CalculationListResponseModel}
import models.errors.{UnexpectedJsonFormat, UnexpectedResponse}
import play.api.http.Status.{NO_CONTENT, OK}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CalculationListHttpParser extends ResponseHttpParsers {

  implicit object CalculationListReads extends HttpReads[HttpGetResult[CalculationListResponseModel]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[CalculationListResponseModel] = {
      response.status match {
        case OK =>
          response.json.validate[Seq[CalculationListModel]].fold(
            invalid => {
              logger.error(s"could not parse to CalculationListResponseModel. Invalid: $invalid")
              Left(UnexpectedJsonFormat)
            },
            valid => {
              logger.info("successfully parsed response to CalculationListResponseModel")
              Right(CalculationListResponseModel(valid))
            }
          )
        case NO_CONTENT =>
          logger.info(s"${response.status} returned")
          Right(CalculationListResponseModel(Seq.empty))

        case status if status >= 400 && status < 500 =>
          logger.warn(s"$status returned from DES with body: ${response.body}")
          handleErrorResponse(response)

        case status =>
          logger.error(s"Unexpected Response with status: $status")
          Left(UnexpectedResponse)
      }
    }
  }
}

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

package connectors.hip.httpParsers

import connectors.hip.httpParsers.errorResponses.ErrorResponseHttpParsers
import models.calculationList.{CalculationListModel, CalculationListResponseModel}
import models.hipErrors.UnexpectedJsonResponse
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CalculationListLegacyHttpParser extends ErrorResponseHttpParsers {

  implicit object CalculationListReads extends HttpReads[HttpGetResult[CalculationListResponseModel]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[CalculationListResponseModel] = {
      response.status match {
        case OK =>
          response.json.validate[Seq[CalculationListModel]].fold(
            invalid => {
              logger.error(s"could not parse to CalculationListResponseModel. Invalid: $invalid")
              Left(UnexpectedJsonResponse)
            },
            valid => {
              logger.info("successfully parsed response to CalculationListResponseModel")
              Right(CalculationListResponseModel(valid))
            }
          )
        case status =>
          logger.warn(s"$status returned from DES with body: ${response.body}")
          handleErrorResponse(response)
      }
    }
  }
}

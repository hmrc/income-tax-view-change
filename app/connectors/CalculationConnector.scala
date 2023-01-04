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

package connectors

import config.MicroserviceAppConfig
import connectors.httpParsers.CalculationHttpParser._
import models.PreviousCalculation.PreviousCalculationModel
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends Logging {

  private[connectors] def getPreviousCalculationUrl(nino: String, year: String): String =
    s"${appConfig.desUrl}/income-tax/previous-calculation/$nino?year=$year"

  def getPreviousCalculation(nino: String, year: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[PreviousCalculationModel]] = {
    val url = getPreviousCalculationUrl(nino, year)

    logger.debug(s"[CalculationConnector][getPreviousCalculation] - Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}")
    http.GET(url = url, headers = appConfig.desAuthHeaders)(PreviousCalculationReads, headerCarrier, ec)
  }
}

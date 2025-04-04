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

package connectors.hip

import config.MicroserviceAppConfig
import connectors.hip.httpParsers.CalculationListLegacyHttpParser.{CalculationListReads, HttpGetResult}
import models.calculationList.CalculationListResponseModel
import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationListLegacyConnector @Inject()(val http: HttpClientV2, val appConfig: MicroserviceAppConfig) extends Logging {

  private val API_1404_CONFIG_NAME = "get-calc-list-1404"
  private val isGetCalcApiEnabledInHip = appConfig.isHIPFeatureSwitchEnabled(API_1404_CONFIG_NAME)

  private[connectors] def getCalculationListUrl(nino: String, taxYearEnd: String): String =
    s"${appConfig.hipUrl}/itsd/calculations/liability/$nino?taxYear=$taxYearEnd"

  def getCalculationList(nino: String, taxYear: String)
                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {
    val url = getCalculationListUrl(nino, taxYear)

    logger.debug(
      s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${appConfig.getHIPHeaders(API_1404_CONFIG_NAME, headerCarrier.requestId)}" +
        s" \nIs1404GetCalcApiEnabledInHip: ${if (isGetCalcApiEnabledInHip) "YES" else "NO"}"
    )

    http.get(url"$url")
      .setHeader(
        appConfig.getHIPHeaders(API_1404_CONFIG_NAME, headerCarrier.requestId): _*
      )
      .execute[HttpGetResult[CalculationListResponseModel]](CalculationListReads, ec)
  }
}

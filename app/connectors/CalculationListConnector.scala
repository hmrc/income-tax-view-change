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
import connectors.httpParsers.CalculationListHttpParser.CalculationListReads
import connectors.httpParsers.ChargeHttpParser.HttpGetResult
import models.calculationList.CalculationListResponseModel
import play.api.Logging
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationListConnector @Inject()(val http: HttpClientV2, val appConfig: MicroserviceAppConfig) extends Logging {

  private[connectors] def getCalculationListUrl(nino: String, taxYearEnd: String): String = {
    val platformUrl = if (appConfig.useGetCalcListIFPlatform) appConfig.ifUrl else appConfig.desUrl
    s"$platformUrl/income-tax/list-of-calculation-results/$nino?taxYear=$taxYearEnd"
  }

  private[connectors] def getCalculationListTYSUrl(nino: String, taxYearRange: String): String =
    s"${appConfig.ifUrl}/income-tax/view/calculations/liability/$taxYearRange/$nino"

  private[connectors] def getCalculationList2083Url(nino: String, taxYearRange: String): String =
    s"${appConfig.ifUrl}/income-tax/$taxYearRange/view/$nino/calculations-summary"

  def getHeaders(api: String): Seq[(String, String)] = {
    if (appConfig.useGetCalcListIFPlatform) appConfig.getIFHeaders(api = api) else appConfig.desAuthHeaders
  }

  @deprecated("Deprecated:: remove after HiP migration", "MISUV-???")
  def getCalculationList(nino: String, taxYear: String)
                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {
    val url = getCalculationListUrl(nino, taxYear)

    logger.debug(s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${getHeaders("1404")} \nIsMigratedToIF: ${if (appConfig.useGetCalcListIFPlatform) "YES" else "NO"}")
    http.get(url"$url")
      .setHeader(getHeaders("1404"): _*)
      .execute[HttpGetResult[CalculationListResponseModel]](CalculationListReads, ec)
  }

  def getCalculationListTYS(nino: String, taxYear: String)
                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {
    val url = getCalculationListTYSUrl(nino, taxYear)

    logger.debug(s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${appConfig.getIFHeaders("1896")}")
    http.get(url"$url")
      .setHeader(appConfig.getIFHeaders("1896"): _*)
      .execute[HttpGetResult[CalculationListResponseModel]](CalculationListReads, ec)

  }

  def getCalculationList2083(nino: String, taxYear: String)
                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {

    val url = getCalculationList2083Url(nino, taxYear)

    logger.debug(s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${appConfig.getIFHeaders("2083")}")
    http.get(url"$url")
      .setHeader(appConfig.getIFHeaders("2083"): _*)
      .execute[HttpGetResult[CalculationListResponseModel]](CalculationListReads, ec)

  }

}

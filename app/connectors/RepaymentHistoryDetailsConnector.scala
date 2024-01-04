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
import connectors.httpParsers.RepaymentHistoryHttpParser.{RepaymentHistoryReads, RepaymentHistoryResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepaymentHistoryDetailsConnector @Inject()(val http: HttpClient,
                                                 val appConfig: MicroserviceAppConfig
                                                )(implicit ec: ExecutionContext) extends RawResponseReads {

  def listRepaymentHistoryDetailsUrl(nino: String): String = {
    val platformUrl = if (appConfig.useRepaymentHistoryDetailsIFPlatform) appConfig.ifUrl else appConfig.desUrl
    s"${platformUrl}/income-tax/self-assessment/repayments-viewer/$nino"
  }

  def headers: Seq[(String, String)] = {
    if (appConfig.useRepaymentHistoryDetailsIFPlatform) appConfig.ifAuthHeaders1771 else appConfig.desAuthHeaders
  }

  private[connectors] def IdQueryParameters(repaymentId: String): Seq[(String, String)] = {
    Seq(
      "repaymentRequestNumber" -> repaymentId
    )
  }

  def getAllRepaymentHistoryDetails(nino: String)(implicit headerCarrier: HeaderCarrier): Future[RepaymentHistoryResponse] = {
    val url = listRepaymentHistoryDetailsUrl(nino)
    logger.info(s"[RepaymentHistoryDetailsConnector][getAllRepaymentHistoryDetails] - Calling GET $url " +
      s"\nHeaders: $headerCarrier \nIsMigrated: ${if (appConfig.useRepaymentHistoryDetailsIFPlatform) "YES" else "NO"}")
    http.GET(
      url = url,
      queryParams = Seq.empty,
      headers = headers
    )(RepaymentHistoryReads, headerCarrier, ec)
  }

  def getRepaymentHistoryDetailsById(nino: String,
                                     repaymentId: String)(implicit headerCarrier: HeaderCarrier): Future[RepaymentHistoryResponse] = {
    val url = listRepaymentHistoryDetailsUrl(nino)
    logger.info(s"[RepaymentHistoryDetailsConnector][getRepaymentHistoryDetailsById] - Calling GET $url " +
      s"\nHeaders: $headerCarrier \nIsMigrated: ${if (appConfig.useRepaymentHistoryDetailsIFPlatform) "YES" else "NO"}")
    http.GET(
      url = url,
      queryParams = IdQueryParameters(repaymentId),
      headers = headers
    )(RepaymentHistoryReads, headerCarrier, ec)
  }
}

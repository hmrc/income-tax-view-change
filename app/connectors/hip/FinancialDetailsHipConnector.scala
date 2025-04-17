/*
 * Copyright 2025 HM Revenue & Customs
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
import connectors.RawResponseReads
import connectors.hip.httpParsers.ChargeHipHttpParser.{ChargeHipReads, ChargeHipResponse}
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URLEncoder
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDetailsHipConnector  @Inject()(val http: HttpClientV2,
                                              appConfig: MicroserviceAppConfig) extends RawResponseReads with Logging {

  private val serviceBaseUrl: String = appConfig.hipUrl

  // TODO: set up headers accoring to the spec / add spec number
  def headers: Seq[(String, String)] = appConfig.getIFHeaders("1553")

  private[connectors] def fullServicePath: String = {
    s"$serviceBaseUrl/RESTAdapter/itsa/taxpayer/financial-details"
  }

  // TODO: implement query string params creation
  private[connectors] def createQueryString(from: String, to: String): Seq[(String, String)] = {
    Seq(
      "dateFrom" -> from,
      "dateTo" -> to
    ) ++: baseQueryParameters(onlyOpenItems = false)
  }

  def makeQueryString(queryParams: Seq[(String, String)]) = {
    val paramPairs = queryParams.map { case (k, v) => s"$k=${URLEncoder.encode(v, "utf-8")}" }
    if (paramPairs.isEmpty) "" else paramPairs.mkString("?", "&", "")
  }

  def getChargeDetails(nino: String, from: String, to: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    getCharge(nino, queryParameters = createQueryString(from, to))
  }

  private[connectors] def paymentAllocationQuery(documentId: String): Seq[(String, String)] = {
    Seq(
      "docNumber" -> documentId
    ) ++: baseQueryParameters(onlyOpenItems = false)
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    getCharge(nino = nino, queryParameters = paymentAllocationQuery(documentId))
  }

  private[connectors] def onlyOpenItemsQuery(): Seq[(String, String)] = baseQueryParameters(onlyOpenItems = true)

  def getOnlyOpenItems(nino: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    getCharge(nino = nino, queryParameters = onlyOpenItemsQuery())
  }

  private def baseQueryParameters(onlyOpenItems: Boolean): Seq[(String, String)] = {
    Seq(
      "onlyOpenItems" -> onlyOpenItems.toString,
      "includeLocks" -> "true",
      "calculateAccruedInterest" -> "true",
      "removePOA" -> "false",
      "customerPaymentInformation" -> "true",
      "includeStatistical" -> "false"
    )
  }

  private[connectors] def getCharge(nino: String, queryParameters: Seq[(String, String)])
                                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    val url = fullServicePath + makeQueryString(queryParameters)
    // TODO: downgrade to debug after fix
    logger.info(s"URL - $url - ${queryParameters.mkString("-")}")
    http.get(url"$url")
      .setHeader(headers: _*)
      .execute[ChargeHipResponse](ChargeHipReads, ec)
  }

}

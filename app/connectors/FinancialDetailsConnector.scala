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
import connectors.httpParsers.ChargeHttpParser.{ChargeReads, ChargeResponse}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDetailsConnectorDES @Inject()(val http: HttpClient,
                                             appConfig: MicroserviceAppConfig) extends FinancialDetailsConnector {
  override val baseUrl: String = appConfig.desUrl

  override def headers(implicit hc: HeaderCarrier): Seq[(String, String)] = appConfig.desAuthHeaders
}

@Singleton
class FinancialDetailsConnectorIF @Inject()(val http: HttpClient,
                                            appConfig: MicroserviceAppConfig) extends FinancialDetailsConnector {
  override val baseUrl: String = appConfig.ifUrl

  override def headers(implicit hc: HeaderCarrier): Seq[(String, String)] = appConfig.ifAuthHeaders
}


trait FinancialDetailsConnector extends RawResponseReads with Logging {

  def http: HttpClient

  def baseUrl: String

  def headers(implicit hc: HeaderCarrier): Seq[(String, String)]

  private[connectors] def financialDetailsUrl(nino: String): String = {
    s"$baseUrl/enterprise/02.00.00/financial-data/NINO/$nino/ITSA"
  }

  private[connectors] def chargeDetailsQuery(from: String, to: String): Seq[(String, String)] = {
    Seq(
      "dateFrom" -> from,
      "dateTo" -> to
    ) ++: baseQueryParameters(onlyOpenItems = false)
  }

  def getChargeDetails(nino: String, from: String, to: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] = {
    getCharge(nino, queryParameters = chargeDetailsQuery(from, to))
  }

  private[connectors] def paymentAllocationQuery(documentId: String): Seq[(String, String)] = {
    Seq(
      "docNumber" -> documentId
    ) ++: baseQueryParameters(onlyOpenItems = false)
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] = {
    getCharge(nino = nino, queryParameters = paymentAllocationQuery(documentId))
  }

  private[connectors] def onlyOpenItemsQuery(): Seq[(String, String)] = baseQueryParameters(onlyOpenItems = true)

  def getOnlyOpenItems(nino: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] = {
    getCharge(nino = nino, queryParameters = onlyOpenItemsQuery())
  }

  private def baseQueryParameters(onlyOpenItems: Boolean): List[(String, String)] = {
    List(
      "onlyOpenItems" -> onlyOpenItems.toString,
      "includeLocks" -> "true",
      "calculateAccruedInterest" -> "true",
      "removePOA" -> "false",
      "customerPaymentInformation" -> "true",
      "includeStatistical" -> "false"
    )
  }

  private[connectors] def getCharge(nino: String, queryParameters: Seq[(String, String)])
                                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] = {
    val url = financialDetailsUrl(nino)
    // TODO: downgrade to debug after fix
    logger.info(s"[FinancialDetailChargesController][getChargeDetails] - URL - $url - ${queryParameters.mkString("-")}")
    http.GET(url, queryParameters, headers)(ChargeReads, hc, ec)
  }

}

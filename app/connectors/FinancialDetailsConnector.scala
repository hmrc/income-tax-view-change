/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FinancialDetailsConnector @Inject()(val http: HttpClient,
                                          val appConfig: MicroserviceAppConfig)
                                         (implicit ec: ExecutionContext) extends RawResponseReads {

  private[connectors] def financialDetailsUrl(nino: String): String = {
    s"${appConfig.desUrl}/enterprise/02.00.00/financial-data/NINO/$nino/ITSA"
  }

  private[connectors] def chargeDetailsQuery(from: String, to: String): Seq[(String, String)] = {
    Seq(
      "dateFrom" -> from,
      "dateTo" -> to,
      "onlyOpenItems" -> "false",
      "includeLocks" -> "true",
      "calculateAccruedInterest" -> "true",
      "removePOA" -> "false",
      "customerPaymentInformation" -> "true",
      "includeStatistical" -> "false"
    )
  }

  def getChargeDetails(nino: String, from: String, to: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] = {
    getCharge(url = financialDetailsUrl(nino), queryParameters = chargeDetailsQuery(from, to))
  }

  private[connectors] def paymentAllocationFinancialDetailsUrl(nino: String): String = financialDetailsUrl(nino)

  private[connectors] def paymentAllocationQuery(documentId: String): Seq[(String, String)] = {
    Seq(
      "docNumber" -> documentId,
      "onlyOpenItems" -> "false",
      "includeLocks" -> "true",
      "calculateAccruedInterest" -> "true",
      "removePOA" -> "false",
      "customerPaymentInformation" -> "true",
      "includeStatistical" -> "false"
    )
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] = {
    getCharge(url = paymentAllocationFinancialDetailsUrl(nino), queryParameters = paymentAllocationQuery(documentId))
  }

  private[connectors] def getCharge(url: String, queryParameters: Seq[(String, String)])
                                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeResponse] =
    http.GET(url, queryParameters, appConfig.desAuthHeaders)(ChargeReads, hc, ec)

}

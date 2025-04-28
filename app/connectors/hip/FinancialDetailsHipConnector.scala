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
import models.hip.GetFinancialDetailsHipApi
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URLEncoder
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDetailsHipConnector @Inject()(val http: HttpClientV2,
                                             appConfig: MicroserviceAppConfig) extends RawResponseReads with Logging with HipConnectorDataHelper {

  private val serviceBaseUrl: String = appConfig.hipUrl

  private[connectors] def fullServicePath: String = {
    s"$serviceBaseUrl/etmp/RESTAdapter/itsa/taxpayer/financial-details"
  }

  private[connectors] def getQueryStringParams(nino: String, fromDate: String, toDate: String): Seq[(String, String)] = {
    baseQueryParameters(nino, onlyOpenItems = false).take(2) ++:
      Seq(
        "dateFrom" -> fromDate,
        "dateTo" -> toDate
      ) ++: baseQueryParameters(nino, onlyOpenItems = false)
      .takeRight(7)
  }

  def buildQueryString(queryParams: Seq[(String, String)]) = {
    val paramPairs = queryParams.map { case (k, v) => s"$k=${URLEncoder.encode(v, "utf-8")}" }
    if (paramPairs.isEmpty) "" else paramPairs.mkString("?", "&", "")
  }

  private[connectors] def paymentAllocationQuery(nino:String, documentId: String): Seq[(String, String)] = {
    baseQueryParameters(nino, onlyOpenItems = false) ++: Seq( "sapDocumentNumber" -> documentId )
  }

  private[connectors] def onlyOpenItemsQuery(nino: String): Seq[(String, String)] =
    baseQueryParameters(nino, onlyOpenItems = true)

  def baseQueryParameters(nino:String, onlyOpenItems: Boolean): Seq[(String, String)] = {
    Seq(
      "calculateAccruedInterest" -> calculateAccruedInterest,
      "customerPaymentInformation" -> customerPaymentInformation,
      "idNumber" -> nino,
      "idType" -> idType,
      "includeLocks" -> includeLocks,
      "includeStatistical" -> includeStatistical,
      "onlyOpenItems" -> onlyOpenItems.toString,
      "regimeType" -> regimeType,
      "removePaymentonAccount" -> removePaymentonAccount
    )
  }

  private[connectors] def getCharge(queryParameters: Seq[(String, String)])
                                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    val url = s"$fullServicePath${buildQueryString(queryParameters)}"
    // TODO: downgrade to debug when PR will be in the review
    logger.info(s"URL - $url")
    http.get(url"$url")
      .setHeader( // set correlationId and basic auth
        appConfig.getHIPHeaders(GetFinancialDetailsHipApi): _*
      )
      .setHeader(("X-Message-Type", xMessageTypeFor5277))
      .setHeader(("X-Originating-System", xOriginatingSystem))
      .setHeader(("X-Receipt-Date", getMessageCreated))
      .setHeader(("X-Transmitting-System", xTransmittingSystem))
      .execute[ChargeHipResponse](ChargeHipReads, ec)
      .collect{
        case Left(ex) =>
          logger.error(s"Response: $ex")
          Left(ex)
        case Right(value) =>
          Right(value)
      }
  }

  def getChargeDetails(nino: String, from: String, to: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    getCharge(queryParameters = getQueryStringParams(nino, from, to))
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    getCharge( queryParameters = paymentAllocationQuery(nino, documentId))
  }

  def getOnlyOpenItems(nino: String)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHipResponse] = {
    getCharge(queryParameters = onlyOpenItemsQuery(nino))
  }
}

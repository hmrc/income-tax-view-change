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
import connectors.httpParsers.ChargeHistoryHttpParser.{ChargeHistoryReads, ChargeHistoryResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChargeHistoryDetailsConnector @Inject()(val http: HttpClient,
                                              val appConfig: MicroserviceAppConfig
                                             )(implicit ec: ExecutionContext) extends RawResponseReads {

  def listChargeHistoryDetailsUrl(idType: String, idNumber: String, regimeType: String): String =
    s"${appConfig.desUrl}/cross-regime/charges/$idType/$idNumber/$regimeType"

  private[connectors] def queryParameters(docNumber: String): Seq[(String, String)] = {
    Seq(
      "docNumber" -> docNumber
    )
  }

  def getChargeHistoryDetailsLegacy(mtdBsa: String, docNumber: String)(implicit headerCarrier: HeaderCarrier): Future[ChargeHistoryResponse] = {
    http.GET(
      url = listChargeHistoryDetailsUrl("MTDBSA", mtdBsa, "ITSA"),
      queryParams = queryParameters(docNumber),
      headers = appConfig.desAuthHeaders
    )(ChargeHistoryReads, headerCarrier, ec)
  }

  // TODO: We only have queryParam 'docNumber', Should we have queryParams for 'dateFrom' and 'dateTo' ? Call examples:
  //    cross-regime/charges/NINO/IN408059B/ITSA?dateFrom=2017-01-01&dateTo=2017-01-31
  //    cross-regime/charges/MTDBSA/XAIT9999999999/ITSA?docNumber=XM0026100122
  // TODO: idType could be 'NINO' or 'MTDBSA', presumably agent uses MTDBSA, should we incorporate this logic? Call examples:
  //    cross-regime/charges/NINO/IN408059B/ITSA?dateFrom=2017-01-01&dateTo=2017-01-31
  //    cross-regime/charges/MTDBSA/XAIT9999999999/ITSA?docNumber=XM0026100122

  def getChargeHistoryDetails(mtdBsa: String, docNumber: String)(implicit headerCarrier: HeaderCarrier): Future[ChargeHistoryResponse] = {
    http.GET(
      url = listChargeHistoryDetailsUrl("MTDBSA", mtdBsa, "ITSA"),
      queryParams = queryParameters(docNumber),
      headers = appConfig.ifAuthHeaders
    )(ChargeHistoryReads, headerCarrier, ec)
  }

}

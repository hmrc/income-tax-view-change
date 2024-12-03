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
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChargeHistoryDetailsConnector @Inject()(val http: HttpClientV2,
                                              val appConfig: MicroserviceAppConfig
                                             )(implicit ec: ExecutionContext) extends RawResponseReads {


  def listChargeHistoryDetailsUrl(idType: String, idNumber: String, regimeType: String, chargeReference: String): String =
    s"${appConfig.ifUrl}/cross-regime/charges/$idType/$idNumber/$regimeType?chargeReference=$chargeReference"

  def headers: Seq[(String, String)] = appConfig.getIFHeaders("1554")

//  private[connectors] def queryParameters(chargeReference: String): Seq[(String, String)] = {
//    Seq(
//      "chargeReference" -> chargeReference
//    )
//  }

  def getChargeHistoryDetails(nino: String, chargeReference: String)(implicit headerCarrier: HeaderCarrier): Future[ChargeHistoryResponse] = {

    val url = listChargeHistoryDetailsUrl("NINO", nino, "ITSA", chargeReference)
    http.get(url"$url")
      .setHeader(headers: _*)
      .execute[ChargeHistoryResponse](ChargeHistoryReads, ec)

//    http.GET(
//      url = listChargeHistoryDetailsUrl("NINO", nino, "ITSA"),
//      queryParams = queryParameters(chargeReference),
//      headers = appConfig.getIFHeaders("1554")
//    )(ChargeHistoryReads, headerCarrier, ec)
  }
}

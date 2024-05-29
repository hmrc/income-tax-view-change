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
    s"${appConfig.ifUrl}/cross-regime/charges/$idType/$idNumber/$regimeType"

  private[connectors] def queryParameters(chargeReference: String): Seq[(String, String)] = {
    Seq(
      "chargeReference" -> chargeReference
    )
  }

  def getChargeHistoryDetails(mtdBsa: String, chargeReference: String)(implicit headerCarrier: HeaderCarrier): Future[ChargeHistoryResponse] = {
    http.GET(
      url = listChargeHistoryDetailsUrl("MTDBSA", mtdBsa, "ITSA"),
      queryParams = queryParameters(chargeReference),
      headers = appConfig.ifAuthHeaders
    )(ChargeHistoryReads, headerCarrier, ec)
  }
}

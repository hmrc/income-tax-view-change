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
import connectors.httpParsers.OutStandingChargesHttpParser.{OutStandingChargeResponse, OutStandingChargesReads}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OutStandingChargesConnector @Inject()(val http: HttpClientV2,
                                            val appConfig: MicroserviceAppConfig
                                           )(implicit ec: ExecutionContext) extends RawResponseReads {

  def listOutStandingChargesUrl(idType: String, idNumber: String, taxYearEndDate: String): String =
    s"${appConfig.desUrl}/income-tax/charges/outstanding/$idType/$idNumber/$taxYearEndDate"

  def listOutStandingCharges(idType: String, idNumber: String, taxYearEndDate: String)
                            (implicit headerCarrier: HeaderCarrier): Future[OutStandingChargeResponse] = {

    val url = listOutStandingChargesUrl(idType, idNumber, taxYearEndDate)

    http
      .get(url"$url")
      .setHeader(appConfig.desAuthHeaders: _*)
      .execute[OutStandingChargeResponse](OutStandingChargesReads, ec)
  }
}

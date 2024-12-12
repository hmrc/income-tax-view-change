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
import connectors.httpParsers.PaymentAllocationsHttpParser.{PaymentAllocationsReads, PaymentAllocationsResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationsConnector @Inject()(val http: HttpClientV2,
                                            val appConfig: MicroserviceAppConfig)
                                           (implicit ec: ExecutionContext) extends RawResponseReads {

  private[connectors] def paymentAllocationsUrl(nino: String): String = {
    s"${appConfig.desUrl}/cross-regime/payment-allocation/NINO/$nino/ITSA"
  }

  private[connectors] def queryParameters(paymentLot: String, paymentLotItem: String): Seq[(String, String)] = {
    Seq(
      "paymentLot" -> paymentLot,
      "paymentLotItem" -> paymentLotItem
    )
  }

  def getPaymentAllocations(nino: String, paymentLot: String, paymentLotItem: String)
                           (implicit hc: HeaderCarrier): Future[PaymentAllocationsResponse] =
    http
      .get(url"${paymentAllocationsUrl(nino)}")
      .setHeader(appConfig.desAuthHeaders: _*)
      .transform(_.addQueryStringParameters(queryParameters(paymentLot, paymentLotItem): _*))
      .execute[PaymentAllocationsResponse](PaymentAllocationsReads, ec)
}

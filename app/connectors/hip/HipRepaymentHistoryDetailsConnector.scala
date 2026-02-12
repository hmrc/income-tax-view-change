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

package connectors.hip

import config.MicroserviceAppConfig
import connectors.hip.httpParsers.ChargeHipHttpParser.HttpGetResult
import models.hip.GetRepaymentHistoryDetails
import models.hip.repayments.SuccessfulRepaymentResponse
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import connectors.hip.httpParsers.RepaymentsHistoryDetailsHttpParser.given

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HipRepaymentHistoryDetailsConnector @Inject()(val http: HttpClientV2, val appConfig: MicroserviceAppConfig) extends Logging {

  def getUrl(idValue: String, repaymentRequestNumber: Option[String]): String = {
    repaymentRequestNumber match {
      case Some(value) => s"${appConfig.hipUrl}/etmp/RESTAdapter/ITSA/RepaymentsViewer/$idValue?repaymentRequestNumber=$value"
      case None => s"${appConfig.hipUrl}/etmp/RESTAdapter/ITSA/RepaymentsViewer/$idValue"
    }
  }
    def getHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(GetRepaymentHistoryDetails)


  def getRepaymentHistoryDetailsList(idValue: String)
                                    (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[SuccessfulRepaymentResponse]] = {

    val url = getUrl(idValue, None)
    logger.debug(s"Calling GET $url \nHeaders: $getHeaders")
    http
      .get(url"$url")
      .setHeader(getHeaders: _*)
      .execute[HttpGetResult[SuccessfulRepaymentResponse]]
  }

  def getRepaymentHistoryDetails(idValue: String, repaymentRequestNumber: String)
                                    (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[SuccessfulRepaymentResponse]] = {

    val url = getUrl(idValue, Some(repaymentRequestNumber))
    logger.debug(s"Calling GET $url \nHeaders: $getHeaders")
    http
      .get(url"$url")
      .setHeader(getHeaders: _*)
      .execute[HttpGetResult[SuccessfulRepaymentResponse]]
  }
  }


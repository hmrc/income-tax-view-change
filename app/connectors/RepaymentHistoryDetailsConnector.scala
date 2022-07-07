/*
 * Copyright 2022 HM Revenue & Customs
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
import connectors.httpParsers.RepaymentHistoryHttpParser.{RepaymentHistoryReads, RepaymentHistoryResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepaymentHistoryDetailsConnector @Inject()(val http: HttpClient,
                                                 val appConfig: MicroserviceAppConfig
                                             )(implicit ec: ExecutionContext) extends RawResponseReads {

  def listRepaymentHistoryDetailsUrl(nino: String): String =
    s"${appConfig.desUrl}/income-tax/self-assessment/repayments-viewer/$nino"

  private[connectors] def dateQueryParameters(fromDate: String): Seq[(String, String)] = {
    Seq(
      "fromDate" -> fromDate
    )
  }

  private[connectors] def IdQueryParameters(repaymentId: String): Seq[(String, String)] = {
    Seq(
      "repaymentId" -> repaymentId
    )
  }

  def getAllRepaymentHistoryDetails(nino: String)(implicit headerCarrier: HeaderCarrier): Future[RepaymentHistoryResponse] = {
    http.GET(
      url = listRepaymentHistoryDetailsUrl(nino),
      queryParams = Seq.empty,
      headers = appConfig.desAuthHeaders
    )(RepaymentHistoryReads, headerCarrier, ec)
  }

  def getRepaymentHistoryDetailsById(nino: String,
                                     repaymentId: String)(implicit headerCarrier: HeaderCarrier): Future[RepaymentHistoryResponse] = {
    http.GET(
      url = listRepaymentHistoryDetailsUrl(nino),
      queryParams = IdQueryParameters(repaymentId),
      headers = appConfig.desAuthHeaders
    )(RepaymentHistoryReads, headerCarrier, ec)
  }
}

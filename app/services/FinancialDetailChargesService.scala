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

package services

import config.MicroserviceAppConfig
import connectors.FinancialDetailsConnector
import connectors.hip.FinancialDetailsHipConnector
import connectors.httpParsers.ChargeHttpParser.ChargeResponse
import models.hip.GetFinancialDetailsHipApi
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class FinancialDetailChargesService @Inject()(val connector: FinancialDetailsConnector,
                                              hipConnector: FinancialDetailsHipConnector,
                                              val appConfig: MicroserviceAppConfig)
                                             (implicit ec: ExecutionContext) extends Logging {

  private val isHipOn = appConfig.hipFeatureSwitchEnabled(GetFinancialDetailsHipApi)

  def getChargeDetails(nino: String, fromDate: String, toDate: String)
                      (implicit hc: HeaderCarrier) : Future[ChargeResponse] = {
    if (isHipOn){
      // TODO: use HipConnector after testing
      connector.getChargeDetails(nino, fromDate, toDate)
    } else {
      connector.getChargeDetails(nino, fromDate, toDate)
    }
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier): Future[ChargeResponse] = {
    if (isHipOn){
      // TODO: use HipConnector after testing
      connector.getPaymentAllocationDetails(nino, documentId)
    } else {
      connector.getPaymentAllocationDetails(nino, documentId)
    }
  }

  def getOnlyOpenItems(nino: String)(implicit hc: HeaderCarrier): Future[ChargeResponse] = {
    if (isHipOn){
      // TODO: use HipConnector after testing
      connector.getOnlyOpenItems(nino)
    } else {
      connector.getOnlyOpenItems(nino)
    }
  }

}

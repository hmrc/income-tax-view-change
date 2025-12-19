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
import connectors.hip.FinancialDetailsHipConnector
import connectors.httpParsers.ChargeHttpParser.ChargeResponseError
import models.credits.CreditsModel
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class FinancialDetailService @Inject()(val hipConnector: FinancialDetailsHipConnector,
                                       val appConfig: MicroserviceAppConfig)
                                      (implicit ec: ExecutionContext) extends Logging {

  type ChargeAsJsonResponse = Either[ChargeResponseError, JsValue]
  type PaymentsAsJsonResponse = Either[ChargeResponseError, JsValue]

  def getChargeDetails(nino: String, fromDate: String, toDate: String)
                      (implicit hc: HeaderCarrier) : Future[ChargeAsJsonResponse] = {
      hipConnector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
           Left(err)
        }
  }

  def getPayments(nino: String, fromDate: String, toDate: String)
                      (implicit hc: HeaderCarrier) : Future[PaymentsAsJsonResponse] = {
    logger.debug(s"Call::getPayments")
      hipConnector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges.payments))
          case Left(err) =>
            Left(err)
        }
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier): Future[ChargeAsJsonResponse] = {
    logger.info(s"Call::getPaymentAllocationDetails")
      hipConnector.getPaymentAllocationDetails(nino, documentId)
        .collect{
          case Right(charges) =>
            logger.info(s"Call::getPaymentAllocationDetails -> $charges")
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
  }

  def getOnlyOpenItems(nino: String)(implicit hc: HeaderCarrier): Future[ChargeAsJsonResponse] = {
    logger.info(s"Call::getOnlyOpenItems")
      hipConnector.getOnlyOpenItems(nino)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
  }

  def getCredits(nino: String, fromDate: String, toDate: String)
                (implicit hc: HeaderCarrier) : Future[ChargeAsJsonResponse] = {
    logger.info(s"Call::getCreditsModel")
      hipConnector
        .getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            val creditsModel: CreditsModel = CreditsModel.fromHipChargesResponse(charges)
            Right(Json.toJson(creditsModel))
          case Left(err) =>
            Left(err)
        }
  }
}

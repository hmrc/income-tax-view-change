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
import connectors.httpParsers.ChargeHttpParser.{ChargeResponseError}
import models.credits.CreditsModel
import models.hip.GetFinancialDetailsHipApi
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


// TODO:    Tidy up story need to be created when HiP migration is over;
// TODO-2:  Avoid using approach where we return Either[_, JsValue] as its very "fragile"
class FinancialDetailChargesService @Inject()(val connector: FinancialDetailsConnector,
                                              val hipConnector: FinancialDetailsHipConnector,
                                              val appConfig: MicroserviceAppConfig)
                                             (implicit ec: ExecutionContext) {

  type ChargeAsJsonResponse = Either[ChargeResponseError, JsValue]
  type PaymentsAsJsonResponse = Either[ChargeResponseError, JsValue]

  private val isHipOn = appConfig.hipFeatureSwitchEnabled(GetFinancialDetailsHipApi)

  def getChargeDetails(nino: String, fromDate: String, toDate: String)
                      (implicit hc: HeaderCarrier) : Future[ChargeAsJsonResponse] = {
    if (isHipOn) {
      hipConnector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
           Left(err)
        }
    } else {
      connector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
    }
  }

  def getPayments(nino: String, fromDate: String, toDate: String)
                      (implicit hc: HeaderCarrier) : Future[PaymentsAsJsonResponse] = {

    if (isHipOn) {
      hipConnector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges.payments))
          case Left(err) =>
            Left(err)
        }
    } else {
      connector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges.payments))
          case Left(err) =>
            Left(err)
        }
    }
  }

  def getPaymentAllocationDetails(nino: String, documentId: String)
                                 (implicit hc: HeaderCarrier): Future[ChargeAsJsonResponse] = {
    if (isHipOn){
      hipConnector.getPaymentAllocationDetails(nino, documentId)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
    } else {
      connector.getPaymentAllocationDetails(nino, documentId)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
    }
  }

  def getOnlyOpenItems(nino: String)(implicit hc: HeaderCarrier): Future[ChargeAsJsonResponse] = {
    if (isHipOn){
      hipConnector.getOnlyOpenItems(nino)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
    } else {
      connector.getOnlyOpenItems(nino)
        .collect{
          case Right(charges) =>
            Right(Json.toJson(charges))
          case Left(err) =>
            Left(err)
        }
    }
  }

  def getCreditsModel(nino: String, fromDate: String, toDate: String)
                     (implicit hc: HeaderCarrier) : Future[ChargeAsJsonResponse] = {
    if (isHipOn) {
      hipConnector
        .getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            val creditsModel: CreditsModel = CreditsModel.fromHipChargesResponse(charges)
            Right(Json.toJson(creditsModel))
          case Left(err) =>
            Left(err)
        }
    } else {
      connector.getChargeDetails(nino, fromDate, toDate)
        .collect{
          case Right(charges) =>
            val creditsModel: CreditsModel = CreditsModel.fromChargesResponse(charges)
            Right(Json.toJson(creditsModel))
          case Left(err) =>
            Left(err)
        }
    }
  }
}

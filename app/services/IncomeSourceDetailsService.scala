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

package services

import config.MicroserviceAppConfig
import connectors.hip.GetBusinessDetailsConnector
import models.core.{NinoErrorModel, NinoModel}
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSourceDetailsService @Inject()(val getBusinessDetailsFromHipConnector: GetBusinessDetailsConnector,
                                           val appConfig: MicroserviceAppConfig
                                          )
                                          (implicit ec: ExecutionContext) extends Logging {

  def getIncomeSourceDetails(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[Result] = {
    getBusinessDetailsFromHipConnector.getBusinessDetails(mtdRef, models.hip.incomeSourceDetails.MtdId).map {
      case success: models.hip.incomeSourceDetails.IncomeSourceDetailsModel =>
        logger.debug(s"Retrieved Income Source Details:\n\n$success")
        Status(OK)(Json.toJson(success))
      case error: models.hip.incomeSourceDetails.IncomeSourceDetailsError =>
        logger.error(s"Retrieved error Income Source Details:\n\n$error")
        Status(error.status)(Json.toJson(error))
      case notFound: models.hip.incomeSourceDetails.IncomeSourceDetailsNotFound =>
        logger.warn(s"Retrieved not found Income Source Details: \n\n$notFound")
        Status(NOT_FOUND)(Json.toJson(notFound))
    }
  }

  def getNino(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[Result] = {
    getBusinessDetailsFromHipConnector.getBusinessDetails(mtdRef, models.hip.incomeSourceDetails.MtdId).map {
      case success: models.hip.incomeSourceDetails.IncomeSourceDetailsModel =>
        logger.debug("Converting to Nino Model")
        Status(OK)(Json.toJson(NinoModel(success.nino)))
      case error: models.hip.incomeSourceDetails.IncomeSourceDetailsError =>
        logger.error(s"Error Response: $error")
        Status(error.status)(Json.toJson(NinoErrorModel(error.status, error.reason)))
      case notFound: models.hip.incomeSourceDetails.IncomeSourceDetailsNotFound =>
        logger.warn(s"Income tax details not found: $notFound")
        Status(NOT_FOUND)(Json.toJson(NinoErrorModel(notFound.status, notFound.reason)))
    }
  }
}

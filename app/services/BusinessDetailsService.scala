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
import connectors.BusinessDetailsConnector
import connectors.hip.GetBusinessDetailsConnector
import models.hip.GetBusinessDetailsHipApi
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Status
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BusinessDetailsService @Inject()(val businessDetailsConnector: BusinessDetailsConnector,
                                       val getBusinessDetailsFromHipConnector: GetBusinessDetailsConnector,
                                       val appConfig: MicroserviceAppConfig
                                      ) extends Logging {

  def getBusinessDetails(nino: String)
                        (implicit headerCarrier: HeaderCarrier,
                         ec:ExecutionContext): Future[Result] = {
    logger.debug("Requesting Income Source Details from Connector")
    if(appConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi))
      getBusinessDetailsFromHipConnector.getBusinessDetails(nino, models.hip.incomeSourceDetails.Nino).map {
        case success: models.hip.incomeSourceDetails.IncomeSourceDetailsModel =>
          Status(OK)(Json.toJson(success))
        case notFound: models.hip.incomeSourceDetails.IncomeSourceDetailsNotFound =>
          logger.warn(s"Income tax details not found: $notFound")
          Status(notFound.status)(Json.toJson(notFound))
        case error:models.hip.incomeSourceDetails.IncomeSourceDetailsError =>
          logger.error(s"Error Response: $error")
          Status(error.status)(Json.toJson(error))
      }
    else
      businessDetailsConnector.getBusinessDetails(nino, models.incomeSourceDetails.Nino).map {
        case success: models.incomeSourceDetails.IncomeSourceDetailsModel =>
          Status(OK)(Json.toJson(success))
        case notFound: models.incomeSourceDetails.IncomeSourceDetailsNotFound =>
          logger.warn(s"Income tax details not found: $notFound")
          Status(notFound.status)(Json.toJson(notFound))
        case error: models.incomeSourceDetails.IncomeSourceDetailsError =>
          logger.error(s"Error Response: $error")
          Status(error.status)(Json.toJson(error))
      }
  }
}

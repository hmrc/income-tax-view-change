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

import connectors.IncomeSourceDetailsConnector
import models.core.{NinoErrorModel, NinoModel, NinoResponse}
import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel, IncomeSourceDetailsNotFound, IncomeSourceDetailsResponseModel}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSourceDetailsService @Inject()(
                                            val incomeSourceDetailsConnector: IncomeSourceDetailsConnector)
                                          (implicit ec: ExecutionContext)extends Logging {

  def getIncomeSourceDetails(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {
    logger.debug("[IncomeSourceDetailsService][getIncomeSourceDetails] - Requesting Income Source Details from Connector")
    incomeSourceDetailsConnector.getIncomeSourceDetails(mtdRef).map {
      case success: IncomeSourceDetailsModel =>
        logger.debug(s"[IncomeSourceDetailsService][getIncomeSourceDetails] - Retrieved Income Source Details:\n\n$success")
        logger.debug("[IncomeSourceDetailsService][getIncomeSourceDetails] - Converting to IncomeSourceDetails Model")
        success
      case error: IncomeSourceDetailsError =>
        logger.error(s"[IncomeSourceDetailsService][getIncomeSourceDetails] - Retrieved Income Source Details:\n\n$error")
        IncomeSourceDetailsError(error.status, error.reason)
      case notFound: IncomeSourceDetailsNotFound =>
        logger.warn(s"[IncomeSourceDetailsService][getIncomeSourceDetails] - Retrieved Income Source Details: \n\n$notFound")
        IncomeSourceDetailsNotFound(notFound.status, notFound.reason)
    }
  }

  def getNino(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[NinoResponse] = {
    getIncomeSourceDetails(mtdRef).map {
      case success: IncomeSourceDetailsModel =>
        logger.debug("[IncomeSourceDetailsService][getNino] - Converting to Nino Model")
        NinoModel(success.nino)
      case error: IncomeSourceDetailsError => NinoErrorModel(error.status, error.reason)
      case notFound: IncomeSourceDetailsNotFound =>
        logger.warn(s"[IncomeSourceDetailsService][getNino] - Income tax details not found: $notFound")
        NinoErrorModel(notFound.status, notFound.reason)
    }
  }

}

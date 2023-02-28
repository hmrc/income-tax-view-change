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

import connectors.GetBusinessDetailsConnector
import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel, IncomeSourceDetailsNotFound, IncomeSourceDetailsResponseModel}
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class GetBusinessDetailsService @Inject()(val getBusinessDetailsConnector: GetBusinessDetailsConnector) extends Logging {

  def getBusinessDetails(nino: String)(implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {
    logger.debug("[getBusinessDetailsService][getBusinessDetails] - Requesting Income Source Details from Connector")
    getBusinessDetailsConnector.getBusinessDetails(nino).map {
      case success: IncomeSourceDetailsModel =>
        logger.debug(s"[getBusinessDetailsService][getBusinessDetails] - Retrieved Get Business Details:\n\n$success")
        success
      case notFound: IncomeSourceDetailsNotFound =>
        logger.warn(s"[getBusinessDetailsService][getgetBusinessDetails] - Income Source Details not found:\n\n$notFound")
        IncomeSourceDetailsError(notFound.status, notFound.reason)
      case error: IncomeSourceDetailsError =>
        logger.error(s"[getBusinessDetailsService][getgetBusinessDetails] - Retrieved Income Source Details:\n\n$error")
        IncomeSourceDetailsError(error.status, error.reason)
    }
  }

}

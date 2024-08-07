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

import connectors.BusinessDetailsConnector
import models.incomeSourceDetails.{Nino, IncomeSourceDetailsResponseModel}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class BusinessDetailsService @Inject()(val businessDetailsConnector: BusinessDetailsConnector) extends Logging {

  def getBusinessDetails(nino: String)(implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {
    logger.debug("Requesting Income Source Details from Connector")
    businessDetailsConnector.getBusinessDetails(nino, Nino)
  }

}

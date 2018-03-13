/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import connectors.IncomeSourceDetailsConnector
import models._
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NinoLookupService @Inject()(val ninoLookupConnector: IncomeSourceDetailsConnector) {

  def getNino(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {
    Logger.debug("[NinoLookupService][getNino] - Requesting Des Business Details from Connector")
    ninoLookupConnector.getIncomeSourceDetails(mtdRef).map[IncomeSourceDetailsResponseModel] {
      case success: IncomeSourceDetailsModel =>
        Logger.debug(s"[NinoLookupService][getNino] - Retrieved Des Business Details:\n\n$success")
        Logger.debug(s"[NinoLookupService][getNino] - Converting to Nino Response Model")
        NinoModel(success.nino)
      case error: IncomeSourceDetailsError =>
        IncomeSourceDetailsError(error.status, error.reason)
    }
  }
}

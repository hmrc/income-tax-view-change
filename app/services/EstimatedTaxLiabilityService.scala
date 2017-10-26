/*
 * Copyright 2017 HM Revenue & Customs
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

import connectors.FinancialDataConnector
import models._
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class EstimatedTaxLiabilityService @Inject()(val financialDataConnector: FinancialDataConnector) {

  def getEstimatedTaxLiability(nino: String, year: String, calcType: String)
                              (implicit headerCarrier: HeaderCarrier): Future[LastTaxCalculationResponseModel] = {
    Logger.debug("[EstimatedTaxLiabilityService][getEstimateTaxLiability] - Requesting Last Tax Calculation from Connector")
    financialDataConnector.getLastEstimatedTaxCalculation(nino, year, calcType).map[LastTaxCalculationResponseModel] {
      case success: LastTaxCalculation =>
        Logger.debug(s"[EstimatedTaxLiabilityService][getEstimateTaxLiability] - Retrieved Financial Data:\n\n$success")
        //TODO for now just return the Last Calculation amount.  Add additional calc breakdown data
        success
      case error: LastTaxCalculationError =>
        LastTaxCalculationError(error.status, error.message)
    }
  }
}

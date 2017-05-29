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
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EstimatedTaxLiabilityService @Inject()(val financialDataConnector: FinancialDataConnector) {

  def getEstimatedTaxLiability(mtditid: String)(implicit headerCarrier: HeaderCarrier): Future[EstimatedTaxLiabilityResponseModel] = {
    Logger.debug("[EstimatedTaxLiabilityService][getEstimateTaxLiability] - Requesting Financial Data from Connector")
    financialDataConnector.getFinancialData(mtditid).map[EstimatedTaxLiabilityResponseModel] {
      case success: FinancialData =>
        Logger.debug(s"[EstimatedTaxLiabilityService][getEstimateTaxLiability] - Retrieved Finacial Data:\n\n${success.financialData}")
        createEstimatedTaxLiabilityResponse(success.financialData)
      case error: FinancialDataError =>
        EstimatedTaxLiabilityError(error.status, error.message)
    }
  }

  // TODO: This assumes a simplistic JSON response. This will need to be replaced with the API#26 response from DES
  private[EstimatedTaxLiabilityService] def createEstimatedTaxLiabilityResponse(financialData: JsValue): EstimatedTaxLiability = {

    val nic2: BigDecimal = (financialData \ "nic2").as[BigDecimal]
    val nic4: BigDecimal = (financialData \ "nic4").as[BigDecimal]
    val incomeTax: BigDecimal = (financialData \ "incomeTax").as[BigDecimal]

    val total: BigDecimal = nic2 + nic4 + incomeTax

    EstimatedTaxLiability(
      total = total,
      nic2 = nic2,
      nic4 = nic4,
      incomeTax = incomeTax
    )
  }
}

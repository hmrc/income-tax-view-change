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

import connectors.CalculationListConnector
import connectors.httpParsers.CalculationListHttpParser.HttpGetResult
import models.calculationList.CalculationListResponseModel
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationListService @Inject()(val calculationListConnector: CalculationListConnector) extends Logging {

  lazy val TAX_YEAR_2026: Int = 2026

  def getCalculationListTYS(nino: String, taxYearRange: String)
                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {

    logger.info(s"Calling calculationListConnector with Nino: $nino\nTax Year: $taxYearRange")
    val calculationListResponse = if(getTaxYearEnd(taxYearRange) < TAX_YEAR_2026) {
      calculationListConnector.getCalculationListTYS(nino, taxYearRange)
    } else {
      calculationListConnector.getCalculationList2083(nino, taxYearRange)
    }
    calculationListResponse.map {
      case success@Right(calculationListResponse: CalculationListResponseModel) =>
        logger.info(s"Retrieved Calculation List TYS Data:\n\n$calculationListResponse")
        success
      case error@Left(_) =>
        error
    }
  }

  private def getTaxYearEnd(taxYearRange: String): Int = {
    val taxYearEndString: String = taxYearRange.trim.takeRight(2)
    taxYearEndString.toInt + 2000
  }



}
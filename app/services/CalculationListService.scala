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
import connectors.httpParsers.PreviousCalculationHttpParser.HttpGetResult
import models.calculationList.CalculationListResponseModel
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationListService @Inject()(val calculationListConnector: CalculationListConnector) extends Logging {

  def getCalculationList(nino: String, taxYearEnd: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {

    logger.debug(s"[CalculationListService][getCalculationList] Calling calculationListConnector with Nino: $nino\nTax Year: $taxYearEnd")
    calculationListConnector.getCalculationList(nino, taxYearEnd).map {
      case success@Right(calculationListResponse: CalculationListResponseModel) =>
        logger.debug(s"[CalculationListService][getCalculationList] - Retrieved Calculation List Data:\n\n$calculationListResponse")
        success
      case error@Left(_) =>
        error
    }
  }

  def getCalculationList2324(nino: String, taxYearRange: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {

    logger.debug(s"[CalculationListService][getCalculationList] Calling calculationListConnector with Nino: $nino\nTax Year: $taxYearRange")
    calculationListConnector.getCalculationList2324(nino, taxYearRange).map {
      case success@Right(calculationListResponse: CalculationListResponseModel) =>
        logger.debug(s"[CalculationListService][getCalculationList] - Retrieved Calculation List TYS Data:\n\n$calculationListResponse")
        success
      case error@Left(_) =>
        error
    }
  }
}
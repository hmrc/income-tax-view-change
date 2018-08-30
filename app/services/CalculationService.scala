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

import connectors.CalculationConnector
import javax.inject.{Inject, Singleton}
import models.PreviousCalculation.{ErrorResponse, PreviousCalculationModel}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationService @Inject()(val calculationConnector: CalculationConnector) {

  def getPreviousCalculation(nino: String, year: String)
                            (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, PreviousCalculationModel]] = {

    Logger.debug(s"[VatReturnsService][getVatReturns] Calling vatReturnsConnector with Nino: $nino\nYear: $year")
    calculationConnector.getPreviousCalculation(nino, year).map {
      case success@Right(vatReturns) =>
        Logger.debug(s"[CalculationService][getPreviousCalculation] - Retrieved Previous Calculation Data:\n\n$success")
        success
      case error@Left(_) =>
        error
    }
  }
}

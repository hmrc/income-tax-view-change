/*
 * Copyright 2019 HM Revenue & Customs
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

package models.PreviousCalculation

import play.api.libs.json.{Json, OFormat}

case class EoyEstimate(incomeTaxNicAmount: BigDecimal)

case class CalcResult(incomeTaxNicYtd: BigDecimal,
                      eoyEstimate: Option[EoyEstimate] = None)

case class PreviousCalculationModel(calcOutput: CalcOutput)

case class CalcOutput(calcID: String,
                      calcAmount: Option[BigDecimal] = None,
                      calcTimestamp: Option[String] = None,
                      crystallised: Option[Boolean] = None,
                      calcResult: Option[CalcResult] = None)

object EoyEstimate {
  implicit val format: OFormat[EoyEstimate] = Json.format[EoyEstimate]
}

object CalcResult {
  implicit val format: OFormat[CalcResult] = Json.format[CalcResult]
}

object PreviousCalculationModel {
  implicit val format: OFormat[PreviousCalculationModel] = Json.format[PreviousCalculationModel]
}

object CalcOutput {
  implicit val format: OFormat[CalcOutput] = Json.format[CalcOutput]
}

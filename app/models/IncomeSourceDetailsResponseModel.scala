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

package models

import play.api.libs.json.{Json, OFormat}

sealed trait IncomeSourceDetailsResponseModel

case class NinoModel(nino: String) extends IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsModel(nino: String,
                                    propertyIncome: Option[Boolean],
                                    businessData: Option[List[BusinessDetailsModel]],
                                    propertyData: Option[PropertyDetailsModel]) extends IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsError(status: Int, reason: String) extends IncomeSourceDetailsResponseModel

object NinoModel {
  implicit val format: OFormat[NinoModel] = Json.format[NinoModel]
}

object IncomeSourceDetailsModel {
  implicit val bFormat: OFormat[BusinessDetailsModel] = BusinessDetailsModel.format
  implicit val pFormat: OFormat[PropertyDetailsModel] = PropertyDetailsModel.format
  implicit val format: OFormat[IncomeSourceDetailsModel] = Json.format[IncomeSourceDetailsModel]
}

object IncomeSourceDetailsError {
  implicit val format: OFormat[IncomeSourceDetailsError] = Json.format[IncomeSourceDetailsError]
}
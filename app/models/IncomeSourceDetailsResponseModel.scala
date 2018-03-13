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

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}

sealed trait IncomeSourceDetailsResponseModel

case class NinoModel(nino: String) extends IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsModel(nino: String,
                                    businessData: List[BusinessDetailsModel],
                                    propertyData: Option[PropertyDetailsModel]) extends IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsError(status: Int, reason: String) extends IncomeSourceDetailsResponseModel

object NinoModel {
  implicit val format: OFormat[NinoModel] = Json.format[NinoModel]
}

object IncomeSourceDetailsModel {

  def applyWithFields(nino: String,
           businessData: Option[List[BusinessDetailsModel]],
           propertyData: Option[PropertyDetailsModel]): IncomeSourceDetailsModel = {

    val businessDetails = businessData match {
      case Some(data) => data
      case None => List()
    }
    IncomeSourceDetailsModel(
      nino,
      businessDetails,
      propertyData
    )
  }

  implicit val reads: Reads[IncomeSourceDetailsModel] = (
    (__ \ "nino").read[String] and
      (__ \ "businessData").readNullable[List[BusinessDetailsModel]] and
      (__ \ "propertyData").readNullable[PropertyDetailsModel]
    )(IncomeSourceDetailsModel.applyWithFields _)

  implicit val writes: Writes[IncomeSourceDetailsModel] = Json.writes[IncomeSourceDetailsModel]

}

object IncomeSourceDetailsError {
  implicit val format: OFormat[IncomeSourceDetailsError] = Json.format[IncomeSourceDetailsError]
}
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

package models.incomeSourceDetails

import play.api.libs.functional.syntax._
import play.api.libs.json._

sealed trait IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsModel(
                                     nino: String,
                                     mtdbsa: String,
                                     yearOfMigration: Option[String],
                                     businesses: List[BusinessDetailsModel],
                                     properties: List[PropertyDetailsModel]
                                   ) extends IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsError(status: Int, reason: String) extends IncomeSourceDetailsResponseModel

case class IncomeSourceDetailsNotFound(status: Int, reason: String) extends IncomeSourceDetailsResponseModel

object IncomeSourceDetailsModel {

  def applyWithFields(nino: String,
                      mtdbsa: String,
                      yearOfMigration: Option[String],
                      businessData: Option[List[BusinessDetailsModel]],
                      propertyData: Option[List[PropertyDetailsModel]]): IncomeSourceDetailsModel = {
    val businessDetails = businessData match {
      case Some(data) => data
      case None => List()
    }
    val propertyDetails = propertyData match {
      case Some(data) => data
      case None => List()
    }
    IncomeSourceDetailsModel(
      nino,
      mtdbsa,
      yearOfMigration,
      businessDetails,
      propertyDetails
    )
  }

  implicit val ifReads: Reads[IncomeSourceDetailsModel] = (
    (__ \ "taxPayerDisplayResponse" \ "nino").read[String] and
      (__ \ "taxPayerDisplayResponse" \ "mtdId").read[String] and
      (__ \ "taxPayerDisplayResponse" \ "yearOfMigration").readNullable[String] and
      (__ \ "taxPayerDisplayResponse" \ "businessData").readNullable(Reads.list(BusinessDetailsModel.reads)) and
      (__ \ "taxPayerDisplayResponse" \ "propertyData").readNullable(Reads.list(PropertyDetailsModel.reads))
    )(IncomeSourceDetailsModel.applyWithFields _)


  implicit val writes: OWrites[IncomeSourceDetailsModel] = Json.writes[IncomeSourceDetailsModel]
}

object IncomeSourceDetailsError {
  implicit val format: Format[IncomeSourceDetailsError] = Json.format[IncomeSourceDetailsError]
}

object IncomeSourceDetailsNotFound {
  implicit val format: Format[IncomeSourceDetailsNotFound] = Json.format[IncomeSourceDetailsNotFound]
}
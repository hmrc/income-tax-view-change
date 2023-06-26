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

package models.calculationList

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class CalculationListModel(calculationId: String,
                                calculationTimestamp: String,
                                calculationType: String,
                                finalDeclaration: Option[Boolean])

object CalculationListModel {
  implicit val writes: Writes[CalculationListModel] = Json.writes[CalculationListModel]
  implicit val reads: Reads[CalculationListModel] =
    ((JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String] and
      (JsPath \ "calculationType").read[String] and
      (JsPath \ "finalDeclaration").readNullable[Boolean]
      ) (CalculationListModel.apply _)
}

case class CalculationListResponseModel(calculations: Seq[CalculationListModel])

object CalculationListResponseModel {
  implicit val writes: Writes[CalculationListResponseModel] = Json.writes[CalculationListResponseModel]
  implicit val reads: Reads[CalculationListResponseModel] =
    implicitly[Reads[Seq[CalculationListModel]]].map(CalculationListResponseModel(_))

}

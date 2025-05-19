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

import play.api.libs.json._

trait CreateBusinessDetailsResponseModel

object CreateBusinessDetailsResponseModel {

final case class IncomeSource(incomeSourceId: String) {
  def toHipModel: models.hip.incomeSourceDetails.IncomeSource = models.hip.incomeSourceDetails.IncomeSource(incomeSourceId)
}
final case class CreateBusinessDetailsModel(response: List[IncomeSource])

  object IncomeSource {
    implicit val formats: OFormat[IncomeSource] = Json.format[IncomeSource]
  }

  object CreateBusinessDetailsModel {
    implicit val formats: OFormat[CreateBusinessDetailsModel] = Json.format[CreateBusinessDetailsModel]
  }

  final case class CreateBusinessDetailsErrorResponse(status: Int, reason: String) extends CreateBusinessDetailsResponseModel {
    def toHipModel: models.hip.incomeSourceDetails.CreateBusinessDetailsHipErrorResponse = {
      models.hip.incomeSourceDetails.CreateBusinessDetailsHipErrorResponse(status, reason)
    }
  }

  object CreateBusinessDetailsErrorResponse {
    implicit val formats: OFormat[CreateBusinessDetailsErrorResponse] = Json.format[CreateBusinessDetailsErrorResponse]
  }

}

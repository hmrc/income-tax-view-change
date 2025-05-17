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

package models.hip.incomeSourceDetails

import play.api.libs.json.{Json, OFormat}


trait CreateBusinessDetailsHipResponseModel

case class CreateBusinessDetailsHipModel(success: IncomeSourceIdDetails) extends CreateBusinessDetailsHipResponseModel

object CreateBusinessDetailsHipModel {
  implicit val format: OFormat[CreateBusinessDetailsHipModel] = Json.format[CreateBusinessDetailsHipModel]
}

case class IncomeSource(incomeSourceId: String)

object IncomeSource {
  implicit val formats: OFormat[IncomeSource] = Json.format[IncomeSource]
}


case class IncomeSourceIdDetails(incomeSourceIdDetails: List[IncomeSource])

object IncomeSourceIdDetails {
  implicit val formats: OFormat[IncomeSourceIdDetails] = Json.format[IncomeSourceIdDetails]
}



case class CreateBusinessDetailsHipErrorResponse(status: Int, reason: String) extends CreateBusinessDetailsHipResponseModel

object CreateBusinessDetailsHipErrorResponse {
  implicit val formats: OFormat[CreateBusinessDetailsHipErrorResponse] = Json.format[CreateBusinessDetailsHipErrorResponse]
}


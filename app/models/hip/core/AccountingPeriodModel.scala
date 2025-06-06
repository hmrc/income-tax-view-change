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

package models.hip.core

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class AccountingPeriodModel(start: LocalDate, end: LocalDate)

object AccountingPeriodModel {

  val reads: Reads[AccountingPeriodModel] = (
    (__ \ "accPeriodSDate").read[LocalDate] and
      (__ \ "accPeriodEDate").read[LocalDate]
    ) (AccountingPeriodModel.apply _)

  implicit val format: Format[AccountingPeriodModel] = Json.format[AccountingPeriodModel]
}
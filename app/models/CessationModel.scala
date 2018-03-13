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

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}

case class CessationModel(date: Option[LocalDate], reason: Option[String])

object CessationModel {

  implicit val reads: Reads[CessationModel] = (
    (__ \ "cessationDate").readNullable[LocalDate] and
      (__ \ "cessationReason").readNullable[String]
  )(CessationModel.apply _)

  def cessation(date: Option[LocalDate], reason: Option[String]): Option[CessationModel] =
    (date,reason) match{
      case (None,None) => None
      case _ => Some(CessationModel(date,reason))
    }
  implicit val writes: Writes[CessationModel] = Json.writes[CessationModel]
}

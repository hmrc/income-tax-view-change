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

case class PropertiesRentedModel(uk:Option[Int], eea:Option[Int], nonEea:Option[Int], total:Option[Int])

object PropertiesRentedModel {

  implicit val reads: Reads[PropertiesRentedModel] = (
    (__ \ "numPropRentedUK").readNullable[Int] and
      (__ \ "numPropRentedEEA").readNullable[Int] and
      (__ \ "numPropRentedNONEEA").readNullable[Int] and
      (__ \ "numPropRented").readNullable[Int]
  )(PropertiesRentedModel.apply _)

  def propertiesRented(uk:Option[Int], eea:Option[Int], nonEea:Option[Int], total:Option[Int]): Option[PropertiesRentedModel] = {
    (uk,eea,nonEea,total) match {
      case (None,None,None,None) => None
      case _ => Some(PropertiesRentedModel(uk,eea,nonEea,total))
    }
  }

  implicit val writes: Writes[PropertiesRentedModel] = Json.writes[PropertiesRentedModel]

}
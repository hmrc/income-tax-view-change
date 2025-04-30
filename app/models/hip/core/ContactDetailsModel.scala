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

case class ContactDetailsModel(phoneNumber: Option[String],
                               mobileNumber: Option[String],
                               faxNumber: Option[String],
                               emailAddress: Option[String])

object ContactDetailsModel {

  val reads: Reads[ContactDetailsModel] = (
    (__ \ "telephone").readNullable[String] and
      (__ \ "mobileNo").readNullable[String] and
      (__ \ "faxNo").readNullable[String] and
      (__ \ "email").readNullable[String]
    ) (ContactDetailsModel.apply _)


  def propertyContactDetails(email: Option[String]): Option[ContactDetailsModel] =
    email match {
      case None => None
      case _ => Some(ContactDetailsModel(None, None, None, email))
    }

  implicit val format: Format[ContactDetailsModel] = Json.format[ContactDetailsModel]

}
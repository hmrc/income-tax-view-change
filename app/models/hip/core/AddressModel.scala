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

case class AddressModel(addressLine1: Option[String],
                        addressLine2: Option[String],
                        addressLine3: Option[String],
                        addressLine4: Option[String],
                        postCode: Option[String],
                        countryCode: Option[String])

object AddressModel {

  val reads: Reads[AddressModel] = (
    (__ \ "addressLine1").readNullable[String] and
      (__ \ "addressLine2").readNullable[String] and
      (__ \ "addressLine3").readNullable[String] and
      (__ \ "addressLine4").readNullable[String] and
      (__ \ "postalCode").readNullable[String] and
      (__ \ "countryCode").readNullable[String]
    ) (AddressModel.apply _)

  implicit val format: Format[AddressModel] = Json.format[AddressModel]

}


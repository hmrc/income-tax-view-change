/*
 * Copyright 2020 HM Revenue & Customs
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

package models.financialDetails

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Charge(taxYear: Option[String],
                  transactionId: Option[String],
                  transactionDate: Option[String],
                  `type`: Option[String],
                  totalAmount: Option[BigDecimal],
                  outstandingAmount: Option[BigDecimal],
                  items: Option[Seq[SubItem]]
                 )

object Charge {

  implicit val reads: Reads[Charge] = (
    (JsPath \ "taxYear").readNullable[String] and
      (JsPath \ "documentId").readNullable[String] and
      (JsPath \ "documentDate").readNullable[String] and
      (JsPath \ "documentDescription").readNullable[String] and
      (JsPath \ "totalAmount").readNullable[BigDecimal] and
      (JsPath \ "documentOutstandingAmount").readNullable[BigDecimal] and
      (JsPath \ "items").readNullable[Seq[SubItem]]
    ) (Charge.apply _)

  implicit val writes: OWrites[Charge] = Json.writes[Charge]

}

/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.libs.json.{JsPath, Json, Reads, Writes}

import java.time.LocalDate

case class CodingDetails(coded: Option[Seq[CodedEntry]],
                         amountCodedOut: Option[BigDecimal])

case class CodedEntry(amount: BigDecimal,
                      initiationDate: LocalDate)


object CodingDetails {
  implicit val writes: Writes[CodingDetails] = Json.writes[CodingDetails]
  implicit val reads: Reads[CodingDetails] = (
      (JsPath \ "coded").readNullable[Seq[CodedEntry]] and
        (JsPath \ "amountCodedOut").readNullable[BigDecimal]
    ) (CodingDetails.apply _)
}

object CodedEntry {
  implicit val writes: Writes[CodedEntry] = Json.writes[CodedEntry]
  implicit val reads: Reads[CodedEntry] = (
    (JsPath \ "amount").read[BigDecimal] and
      (JsPath \ "initiationDate").read[LocalDate]
    ) (CodedEntry.apply _)
}

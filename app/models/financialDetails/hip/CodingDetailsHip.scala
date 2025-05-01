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

package models.financialDetails.hip.model

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class CodingDetailsHip(
                             /* Format: YYYY */
                             taxYearReturn: Option[String] = None,
                             /* Currency amount. 13-digits total with 2 decimal places */
                             amountCodedOut: Option[BigDecimal] = None, // renamed to fit FE from totalLiabilityAmount
                             /* Format: YYYY */
                             taxYearCoding: Option[String] = None,
                             coded: Option[Seq[CodedEntryHip]] = None //??? non-Hip version use: Option[Seq[CodedEntry]]
)

object CodingDetailsHip {
  implicit val writes: Writes[CodingDetailsHip] = Json.writes[CodingDetailsHip]

  implicit val reads: Reads[CodingDetailsHip] = (
    (JsPath \ "taxYearReturn").readNullable[String] and
    (JsPath \ "totalLiabilityAmount").readNullable[BigDecimal] and
      (JsPath \ "taxYearCoding").readNullable[String] and
      (JsPath \ "coded").readNullable[Seq[CodedEntryHip]]
    ) (CodingDetailsHip.apply _)
}


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

package models.financialDetails

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

import java.time.LocalDate

case class FinancialDetail(taxYear: String,
                           transactionId: String,
                           transactionDate: Option[LocalDate],
                           `type`: Option[String],
                           chargeReference: String,
                           totalAmount: Option[BigDecimal],
                           originalAmount: Option[BigDecimal],
                           outstandingAmount: Option[BigDecimal],
                           clearedAmount: Option[BigDecimal],
                           chargeType: Option[String],
                           mainType: Option[String],
                           mainTransaction: Option[String],
                           accruedInterest: Option[BigDecimal],
                           items: Option[Seq[SubItem]]
                          )

object FinancialDetail {

  implicit val reads: Reads[FinancialDetail] = (
    (JsPath \ "taxYear").read[String] and
      (JsPath \ "documentId").read[String] and
      (JsPath \ "documentDate").readNullable[LocalDate] and
      (JsPath \ "documentDescription").readNullable[String] and
      (JsPath \ "chargeReference").read[String] and
      (JsPath \ "totalAmount").readNullable[BigDecimal] and
      (JsPath \ "originalAmount").readNullable[BigDecimal] and
      (JsPath \ "documentOutstandingAmount").readNullable[BigDecimal] and
      (JsPath \ "clearedAmount").readNullable[BigDecimal] and
      (JsPath \ "chargeType").readNullable[String] and
      (JsPath \ "mainType").readNullable[String] and
      (JsPath \ "mainTransaction").readNullable[String] and
      (JsPath \ "accruedInterest").readNullable[BigDecimal] and
      (JsPath \ "items").readNullable[Seq[SubItem]]
    ) (FinancialDetail.apply _)

  implicit val writes: OWrites[FinancialDetail] = Json.writes[FinancialDetail]

}

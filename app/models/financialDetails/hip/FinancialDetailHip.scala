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

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import java.time.LocalDate

case class FinancialDetailHip(
                               /* Format: YYYY */
                               taxYear: String,
                               /* SAP document number or Form Bundle Number for zero amount documents */
                               transactionId: String,
                               /* Description of charge type */
                               chargeType: Option[String] = None,
                               /* Description of main type */
                               mainType: Option[String] = None,
                               /* Period Key */
                               //periodKey: Option[String] = None,
                               /* Period Key Description */
                               //`type`: Option[String] = None, // renamed to fit FE schema

                               taxPeriodFrom: Option[LocalDate] = None,
                               taxPeriodTo: Option[LocalDate] = None,
                               /* Business Partner */
                               //businessPartner: Option[String] = None,
                               /* Contract Account Category */
                               //contractAccountCategory: Option[String] = None,
                               /* Contract Account */
                               //contractAccount: Option[String] = None,
                               /* Contract Object Type */
                               //contractObjectType: Option[String] = None,
                               /* Contract Object */
                               //contractObject: Option[String] = None,
                               /* SAP Document Number */
                               //sapDocumentNumber: Option[String] = None,
                               /* SAP Document Number Item */
                               //sapDocumentNumberItem: Option[String] = None,
                               /* Charge Reference */
                               chargeReference: Option[String] = None,

                               /* Main Transaction */
                               mainTransaction: Option[String] = None,
                               /* Sub Transaction */
                               //subTransaction: Option[String] = None,
                               /* Currency amount. 13-digits total with 2 decimal places */
                               originalAmount: Option[BigDecimal] = None,
                               /* Currency amount. 13-digits total with 2 decimal places */
                               outstandingAmount: Option[BigDecimal] = None,
                               /* Currency amount. 13-digits total with 2 decimal places */
                               clearedAmount: Option[BigDecimal] = None,
                               /* Currency amount. 13-digits total with 2 decimal places */
                               accruedInterest: Option[BigDecimal] = None,
                               items: Option[Seq[SubItemHip]]
)

object FinancialDetailHip {

  implicit val reads: Reads[FinancialDetailHip] = (
    (JsPath \ "taxYear").read[String] and
      (JsPath \ "documentID").read[String] and
      (JsPath \ "chargeType").readNullable[String] and
      (JsPath \ "mainType").readNullable[String] and
      //(JsPath \ "periodKeyDescription").readNullable[String] and
      (JsPath \ "taxPeriodFrom").readNullable[LocalDate] and
      (JsPath \ "taxPeriodTo").readNullable[LocalDate] and
      (JsPath \ "chargeReference").readNullable[String] and
      (JsPath \ "mainTransaction").readNullable[String] and
      (JsPath \ "originalAmount").readNullable[BigDecimal] and
      (JsPath \ "outstandingAmount").readNullable[BigDecimal] and
      (JsPath \ "clearedAmount").readNullable[BigDecimal] and
      (JsPath \ "accruedInterest").readNullable[BigDecimal] and
      (JsPath \ "items").readNullable[Seq[SubItemHip]]
    ) (FinancialDetailHip.apply _)

  implicit val writes: OWrites[FinancialDetailHip] = Json.writes[FinancialDetailHip]

}


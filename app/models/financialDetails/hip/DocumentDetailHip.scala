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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, Reads, Writes, __}

import java.time.LocalDate

// TODO: enable disabled field after migration to Scala 3 => MISUV-7996
case class DocumentDetailHip(
                              /* Format: YYYY */
                              taxYear: Int,
                              /* SAP document number or Form Bundle Number for zero amount documents */
                              transactionId: String,
                              /* If the document was created using the Form Bundle, the FB Number is provided */
                              //formBundleNumber: Option[String] = None,
                              /* Gives the reason as to why there is a credit on the account.  */
                              //creditReason: Option[String] = None,
                              documentDate: LocalDate,
                              /* Document Text */
                              documentText: Option[String] = None,
                              documentDueDate: Option[LocalDate] = None,
                              /* Document descriptiom */
                              documentDescription: Option[String] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              originalAmount: BigDecimal, // renamed from totalAmount
                              /* Currency amount. 13-digits total with 2 decimal places */
                              outstandingAmount: BigDecimal, // renamed from documentOutstandingAmount
                              /* Currency amount. 13-digits total with 2 decimal places */
                              poaRelevantAmount: Option[BigDecimal] = None,
                              //lastClearingDate: Option[LocalDate] = None,
                              /* Last clearing reason */
                              //lastClearingReason: Option[String] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              lastClearedAmount: Option[BigDecimal] = None,
                              /* Y for Statistical. N for not */
                              //statisticalFlag: String,
                              /* Identifies a charge that has multiple items associated e.g. there are two BCD items due to coding occurring */
                              //  informationCode: Option[String] = None,
                              //  /* Payment Lot */
                              paymentLot: Option[String] = None,
                              /* Payment Lot Item */
                              paymentLotItem: Option[String] = None,
                              effectiveDateOfPayment: Option[LocalDate] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              accruingInterestAmount: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              interestRate: Option[BigDecimal] = None,
                              interestFromDate: Option[LocalDate] = None,
                              interestEndDate: Option[LocalDate] = None,
                              /* Late Payment Interets Id */
                              latePaymentInterestId: Option[String] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              latePaymentInterestAmount: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              lpiWithDunningLock: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              interestOutstandingAmount: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              amountCodedOut: Option[BigDecimal] = None
                              /* If Charge has been reduced, and credit arises, document number to be shown */
                              //documentNumberReducedCharge: Option[String] = None,
                              /* Document name of charge reduced */
                              //chargeTypeReducedCharge: Option[String] = None,
                              //amendmentDateReducedCharge: Option[LocalDate] = None,
                              /* Format: YYYY */
                              //taxYearReducedCharge: Option[String] = None
                            )


object DocumentDetailHip {
  implicit val writes: Writes[DocumentDetailHip] = Json.writes[DocumentDetailHip]
  implicit val reads: Reads[DocumentDetailHip] = (
    (__ \ "taxYear").read[String].map(_.toInt) and // <= RT conversion applied
      (__ \ "documentID").read[String] and
      (__ \ "documentDate").read[LocalDate] and
      (__ \ "documentText").readNullable[String] and
      (__ \ "documentDueDate").readNullable[LocalDate] and
      (__ \ "documentDescription").readNullable[String] and
      (__ \ "totalAmount").read[BigDecimal] and
      (__ \ "documentOutstandingAmount").read[BigDecimal] and
      (__ \ "poaRelevantAmount").readNullable[BigDecimal] and
      (__ \ "lastClearedAmount").readNullable[BigDecimal] and
      (__ \ "paymentLot").readNullable[String] and
      (__ \ "paymentLotItem").readNullable[String] and
      (__ \ "effectiveDateOfPayment").readNullable[LocalDate] and
      (__ \ "accruingInterestAmount").readNullable[BigDecimal] and
      (__ \ "interestRate").readNullable[BigDecimal] and
      (__ \ "interestFromDate").readNullable[LocalDate] and
      (__ \ "interestEndDate").readNullable[LocalDate] and
      (__ \ "latePaymentInterestID").readNullable[String] and
      (__ \ "latePaymentInterestAmount").readNullable[BigDecimal] and
      (__ \ "lpiWithDunningBlock").read[BigDecimal].map(Option(_)).orElse((__ \ "lpiWithDunningLock").readNullable[BigDecimal]) and
      (__ \ "interestOutstandingAmount").readNullable[BigDecimal] and
      (__ \ "amountCodedOut").readNullable[BigDecimal]
    )(DocumentDetailHip.apply _)
}


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

import models.financialDetails.Payment
import models.readNullableList
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ChargesHipResponse(
                               taxpayerDetails: TaxpayerDetailsHip,
                               balanceDetails: BalanceDetailsHip,
                               /* Coding Details */
                               codingDetails: List[CodingDetailsHip],
                               /* Document Details */
                               documentDetails: List[DocumentDetailHip],
                               /* Financial Details Item */
                               financialDetails: List[FinancialDetailHip]
                             ) {
    val payments: List[Payment] = {
      val paymentDocuments: List[DocumentDetailHip] = documentDetails.filter(document => document.originalAmount < 0).toList
      paymentDocuments.map { document =>
        val subItem = {
          if (document.paymentLot.isDefined && document.paymentLotItem.isDefined) {
            financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
              _.items.map(x => x.find(
                item => item.paymentLot.exists(_.equals(document.paymentLot.get)) && item.paymentLotItem.exists(_.equals(document.paymentLotItem.get))
              ))).flatten
          } else {
            financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
              _.items.map(_.find(
                item => item.dueDate.isDefined
              ))).flatten
          }
        }

        val mainType: Option[String] = financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
          _.mainType
        )

        val mainTransaction: Option[String] = financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
          _.mainTransaction
        )

        Payment(
          reference = subItem.flatMap(_.paymentReference),
          amount = document.originalAmount,
          outstandingAmount = document.originalAmount,
          documentDescription = document.documentDescription,
          method = subItem.flatMap(_.paymentMethod),
          lot = document.paymentLot.fold(subItem.flatMap(_.paymentLot))(Some(_)),
          lotItem = document.paymentLotItem.fold(subItem.flatMap(_.paymentLotItem))(Some(_)),
          dueDate = document.effectiveDateOfPayment,
          documentDate = document.documentDate,
          transactionId = document.transactionId,
          mainType = mainType,
          mainTransaction = mainTransaction
        )
      }
    }
}

object ChargesHipResponse {
  implicit val writes: Writes[ChargesHipResponse] = Json.writes[ChargesHipResponse]
  implicit val reads: Reads[ChargesHipResponse] = (
    (__ \ "success" \ "taxpayerDetails").read[TaxpayerDetailsHip] and
      (__ \ "success" \ "balanceDetails").read[BalanceDetailsHip] and
      readNullableList[CodingDetailsHip](__ \ "success" \ "codingDetails") and
      readNullableList[DocumentDetailHip](__ \ "success" \ "documentDetails") and
      readNullableList[FinancialDetailHip](__ \ "success" \ "financialDetailsItem")
    )(ChargesHipResponse.apply _)

}
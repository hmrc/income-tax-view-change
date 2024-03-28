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

package models.financialDetails.responses

import models.financialDetails.{BalanceDetails, DocumentDetail, FinancialDetail, Payment}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import models.readNullableList

case class ChargesResponse(balanceDetails: BalanceDetails,
                           documentDetails: List[DocumentDetail],
                           financialDetails: List[FinancialDetail]) {

  val payments: List[Payment] = {
    val paymentDocuments: List[DocumentDetail] = documentDetails.filter(document => document.originalAmount.exists(_ < 0))
    paymentDocuments.map { document =>
      val subItem = {
        if (document.paymentLot.isDefined && document.paymentLotItem.isDefined) {
          financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
            _.items.map(_.find(
              item => item.paymentLot.exists(_.equals(document.paymentLot.get)) && item.paymentLotItem.exists(_.equals(document.paymentLotItem.get))
            ))).flatten
        } else {
          financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
            _.items.map(_.find(
              item => item.dueDate.isDefined
            ))).flatten
        }
      }

      val mainType = financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
        _.mainType
      )

      val mainTransaction = financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
        _.mainTransaction
      )

      Payment(
        reference = subItem.flatMap(_.paymentReference),
        amount = document.originalAmount,
        outstandingAmount = document.outstandingAmount,
        documentDescription = document.documentDescription,
        method = subItem.flatMap(_.paymentMethod),
        lot = document.paymentLot,
        lotItem = document.paymentLotItem,
        dueDate = document.effectiveDateOfPayment,
        documentDate = document.documentDate,
        transactionId = document.transactionId,
        mainType = mainType,
        mainTransaction = mainTransaction
      )
    }
  }
}

object ChargesResponse {

  implicit val writes: Writes[ChargesResponse] = Json.writes[ChargesResponse]
  implicit val reads: Reads[ChargesResponse] = (
    (__ \ "balanceDetails").read[BalanceDetails] and
      readNullableList[DocumentDetail](__ \ "documentDetails") and
      readNullableList[FinancialDetail](__ \ "financialDetails")
    ) (ChargesResponse.apply _)

}

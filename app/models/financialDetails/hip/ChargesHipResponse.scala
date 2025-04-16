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

case class ChargesHipResponse(
                               taxpayerDetails: TaxpayerDetailsHip,
                               balanceDetails: BalanceDetailsHip,
                               /* Coding Details */
                               codingDetails: Option[Seq[CodingDetailsHip]] = None,
                               /* Document Details */
                               documentDetails: Seq[DocumentDetailHip],
                               /* Financial Details Item */
                               financialDetails: Seq[FinancialDetailHip]
) {
  val payments: List[Payment] = {
    val paymentDocuments: List[DocumentDetailHip] = documentDetails.filter(document => document.totalAmount < 0).toList
    paymentDocuments.map { document =>
      val subItem = {
        if (document.paymentLot.isDefined && document.paymentLotItem.isDefined) {
          financialDetails.find(_.documentID.equals(document.documentID)).flatMap(
            _.items.map(x => x.find(
              item => item.paymentLot.exists(_.equals(document.paymentLot.get)) && item.paymentLotItem.exists(_.equals(document.paymentLotItem.get))
            ))).flatten
        } else {
          financialDetails.find(_.documentID.equals(document.documentID)).flatMap(
            _.items.map(_.find(
              item => item.dueDate.isDefined
            ))).flatten
        }
      }

      val mainType: Option[String] = financialDetails.find(_.documentID.equals(document.documentID)).flatMap(
        _.mainType
      )

      val mainTransaction: Option[String] = financialDetails.find(_.documentID.equals(document.documentID)).flatMap(
        _.mainTransaction
      )

      Payment(
        reference = subItem.flatMap(_.paymentReference),
        amount = document.totalAmount,
        outstandingAmount = document.totalAmount,
        documentDescription = document.documentDescription,
        method = subItem.flatMap(_.paymentMethod),
        lot = document.paymentLot.fold(subItem.flatMap(_.paymentLot))(Some(_)),
        lotItem = document.paymentLotItem.fold(subItem.flatMap(_.paymentLotItem))(Some(_)),
        dueDate = document.effectiveDateOfPayment,
        documentDate = document.documentDate,
        transactionId = document.documentID,
        mainType = mainType,
        mainTransaction = mainTransaction
      )
    }
  }
}


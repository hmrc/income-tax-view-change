/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.{Json, OFormat}

case class ChargesResponse(balanceDetails: BalanceDetails,
                           documentDetails: List[DocumentDetail],
                           financialDetails: List[FinancialDetail]) {

  val payments: List[Payment] = {
    val paymentDocuments: List[DocumentDetail] = documentDetails.filter(document => document.paymentLot.isDefined && document.paymentLotItem.isDefined)

    paymentDocuments.map { document =>
      val subItem = financialDetails.find(_.transactionId.equals(document.transactionId)).flatMap(
        _.items.map(_.find(
          item => item.paymentLot.exists(_.equals(document.paymentLot.get)) && item.paymentLotItem.exists(_.equals(document.paymentLotItem.get))
        ))).flatten

      Payment(
        reference = subItem.flatMap(_.paymentReference),
        amount = document.originalAmount,
        method = subItem.flatMap(_.paymentMethod),
        lot = document.paymentLot,
        lotItem = document.paymentLotItem,
        date = subItem.flatMap(_.dueDate),
        transactionId = document.transactionId
      )
    }
  }.filter(_.reference.isDefined)
}

object ChargesResponse {
  implicit val format: OFormat[ChargesResponse] = Json.format[ChargesResponse]
}

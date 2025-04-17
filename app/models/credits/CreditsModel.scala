/*
 * Copyright 2024 HM Revenue & Customs
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

package models.credits

import models.financialDetails.DocumentDetail
import models.financialDetails.hip.model.{ChargesHipResponse, DocumentDetailHip}
import models.financialDetails.responses.ChargesResponse
import play.api.libs.json.{Json, OFormat}

case class CreditsModel(availableCredit: BigDecimal,
                        allocatedCredit: BigDecimal,
                        transactions: List[Transaction] )

object CreditsModel {

  implicit val format: OFormat[CreditsModel] = Json.format[CreditsModel ]

  @deprecated("To be decommissioned after HiP migration", "MISUV-9576")
  private def getCreditOrPaymentAmount(documentDetail: DocumentDetail): BigDecimal = {
    Option(documentDetail.outstandingAmount)
      .filter(_ < 0)
      .map(_.abs)
      .getOrElse(0.0)
  }

  private def getCreditOrPaymentAmountForHip(documentDetail: DocumentDetailHip): BigDecimal = {
    Option(documentDetail.documentOutstandingAmount)
      .filter(_ < 0)
      .map(_.abs)
      .getOrElse(0.0)
  }

  @deprecated("To be decommissioned after HiP migration", "MISUV-9576")
  private def createPendingRefundTransactions(chargesResponse: ChargesResponse): List[Transaction] = {
    Seq(
      chargesResponse.balanceDetails.firstPendingAmountRequested.map(_.abs),
      chargesResponse.balanceDetails.secondPendingAmountRequested.map(_.abs))
      .flatten.map(amount => Transaction(
        Repayment,
        amount,
        None,
        None
      )).toList
  }

  private def createPendingRefundTransactionsForHip(chargesResponse: ChargesHipResponse): List[Transaction] = {
    Seq(
      chargesResponse.balanceDetails.firstPendingAmountRequested.map(_.abs),
      chargesResponse.balanceDetails.secondPendingAmountRequested.map(_.abs))
      .flatten.map(amount => Transaction(
        Repayment,
        amount,
        None,
        None
      )).toList
  }

  @deprecated("To be decommissioned after HiP migration", "MISUV-9576")
  private def getCreditTransactions(chargesResponse: ChargesResponse): List[Transaction] = {
    chargesResponse.documentDetails.flatMap(documentDetail => {
      for {
        fd <- chargesResponse.financialDetails.find(_.transactionId == documentDetail.transactionId)
        mainTransaction <- fd.mainTransaction
        transactionType <- TransactionType.fromCode(mainTransaction)
      } yield {
        Transaction(
          transactionType = transactionType,
          amount = getCreditOrPaymentAmount(documentDetail),
          taxYear = Some(documentDetail.taxYear),
          dueDate = documentDetail.documentDueDate
        )
      }
    })
  }

  private def getCreditTransactionsForHip(chargesResponse: ChargesHipResponse): List[Transaction] = {
    chargesResponse.documentDetails.flatMap(documentDetail => {
      for {
        fd <- chargesResponse.financialDetails.find(_.transactionId == documentDetail.transactionId)
        mainTransaction <- fd.mainTransaction
        transactionType <- TransactionType.fromCode(mainTransaction)
      } yield {
        Transaction(
          transactionType = transactionType,
          amount = getCreditOrPaymentAmountForHip(documentDetail),
          // TODO: convert TaxYear to Int in the actual model?
          taxYear = Some(documentDetail.taxYear.toInt),
          dueDate = documentDetail.documentDueDate
        )
      }
    })
  }

  @deprecated("To be decommissioned after HiP migration", "MISUV-9576")
  def fromChargesResponse(chargesResponse: ChargesResponse): CreditsModel = {
    CreditsModel(
      chargesResponse.balanceDetails.availableCredit.map(_.abs).getOrElse(0.0),
      chargesResponse.balanceDetails.allocatedCredit.map(_.abs).getOrElse(0.0),
      getCreditTransactions(chargesResponse) :++ createPendingRefundTransactions(chargesResponse)
    )
  }

  def fromHipChargesResponse(chargesResponse: ChargesHipResponse): CreditsModel = {
    CreditsModel(
      chargesResponse.balanceDetails.availableCredit.map(_.abs).getOrElse(0.0),
      chargesResponse.balanceDetails.allocatedCredit.map(_.abs).getOrElse(0.0),
      getCreditTransactionsForHip(chargesResponse) :++ createPendingRefundTransactionsForHip(chargesResponse)
    )
  }

}

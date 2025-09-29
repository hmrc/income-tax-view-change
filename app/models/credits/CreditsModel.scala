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
import models.financialDetails.hip.model.{ChargesHipResponse, DocumentDetailHip}
import play.api.libs.json.{Json, OFormat}

case class CreditsModel(availableCreditForRepayment: BigDecimal,
                        allocatedCredit: BigDecimal,
                        allocatedCreditForFutureCharges: BigDecimal,
                        unallocatedCredit: BigDecimal,
                        totalCredit: BigDecimal,
                        transactions: List[Transaction] )

object CreditsModel {

  implicit val format: OFormat[CreditsModel] = Json.format[CreditsModel ]

  private def getCreditOrPaymentAmountForHip(documentDetail: DocumentDetailHip): BigDecimal = {
    Option(documentDetail.outstandingAmount)
      .filter(_ < 0)
      .map(_.abs)
      .getOrElse(0.0)
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
          taxYear = Some(documentDetail.taxYear),
          dueDate = documentDetail.documentDueDate
        )
      }
    })
  }

  def fromHipChargesResponse(chargesResponse: ChargesHipResponse): CreditsModel = {
    val availableCredit: Option[BigDecimal] = chargesResponse.balanceDetails.availableCredit match {
      //Logic deprecated after R18, should just read from totalCreditAvailableForRepayment
      case Some(value) => Some(value)
      case None => chargesResponse.balanceDetails.totalCreditAvailableForRepayment
    }
    CreditsModel(
      availableCredit.map(_.abs).getOrElse(0.0),
      chargesResponse.balanceDetails.allocatedCredit.map(_.abs).getOrElse(0.0),
      chargesResponse.balanceDetails.allocatedCreditForFutureCharges.map(_.abs).getOrElse(0.0),
      chargesResponse.balanceDetails.unallocatedCredit.map(_.abs).getOrElse(0.0),
      chargesResponse.balanceDetails.totalCredit.map(_.abs).getOrElse(0.0),
      getCreditTransactionsForHip(chargesResponse) :++ createPendingRefundTransactionsForHip(chargesResponse)
    )
  }

}

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

package utils

import models.financialDetails.hip.model.{BalanceDetailsHip, ChargesHipResponse, DocumentDetailHip, FinancialDetailHip, TaxpayerDetailsHip}

import java.time.LocalDate


case class AChargesResponse(model: ChargesHipResponse = ChargesHipResponse(
  TaxpayerDetailsHip("NINO","BB123456A","ITSA"),
  BalanceDetailsHip(0.0, None, 0.0, None, 0.0, None, 0.0, None, None, None, None),
  Nil, Nil, Nil
)) {

  def withAvailableCredit(availableCredit: BigDecimal): AChargesResponse = {
    val balanceDetails = model.balanceDetails
      .copy(availableCredit = Some(availableCredit))
    AChargesResponse(model.copy(balanceDetails = balanceDetails))
  }

  def withAllocatedCredit(allocatedCredit: BigDecimal): AChargesResponse = {
    val balanceDetails = model.balanceDetails
      .copy(allocatedCredit = Some(allocatedCredit))
    AChargesResponse(model.copy(balanceDetails = balanceDetails))
  }

  def withFirstRefundRequest(amount: BigDecimal): AChargesResponse = {
    val balanceDetails = model.balanceDetails
      .copy(firstPendingAmountRequested = Some(amount))
    AChargesResponse(model.copy(balanceDetails = balanceDetails))
  }

  def withSecondRefundRequest(amount: BigDecimal): AChargesResponse = {
    val balanceDetails = model.balanceDetails
      .copy(secondPendingAmountRequested = Some(amount))
    AChargesResponse(model.copy(balanceDetails = balanceDetails))
  }

  private def addToModel(documentAndFinancialDetails: (DocumentDetailHip, FinancialDetailHip)): AChargesResponse = {
    AChargesResponse(model.copy(
      documentDetails = model.documentDetails :+ documentAndFinancialDetails._1,
      financialDetails = model.financialDetails :+ documentAndFinancialDetails._2))
  }

  def withCutoverCredit(transactionId: String, dueDate: LocalDate, outstandingAmount: BigDecimal) = {
    withCustomMainTransaction(transactionId, dueDate, outstandingAmount, Some("ITSA Cutover Credits"), Some("6110"))
  }

  def withBalancingChargeCredit(transactionId: String, dueDate: LocalDate, outstandingAmount: BigDecimal) = {
    withCustomMainTransaction(transactionId, dueDate, outstandingAmount, Some("SA Balancing Charge Credit"), Some("4905"))
  }

  def withRepaymentInterest(transactionId: String, dueDate: LocalDate, outstandingAmount: BigDecimal) = {
    withCustomMainTransaction(transactionId, dueDate, outstandingAmount, Some("SA Repayment Supplement Credit"), Some("6020"))
  }

  def withMfaCredit(transactionId: String, dueDate: LocalDate, outstandingAmount: BigDecimal) = {
    withCustomMainTransaction(transactionId, dueDate, outstandingAmount, Some("ITSA Overpayment Relief"), Some("4004"))
  }

  def withPayment(transactionId: String, dueDate: LocalDate, outstandingAmount: BigDecimal) = {
    withCustomMainTransaction(transactionId, dueDate, outstandingAmount, Some("Payment"), Some("0060"))
  }

  def withCustomMainTransaction(transactionId: String, dueDate: LocalDate, outstandingAmount: BigDecimal, mainType: Option[String], mainTransaction: Option[String]) = {
    val documentAndFinancialDetails = details(
      transactionId = transactionId,
      taxYear = dueDate.getYear,
      mainType = mainType,
      mainTransaction = mainTransaction,
      originalAmount = -1000.0,
      outstandingAmount = outstandingAmount,
      dueDate = dueDate)
    addToModel(documentAndFinancialDetails)
  }

  def get(): ChargesHipResponse = model

  private def details( transactionId: String,
                       taxYear: Int,
                       mainType: Option[String],
                       mainTransaction: Option[String],
                       originalAmount: BigDecimal,
                       outstandingAmount:BigDecimal,
                       dueDate: LocalDate):(DocumentDetailHip, FinancialDetailHip) = {

    val dd = DocumentDetailHip(
      taxYear = taxYear,
      transactionId =transactionId,
      documentDescription = None,
      documentText = None,
      originalAmount = originalAmount,
      outstandingAmount = outstandingAmount,
      documentDate = dueDate,
      interestRate = None,
      interestFromDate = None,
      interestEndDate = None,
      latePaymentInterestId = None,
      latePaymentInterestAmount = None,
      interestOutstandingAmount = None,
      paymentLotItem = None,
      paymentLot = None,
      lpiWithDunningLock = None,
      amountCodedOut = None,
      effectiveDateOfPayment = None,
      documentDueDate = Some(dueDate),
      poaRelevantAmount = None
    )

    val fd = FinancialDetailHip(
      taxYear = s"$taxYear",
      transactionId = transactionId,
      chargeReference = None,
      originalAmount = Some(originalAmount),
      outstandingAmount = Some(outstandingAmount),
      clearedAmount = None,
      chargeType = None,
      mainType = mainType,
      mainTransaction = mainTransaction,
      accruedInterest = None,
      items = None
    )

    (dd, fd)
  }
}

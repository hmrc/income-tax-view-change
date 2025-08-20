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

package constants

import constants.BaseTestConstants.testNino
import models.financialDetails.hip.model.{BalanceDetailsHip, CodingDetailsHip, DocumentDetailHip, FinancialDetailHip, SubItemHip, TaxpayerDetailsHip}

import java.time.LocalDate

object FinancialDetailHipIntegrationTestConstants {

  val testTaxPayerHipDetails: TaxpayerDetailsHip = TaxpayerDetailsHip("NINO", testNino, "ITSA")

  val testBalanceHipDetails: BalanceDetailsHip = BalanceDetailsHip(
    balanceDueWithin30Days = 100.00,
    nxtPymntDateChrgsDueIn30Days = None,
    balanceNotDuein30Days = 200.00,
    nextPaymntDateBalnceNotDue = None,
    overDueAmount = 45.00,
    earlistPymntDateOverDue = None,
    totalBalance = 300.17,
    amountCodedOut = None,
    totalBCDBalance = None,
    unallocatedCredit = Some(400.00),
    allocatedCredit = None,
    totalCredit = None,
    firstPendingAmountRequested = None,
    secondPendingAmountRequested = None,
    availableCredit = None)

  val documentDetailsHip: DocumentDetailHip = DocumentDetailHip(
    taxYear = 2018,
    transactionId = "id",
    documentDate = LocalDate.parse("2018-03-29"),
    documentText = Some("documentText"),
    documentDueDate = Some(LocalDate.parse("2019-03-29")),
    documentDescription = Some("documentDescription"),
    originalAmount = 1000.11,
    outstandingAmount = 200.00,
    poaRelevantAmount = Some(1000.00),
    lastClearedAmount = None,
    paymentLot = Some("paymentLot"),
    paymentLotItem = Some("paymentLotItem"),
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    accruingInterestAmount = Some(12.34),
    interestRate = Some(2.60),
    interestFromDate = Some(LocalDate.parse("2018-08-01")),
    interestEndDate = Some(LocalDate.parse("2019-01-15")),
    latePaymentInterestId = Some("latePaymentInterestID"),
    lpiWithDunningLock = Some(12.50),
    interestOutstandingAmount = Some(31.00),
    amountCodedOut = Some(3.21))

  val financialDetailsHip: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2018",
    transactionId = "transactionId",
    mainType = Some("4920"),
    taxPeriodFrom = Some(LocalDate.parse("2017-04-05")),
    taxPeriodTo = Some(LocalDate.parse("2018-04-06")),
    chargeReference = Some("chargeRef"),
    mainTransaction = Some("4920"),
    originalAmount = Some(BigDecimal(500.00)),
    outstandingAmount = Some(BigDecimal(500.00)),
    clearedAmount = Some(BigDecimal(500.00)),
    accruedInterest = Some(BigDecimal(1000.00)),
    items = Some(
      Seq(
        SubItemHip(
          subItem = Option("001"),
          dueDate = Option(LocalDate.parse("2025-07-10")),
          amount = Option(-300),
          clearingDate = Option(LocalDate.parse("2024-10-29")),
          clearingReason = Option("Allocated to Charge"),
          paymentReference = Option("100207948"),
          paymentAmount = Option(BigDecimal(500)),
          paymentMethod = Option("PAYMENTS MADE BY CHEQUE"),
          paymentLot = Option("24714"),
          paymentLotItem = Option("000002"),
          clearingSAPDocument = Option("003400044065"),
          codedOutStatus = Option("S"),
          paymentId = Option("24714-000002")
        )
      )
    ))

  val codingOutList: List[CodingDetailsHip] = List(
    CodingDetailsHip(
      totalLiabilityAmount = Some(2015.13),
      taxYearReturn = Option("2018")
    )
  )
}

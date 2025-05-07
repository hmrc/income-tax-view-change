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
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

object FinancialDetailHipIntegrationTestConstants {

  // OLD => VERSION
  val chargeHipJson: JsObject =
    Json.obj("success" ->
      Json.obj(
        "taxpayerDetails" -> Json.obj(
          "idType" -> "NINO",
          "idNumber" -> "BB123456A",
          "regimeType" -> "ITSA"
        ),
        "balanceDetails" -> Json.obj(
          "balanceDueWithin30days" -> 100.00,
          "balanceNotDuein30Days" -> 200.00,
          "overDueAmount" -> 45.00,
          "totalBalance" -> 300.17,
          "unallocatedCredit" -> 400.00
        ),
        "codingDetails" -> Json.arr(
          Json.obj(
            "taxYearReturn" -> "2018",
            "totalLiabilityAmount" -> 2015.13,
            "taxYearCoding" -> "2017"
          )
        ),
        "documentDetails" -> Json.arr(
          Json.obj(
            "taxYear" -> "2018",
            "documentID" -> "id",
            "documentDate" -> "2018-03-29",
            "documentText" -> "documentText",
            "documentDueDate" -> "2019-03-29",
            "documentDescription" -> "documentDescription",
            "totalAmount" -> 1000.11,
            "documentOutstandingAmount" -> 200.00,
            "documentOutstandingAmount" -> 200.00,
            "poaRelevantAmount" -> 1000.00,
            "paymentLotItem" -> "paymentLotItem",
            "paymentLot" -> "paymentLot",
            "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
            "interestRate" -> 2.60,

            "interestFromDate" -> "2018-08-01",
            "interestEndDate" -> "2019-01-15",
            "latePaymentInterestID" -> "latePaymentInterestID",
            "latePaymentInterestAmount" -> 12.34,
            "lpiWithDunningBlock" -> 12.50,
            "interestOutstandingAmount" -> 31.00,
            "amountCodedOut" -> 3.21,
          )
        ),
        "financialDetails" -> Json.arr(
          Json.obj(
            "taxYear" -> "2018",
            "documentID" -> "transactionId",
            "mainType" -> "4920",
            "taxPeriodFrom" -> LocalDate.parse("2017-04-05"),
            "taxPeriodTo" -> LocalDate.parse("2018-04-06"),
            "chargeReference" -> "chargeRef",
            "mainTransaction" -> "4920",
            "originalAmount" -> 500.00,
            "outstandingAmount" -> 500.00,
            "clearedAmount" -> 500.00,
            "accruedInterest" -> 1000,
            "items" -> Json.arr(
              Json.obj(
              )
            )
          )
        ),
      )
    )

  // HiP
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
    accruingInterestAmount = None,
    interestRate = Some(2.60),
    interestFromDate = Some(LocalDate.parse("2018-08-01")),
    interestEndDate = Some(LocalDate.parse("2019-01-15")),
    latePaymentInterestId = Some("latePaymentInterestID"),
    latePaymentInterestAmount = Some(12.34),
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
        SubItemHip()
      )
    ))

  val codingOutList: List[CodingDetailsHip] = List(
    CodingDetailsHip(
      taxYearReturn = Option("2018"),
      amountCodedOut = Option(2015.13),
      taxYearCoding = Option("2017"),
      coded = None
    )
  )
}

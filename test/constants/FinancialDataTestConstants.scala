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

package constants

import constants.BaseTestConstants.testNino
import models.financialDetails.hip.model.{BalanceDetailsHip, ChargesHipResponse, CodingDetailsHip, DocumentDetailHip, FinancialDetailHip, SubItemHip, TaxpayerDetailsHip}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object FinancialDataTestConstants {

  val validHipChargesJson: JsValue = Json.parse(
    """
      |{
      |  "success": {
      |    "taxpayerDetails": {
      |      "idType": "NINO",
      |      "idNumber": "BB123456A",
      |      "regimeType": "ITSA"
      |    },
      |    "balanceDetails": {
      |      "balanceDueWithin30days": 100.0,
      |      "balanceNotDuein30Days": 200.0,
      |      "nextPaymntDateBalnceNotDue": "2018-08-01",
      |      "nxtPymntDateChrgsDueIn30Days" : "2018-08-01",
      |      "overDueAmount": 45.0,
      |      "earlistPymntDateOverDue" : "2018-08-01",
      |      "totalBalance": 450.0,
      |      "amountCodedOut": 340.55,
      |      "totalCredit": 123.0,
      |      "firstPendingAmountRequested" : 120.0
      |    },
      |    "codingDetails": [
      |      {
      |        "taxYearReturn": "2024",
      |        "totalLiabilityAmount": 1500.0
      |      }
      |    ],
      |    "documentDetails": [
      |      {
      |        "taxYear": "2018",
      |        "documentID": "id",
      |        "documentDate": "2018-03-29",
      |        "documentText": "documentText",
      |        "documentDueDate": "2019-03-29",
      |        "documentDescription": "documentDescription",
      |        "totalAmount": 1000.0,
      |        "documentOutstandingAmount": 200.0,
      |        "poaRelevantAmount": 1000.0,
      |        "paymentLot": "paymentLot",
      |        "paymentLotItem": "paymentLotItem",
      |        "effectiveDateOfPayment": "2018-03-29",
      |        "interestRate": 2.6,
      |        "interestFromDate": "2018-08-01",
      |        "interestEndDate": "2019-01-15",
      |        "latePaymentInterestID": "latePaymentInterestID",
      |        "lpiWithDunningLock": 12.50,
      |        "interestOutstandingAmount": 31.0,
      |        "amountCodedOut": 3.21,
      |        "accruingInterestAmount": 12.34
      |      }
      |    ],
      |    "financialDetailsItem": [
      |      {
      |        "taxYear": "2018",
      |        "documentID": "id",
      |        "taxPeriodFrom": "2017-04-05",
      |        "taxPeriodTo": "2018-04-06",
      |        "chargeReference": "chargeRef",
      |        "mainType": "4920",
      |        "mainTransaction": "4920",
      |        "originalAmount": 500.0,
      |        "outstandingAmount": 500.0,
      |        "clearedAmount": 500.0,
      |        "accruedInterest": 1000.0,
      |        "items": [
      |         {
      |         }
      |        ]
      |      }
      |    ]
      |  }
      |}
      |""".stripMargin
  )


  val validFinancialDetailJsonAfterWrites: JsValue = Json.parse(
    """
			|{
			|     "taxYear": "2018",
			|     "transactionId": "id",
      |     "chargeType": "POA1",
      |     "mainType": "4920",
      |     "chargeReference" : "chargeRef",
      |     "mainTransaction": "4920",
			|     "originalAmount": 500.00,
			|     "outstandingAmount": 500.00,
			|     "clearedAmount": 500.00,
      |     "accruedInterest": 1000,
			|     "items": [{
			|       "amount": 100.00,
			|       "clearingDate": "2022-06-23",
			|       "clearingReason": "clearingReason",
      |       "clearingSAPDocument": "012345678912",
			|       "outgoingPaymentMethod": "outgoingPaymentMethod",
      |       "interestLock": "interestLock",
      |       "dunningLock": "dunningLock",
			|       "paymentReference": "paymentReference",
			|       "paymentAmount": 2000.00,
			|       "dueDate": "2022-06-23",
			|       "paymentMethod": "paymentMethod",
			|       "paymentLot": "paymentLot",
      |       "subItem" : "1",
			|       "paymentLotItem": "paymentLotItem",
			|       "paymentId": "paymentLot-paymentLotItem",
      |       "codedOutStatus": "I"
			|       }
			|     ]
			|}
			|""".stripMargin)

  val testTaxPayerHipDetails: TaxpayerDetailsHip = TaxpayerDetailsHip("NINO", testNino, "ITSA")

  val testBalanceDetails: BalanceDetailsHip = BalanceDetailsHip(100.00, None, 200.00, None, 300.00, None, 400.00)

  val testCodingDetails: CodingDetailsHip = CodingDetailsHip(Some(2300.00), Some("2020-04-20"))

  val documentDetail: DocumentDetailHip = DocumentDetailHip(
    taxYear = 2018,
    transactionId = "id",
    documentDescription = Some("documentDescription"),
    documentText = Some("documentText"),
    originalAmount = 300.00,
    outstandingAmount = 200.00,
    documentDate = LocalDate.parse("2018-03-29"),
    interestRate = Some(2.60),
    interestFromDate = Some(LocalDate.parse("2018-08-01")),
    interestEndDate = Some(LocalDate.parse("2019-01-15")),
    latePaymentInterestId = Some("latePaymentInterestID"),
    interestOutstandingAmount = Some(31.00),
    paymentLotItem = Some("paymentLotItem"),
    paymentLot = Some("paymentLot"),
    lpiWithDunningLock = Some(12.50),
    amountCodedOut = Some(3.21),
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    documentDueDate = Some(LocalDate.parse("2019-03-29")),
    poaRelevantAmount = Some(1000.00),
    accruingInterestAmount = Some(12.34)
  )

  val documentDetail2: DocumentDetailHip = DocumentDetailHip(
    taxYear = 2019,
    transactionId = "id2",
    documentDescription = Some("documentDescription2"),
    documentText = None,
    originalAmount = 100.00,
    outstandingAmount = 50.00,
    documentDate = LocalDate.parse("2018-03-29"),
    interestRate = None,
    interestFromDate = None,
    interestEndDate = None,
    latePaymentInterestId = None,
    interestOutstandingAmount = None,
    paymentLotItem = None,
    paymentLot = None,
    lpiWithDunningLock = None,
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    poaRelevantAmount = Some(1000.00),
    accruingInterestAmount = None
  )

  val documentDetail3: DocumentDetailHip = DocumentDetailHip(
    taxYear = 2018,
    documentDescription = Some("documentDescription"),
    documentText = None,
    originalAmount = -1000,
    outstandingAmount = 0,
    documentDate = LocalDate.parse("2022-06-23"),
    interestRate = None,
    interestFromDate = None,
    interestEndDate = None,
    latePaymentInterestId = None,
    interestOutstandingAmount = None,
    transactionId = "id",
    paymentLot = None,
    paymentLotItem = None,
    lpiWithDunningLock = None,
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    poaRelevantAmount = Some(1000.00),
    accruingInterestAmount = None
  )

  val financialDetail3: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2018",
    transactionId = "id",
    chargeReference = Some("chargeRef"),
    originalAmount = Some(BigDecimal(-1000.00)),
    outstandingAmount = Some(BigDecimal("0")),
    clearedAmount = Some(BigDecimal(-1000.00)),
    chargeType = Some("Cutover Credits"),
    mainType = Some("ITSA Cutover Credits"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("0")),
    items = Some(Seq(
      SubItemHip(
        amount = Some(BigDecimal("-1000.00")),
        clearingDate = Some(LocalDate.parse("2022-06-23")),
        clearingReason = Some("clearingReason"),
        clearingSAPDocument = Some("012345678912"),
        outgoingPaymentMethod = Some("outgoingPaymentMethod"),
        interestLock = Some("interestLock"),
        dunningLock = Some("dunningLock"),
        paymentReference = Some("paymentReference"),
        paymentAmount = None,
        dueDate = Some(LocalDate.parse("2022-06-23")),
        paymentMethod = None,
        paymentLot = None,
        paymentLotItem = None,
        paymentId = None,
        codedOutStatus = Some("I")
      )))
  )

  val financialDetail: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2018",
    transactionId = "id",
    chargeReference = Some("chargeRef"),
    originalAmount = Some(500.0),
    outstandingAmount = Some(500.00),
    clearedAmount = Some(BigDecimal(500.00)),
    chargeType = Some("POA1"),
    mainType = Some("4920"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal(1000.0)),
    items = Some(Seq(
      SubItemHip(
        amount = Some(BigDecimal(100.00)),
        clearingDate = Some(LocalDate.parse("2022-06-23")),
        clearingReason = Some("clearingReason"),
        clearingSAPDocument = Some("012345678912"),
        outgoingPaymentMethod = Some("outgoingPaymentMethod"),
        interestLock = Some("interestLock"),
        dunningLock = Some("dunningLock"),
        paymentReference = Some("paymentReference"),
        paymentAmount = Some(BigDecimal("2000.0")),
        dueDate = Some(LocalDate.parse("2022-06-23")),
        paymentMethod = Some("paymentMethod"),
        paymentLot = Some("paymentLot"),
        paymentLotItem = Some("paymentLotItem"),
        paymentId = Some("paymentLot-paymentLotItem"),
        subItem = Some("1"),
        codedOutStatus = Some("I")
      )))
  )
  val financialDetail2: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2019",
    transactionId = "transactionId2",
    chargeReference = Some("chargeRef"),
    originalAmount = Some(BigDecimal(500.00)),
    outstandingAmount = Some(BigDecimal("200.00")),
    clearedAmount = Some(BigDecimal(500.00)),
    chargeType = Some("POA1"),
    mainType = Some("4920"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("2000.00")),
    items = Some(Seq(
      SubItemHip(
        amount = Some(BigDecimal("200.00")),
        clearingDate = Some(LocalDate.parse("2022-06-23")),
        clearingReason = Some("clearingReason2"),
        clearingSAPDocument = Some("012345678912"),
        outgoingPaymentMethod = Some("outgoingPaymentMethod2"),
        interestLock = Some("interestLock2"),
        dunningLock = Some("dunningLock2"),
        paymentReference = Some("paymentReference2"),
        paymentAmount = Some(BigDecimal("3000.00")),
        dueDate = Some(LocalDate.parse("2022-06-23")),
        paymentMethod = Some("paymentMethod2"),
        paymentLot = Some("paymentLot2"),
        paymentLotItem = Some("paymentLotItem2"),
        paymentId = Some("paymentLot2-paymentLotItem2"),
        subItem = Some("2"),
        codedOutStatus = Some("I")
      )))
  )

  val chargesResponse: ChargesHipResponse = ChargesHipResponse(
    taxpayerDetails = testTaxPayerHipDetails,
    balanceDetails = testBalanceDetails,
    codingDetails = List(testCodingDetails),
    documentDetails = List(documentDetail),
    financialDetails = List(financialDetail)
  )

  val creditChargesResponse: ChargesHipResponse = ChargesHipResponse(
    balanceDetails = testBalanceDetails,
    taxpayerDetails = testTaxPayerHipDetails,
    codingDetails = List(testCodingDetails),
    documentDetails = List(documentDetail3),
    financialDetails = List(financialDetail3)
  )

  val chargesResponseNoCodingDetails2: ChargesHipResponse = creditChargesResponse

  val chargesResponseNoCodingDetails: ChargesHipResponse = chargesResponse

  val testChargesResponse: ChargesHipResponse = ChargesHipResponse(
    taxpayerDetails = testTaxPayerHipDetails,
    balanceDetails = testBalanceDetails,
    codingDetails = List(testCodingDetails),
    documentDetails = List(documentDetail, documentDetail2),
    financialDetails = List(financialDetail, financialDetail2)
  )

  val validSubItemJson: JsValue = Json.parse(
    """
			|{
      |       "subItem": "001",
			|       "amount": -300.00,
			|       "clearingDate": "2024-10-29",
			|       "clearingReason": "Allocated to Charge",
      |       "clearingSAPDocument":  "003400044065",
			|       "paymentReference": "100207948",
			|       "paymentAmount": 500.00,
			|       "dueDate": "2025-07-10",
			|       "paymentMethod": "PAYMENTS MADE BY CHEQUE",
			|       "paymentLot": "24714",
			|       "paymentLotItem": "000002",
      |       "codedOutStatus": "S"
			|}
			|""".stripMargin)

  val subItems1: SubItemHip = SubItemHip(
    subItem = Some("001"),
    dueDate = Some(LocalDate.parse("2025-07-10")),
    amount = Some(-300.00),
    clearingDate = Some(LocalDate.parse("2024-10-29")),
    clearingReason = Some("Allocated to Charge"),
    paymentReference = Some("100207948"),
    paymentAmount = Some(BigDecimal(500)),
    paymentMethod = Some("PAYMENTS MADE BY CHEQUE"),
    paymentLot = Some("24714"),
    paymentLotItem = Some("000002"),
    clearingSAPDocument = Some("003400044065"),
    codedOutStatus = Some("S"),
    paymentId = Option("24714-000002")
  )




  val validSubItemJsonAfterWrites: JsValue = Json.parse(
    """
			|{
      |       "subItem": "001",
			|       "amount": -300.00,
			|       "clearingDate": "2024-10-29",
			|       "clearingReason": "Allocated to Charge",
      |       "clearingSAPDocument":  "003400044065",
			|       "paymentReference": "100207948",
			|       "paymentAmount": 500.00,
			|       "dueDate": "2025-07-10",
			|       "paymentMethod": "PAYMENTS MADE BY CHEQUE",
			|       "paymentLot": "24714",
			|       "paymentLotItem": "000002",
			|       "paymentId": "24714-000002",
      |       "codedOutStatus": "S"
			|}
			|""".stripMargin)


  // Hip Migration

  val testBalanceHipDetails: BalanceDetailsHip = BalanceDetailsHip(
    balanceDueWithin30Days = 100.00,
    nxtPymntDateChrgsDueIn30Days = Some(LocalDate.parse("2018-08-01")),
    balanceNotDuein30Days = 200.00,
    nextPaymntDateBalnceNotDue = Some(LocalDate.parse("2018-08-01")),
    overDueAmount = 45.00,
    earlistPymntDateOverDue = Some(LocalDate.parse("2018-08-01")),
    totalBalance = 450.00,
    amountCodedOut = Some(340.55),
    totalBCDBalance = None,
    unallocatedCredit = None,
    totalCredit = Some(123.00),
    firstPendingAmountRequested = Some(120.00),
    secondPendingAmountRequested = None,
    totalCreditAvailableForRepayment = None)

  val testCodingDetailsHip: CodingDetailsHip = CodingDetailsHip(
    totalLiabilityAmount = Some(100),
    taxYearReturn = Some("2018")
  )

  val documentDetailsHip: DocumentDetailHip = DocumentDetailHip(taxYear = 2018,
    transactionId = "id",
    documentDate = LocalDate.parse("2018-03-29"),
    documentText = Some("documentText"),
    documentDueDate = Some(LocalDate.parse("2019-03-29")),
    documentDescription = Some("documentDescription"),
    originalAmount = 1000.00,
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
    transactionId = "id",
    mainType = Some("4920"),
    taxPeriodFrom = Some(LocalDate.parse("2017-04-05")),
    taxPeriodTo = Some(LocalDate.parse("2018-04-06")),
    chargeReference = Some("chargeRef"),
    mainTransaction =  Some("4920"),
    originalAmount = Some(BigDecimal(500.00)),
    outstandingAmount = Some(BigDecimal("500.00")),
    clearedAmount =  Some(BigDecimal(500.00)),
    accruedInterest = Some(BigDecimal("1000.00")),
    items = Some(
      Seq(
        SubItemHip()
      )
    ))

  val testChargeHipResponse: ChargesHipResponse = ChargesHipResponse(
    taxpayerDetails = testTaxPayerHipDetails,
    balanceDetails = testBalanceHipDetails,
    codingDetails = List(
      CodingDetailsHip(Some(1500.00), Some("2024"))
    ),
    documentDetails = List(documentDetailsHip),
    financialDetails = List(financialDetailsHip)
  )
}

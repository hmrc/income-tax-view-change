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
import models.financialDetails.hip.model.{BalanceDetailsHip, ChargesHipResponse,
  CodingDetailsHip, DocumentDetailHip, FinancialDetailHip, SubItemHip, TaxpayerDetailsHip}
import models.financialDetails.responses.ChargesResponse
import models.financialDetails.{BalanceDetails, DocumentDetail, FinancialDetail, SubItem}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object FinancialDataTestConstants {

  val validChargesJson: JsValue = Json.parse(
    """{
      |"balanceDetails": {
      | "balanceDueWithin30Days": 100.00,
      | "overDueAmount": 200.00,
      | "totalBalance": 300.00,
      | "unallocatedCredit": 400.00
      | },
      |"codingDetails": [
      |   {
      |     "taxYearReturn": "2018",
      |     "totalReturnAmount": 100.00,
      |     "amountNotCoded": 200.00,
      |     "amountNotCodedDueDate": "2018-01-01",
      |     "amountCodedOut": 300.00,
      |     "taxYearCoding": "2019"
      |   }
      | ],
      | "documentDetails": [
      |   {
      |     "taxYear": "2018",
      |     "documentId": "id",
      |     "documentDescription": "documentDescription",
      |     "documentText": "documentText",
      |     "totalAmount": 300.0,
      |     "documentOutstandingAmount": 200.0,
      |     "lastClearedAmount": 100.0,
      |     "documentDate": "2018-03-29",
      |     "interestRate": 2.6,
      |     "interestFromDate": "2018-08-01",
      |     "interestEndDate": "2019-01-15",
      |     "latePaymentInterestID": "latePaymentInterestID",
      |     "latePaymentInterestAmount": 12.34,
      |     "interestOutstandingAmount": 31.0,
      |     "paymentLot": "paymentLot",
      |     "paymentLotItem": "paymentLotItem",
      |     "lpiWithDunningBlock": 12.5,
      |     "amountCodedOut" : 3.21,
      |     "effectiveDateOfPayment" : "2018-03-29",
      |     "documentDueDate" : "2019-03-29",
      |     "poaRelevantAmount": 1000.0
      |   },
      |   {
      |     "taxYear": "2019",
      |     "documentId": "id2",
      |     "documentDescription": "documentDescription2",
      |     "totalAmount": 100.00,
      |     "documentOutstandingAmount": 50.00,
      |     "lastClearedAmount": 50.00,
      |     "documentDate": "2018-03-29",
      |     "effectiveDateOfPayment" : "2018-03-29",
      |     "poaRelevantAmount": 1000.0
      |   }
      | ],
      | "financialDetails": [
      |   {
      |     "taxYear": "2018",
      |     "documentId": "id",
      |     "documentDate": "2022-06-23",
      |     "chargeReference": "chargeRef",
      |     "documentDescription": "type",
      |     "totalAmount": 1000.00,
      |     "originalAmount": 500.00,
      |     "clearedAmount": 500.00,
      |     "documentOutstandingAmount": 500.00,
      |     "chargeType": "POA1",
      |     "mainType": "4920",
      |     "mainTransaction": "4920",
      |     "accruedInterest": 1000,
      |     "items": [{
      |       "subItem": "1",
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
      |       "paymentLotItem": "paymentLotItem"
      |       }
      |     ]
      |   },
      |   {
      |     "taxYear": "2019",
      |     "documentId": "transactionId2",
      |     "documentDate": "2022-06-23",
      |     "chargeReference": "chargeRef",
      |     "documentDescription": "type2",
      |     "totalAmount": 2000.00,
      |     "originalAmount": 500.00,
      |     "clearedAmount": 500.00,
      |     "documentOutstandingAmount": 200.00,
      |     "chargeType": "POA1",
      |     "mainType": "4920",
      |     "mainTransaction": "4920",
      |     "accruedInterest": 2000,
      |     "items": [{
      |       "subItem": "2",
      |       "amount": 200.00,
      |       "clearingDate": "2022-06-23",
      |       "clearingReason": "clearingReason2",
      |       "clearingSAPDocument": "012345678912",
      |       "outgoingPaymentMethod": "outgoingPaymentMethod2",
      |       "interestLock": "interestLock2",
      |       "dunningLock": "dunningLock2",
      |       "paymentReference": "paymentReference2",
      |       "paymentAmount": 3000.00,
      |       "dueDate": "2022-06-23",
      |       "paymentMethod": "paymentMethod2",
      |       "paymentLot": "paymentLot2",
      |       "paymentLotItem": "paymentLotItem2"
      |       }
      |     ]
      |   }
      | ]
      |}""".stripMargin)


  val validFinancialDetailJsonAfterWrites: JsValue = Json.parse(
    """
			|{
			|     "taxYear": "2018",
			|     "transactionId": "id",
			|     "transactionDate": "2022-06-23",
      |     "chargeReference": "chargeRef",
			|     "type": "type",
			|     "totalAmount": 1000.00,
			|     "originalAmount": 500.00,
			|     "outstandingAmount": 500.00,
			|     "clearedAmount": 500.00,
			|     "chargeType": "POA1",
			|     "mainType": "4920",
      |     "mainTransaction": "4920",
      |     "accruedInterest": 1000,
			|     "items": [{
			|       "subItemId": "1",
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
			|       "paymentLotItem": "paymentLotItem",
			|       "paymentId": "paymentLot-paymentLotItem"
			|       }
			|     ]
			|}
			|""".stripMargin)

  val testTaxPayerHipDetails : TaxpayerDetailsHip = TaxpayerDetailsHip("NINO", testNino, "ITSA")

  val testBalanceDetails: BalanceDetails = BalanceDetails(100.00, 200.00, 300.00, None, None, None, None, Some(400.00))
  val testBalanceHipDetails: BalanceDetailsHip = BalanceDetailsHip(
    balanceDueWithin30days = 100.00,
    nxtPymntDateChrgsDueIn30Days = Some(LocalDate.parse("2018-08-01")),
    balanceNotDuein30Days = 200.00,
    nextPaymntDateBalnceNotDue = Some(LocalDate.parse("2018-08-01")),
    overDueAmount = 45.00,
    earlistPymntDateOverDue = Some(LocalDate.parse("2018-08-01")),
    totalBalance = 450.00,
    amountCodedOut = Some(340.55),
    totalBCDBalance = None,
    unallocatedCredit = None,
    allocatedCredit = None,
    totalCredit = Some(123.00),
    firstPendingAmountRequested = Some(120.00),
    secondPendingAmountRequested = None,
    availableCredit = None)

  val documentDetail: DocumentDetail = DocumentDetail(
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
    latePaymentInterestAmount = Some(12.34),
    interestOutstandingAmount = Some(31.00),
    paymentLotItem = Some("paymentLotItem"),
    paymentLot = Some("paymentLot"),
    lpiWithDunningBlock = Some(12.50),
    amountCodedOut = Some(3.21),
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    documentDueDate = Some(LocalDate.parse("2019-03-29")),
    poaRelevantAmount = Some(1000.00)
  )

  val documentDetailsHip: DocumentDetailHip = DocumentDetailHip(taxYear = "2018",
    transactionId = "id",
    documentDate = LocalDate.parse("2018-03-29"),
    documentText = Some("documentText"),
    documentDueDate = Some(LocalDate.parse("2019-03-29")),
    documentDescription = Some("documentDescription"),
    totalAmount = 1000.00,
    documentOutstandingAmount = 200.00,
    poaRelevantAmount = Some(1000.00),
    lastClearedAmount = None,
    paymentLot = Some("paymentLot"),
    paymentLotItem = Some("paymentLotItem"),
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    accruingInterestAmount = None,
    interestRate = Some(2.60),
    interestFromDate = Some(LocalDate.parse("2018-08-01")),
    interestEndDate = Some(LocalDate.parse("2019-01-15")),
    latePaymentInterestID = Some("latePaymentInterestID"),
    latePaymentInterestAmount = Some(12.34),
    lpiWithDunningLock = Some(12.50),
    interestOutstandingAmount = Some(31.00),
    amountCodedOut = Some(3.21))


  val documentDetail2: DocumentDetail = DocumentDetail(
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
    latePaymentInterestAmount = None,
    interestOutstandingAmount = None,
    paymentLotItem = None,
    paymentLot = None,
    lpiWithDunningBlock = None,
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    poaRelevantAmount = Some(1000.00)
  )

  val documentDetail3: DocumentDetail = DocumentDetail(
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
    latePaymentInterestAmount = None,
    interestOutstandingAmount = None,
    transactionId = "id",
    paymentLot = None,
    paymentLotItem = None,
    lpiWithDunningBlock = None,
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    poaRelevantAmount = Some(1000.00)
  )

  val financialDetail3: FinancialDetail = FinancialDetail(
    taxYear = "2018",
    transactionId = "id",
    transactionDate = Some(LocalDate.parse("2022-06-23")),
    chargeReference = Some("chargeRef"),
    `type` = Some("type"),
    totalAmount = Some(BigDecimal("-1000.00")),
    originalAmount = Some(BigDecimal(-1000.00)),
    outstandingAmount = Some(BigDecimal("0")),
    clearedAmount = Some(BigDecimal(-1000.00)),
    chargeType = Some("Cutover Credits"),
    mainType = Some("ITSA Cutover Credits"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("0")),
    items = Some(Seq(
      SubItem(
        subItemId = Some("1"),
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
        paymentId = None
      )))
  )

  val financialDetail: FinancialDetail = FinancialDetail(
    taxYear = "2018",
    transactionId = "id",
    transactionDate = Some(LocalDate.parse("2022-06-23")),
    chargeReference = Some("chargeRef"),
    `type` = Some("type"),
    totalAmount = Some(BigDecimal("1000.00")),
    originalAmount = Some(BigDecimal(500.00)),
    outstandingAmount = Some(BigDecimal("500.00")),
    clearedAmount = Some(BigDecimal(500.00)),
    chargeType = Some("POA1"),
    mainType = Some("4920"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("1000.00")),
    items = Some(Seq(
      SubItem(
        subItemId = Some("1"),
        amount = Some(BigDecimal("100.00")),
        clearingDate = Some(LocalDate.parse("2022-06-23")),
        clearingReason = Some("clearingReason"),
        clearingSAPDocument = Some("012345678912"),
        outgoingPaymentMethod = Some("outgoingPaymentMethod"),
        interestLock = Some("interestLock"),
        dunningLock = Some("dunningLock"),
        paymentReference = Some("paymentReference"),
        paymentAmount = Some(BigDecimal("2000.00")),
        dueDate = Some(LocalDate.parse("2022-06-23")),
        paymentMethod = Some("paymentMethod"),
        paymentLot = Some("paymentLot"),
        paymentLotItem = Some("paymentLotItem"),
        paymentId = Some("paymentLot-paymentLotItem")
      )))
  )
  val financialDetail2: FinancialDetail = FinancialDetail(
    taxYear = "2019",
    transactionId = "transactionId2",
    transactionDate = Some(LocalDate.parse("2022-06-23")),
    chargeReference = Some("chargeRef"),
    `type` = Some("type2"),
    totalAmount = Some(BigDecimal("2000.00")),
    originalAmount = Some(BigDecimal(500.00)),
    outstandingAmount = Some(BigDecimal("200.00")),
    clearedAmount = Some(BigDecimal(500.00)),
    chargeType = Some("POA1"),
    mainType = Some("4920"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("2000.00")),
    items = Some(Seq(
      SubItem(
        subItemId = Some("2"),
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
        paymentId = Some("paymentLot2-paymentLotItem2")
      )))
  )

  val financialDetailsHip: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2018",
    transactionId = "id",
    mainType = Some("4920"),
    periodKeyDescription = None,
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

  val chargesResponse: ChargesResponse = ChargesResponse(
    balanceDetails = testBalanceDetails,
    documentDetails = List(documentDetail),
    financialDetails = List(financialDetail)
  )

  val creditChargesResponse: ChargesResponse = ChargesResponse(
    balanceDetails = testBalanceDetails,
    documentDetails = List(documentDetail3),
    financialDetails = List(financialDetail3)
  )

  val chargesResponseNoCodingDetails2: ChargesResponse = creditChargesResponse

  val chargesResponseNoCodingDetails: ChargesResponse = chargesResponse

  val testChargesResponse: ChargesResponse = ChargesResponse(
    balanceDetails = testBalanceDetails,
    documentDetails = List(documentDetail, documentDetail2),
    financialDetails = List(financialDetail, financialDetail2)
  )
  val testChargeHipResponse: ChargesHipResponse = ChargesHipResponse(
    taxpayerDetails = testTaxPayerHipDetails,
    balanceDetails = testBalanceHipDetails,
    codingDetails = List(
      CodingDetailsHip(Some("2024"))
    ),
    documentDetails = List(documentDetailsHip),
    financialDetails = List(financialDetailsHip)
  )

  val validSubItemJson: JsValue = Json.parse(
    """
			|{
			|       "subItem": "1",
			|       "amount": 100.00,
			|       "clearingDate": "2022-06-23",
			|       "clearingReason": "clearingReason",
      |       "clearingSAPDocument":  "012345678912",
			|       "outgoingPaymentMethod": "outgoingPaymentMethod",
      |       "interestLock": "interestLock",
      |       "dunningLock": "dunningLock",
			|       "paymentReference": "paymentReference",
			|       "paymentAmount": 2000.00,
			|       "dueDate": "2022-06-23",
			|       "paymentMethod": "paymentMethod",
			|       "paymentLot": "paymentLot",
			|       "paymentLotItem": "paymentLotItem"
			|}
			|""".stripMargin)


  val subItems1: SubItem = SubItem(
    subItemId = Some("1"),
    amount = Some(BigDecimal("100.00")),
    clearingDate = Some(LocalDate.parse("2022-06-23")),
    clearingReason = Some("clearingReason"),
    clearingSAPDocument = Some("012345678912"),
    outgoingPaymentMethod = Some("outgoingPaymentMethod"),
    interestLock = Some("interestLock"),
    dunningLock = Some("dunningLock"),
    paymentReference = Some("paymentReference"),
    paymentAmount = Some(BigDecimal("2000.00")),
    dueDate = Some(LocalDate.parse("2022-06-23")),
    paymentMethod = Some("paymentMethod"),
    paymentLot = Some("paymentLot"),
    paymentLotItem = Some("paymentLotItem"),
    paymentId = Some("paymentLot-paymentLotItem")
  )

  val validSubItemJsonAfterWrites: JsValue = Json.parse(
    """
			|{
			|       "subItemId": "1",
			|       "amount": 100.00,
			|       "clearingDate": "2022-06-23",
			|       "clearingReason": "clearingReason",
      |       "clearingSAPDocument":  "012345678912",
			|       "outgoingPaymentMethod": "outgoingPaymentMethod",
      |       "interestLock": "interestLock",
      |       "dunningLock": "dunningLock",
			|       "paymentReference": "paymentReference",
			|       "paymentAmount": 2000.00,
			|       "dueDate": "2022-06-23",
			|       "paymentMethod": "paymentMethod",
			|       "paymentLot": "paymentLot",
			|       "paymentLotItem": "paymentLotItem",
			|       "paymentId": "paymentLot-paymentLotItem"
			|}
			|""".stripMargin)

}

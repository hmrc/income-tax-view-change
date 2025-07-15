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

package constants

import models.financialDetails.hip.model.{BalanceDetailsHip, CodingDetailsHip, DocumentDetailHip, FinancialDetailHip, SubItemHip, TaxpayerDetailsHip}
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

object FinancialDetailIntegrationTestConstants {

  val chargeJson: JsObject = Json.obj(
    "success" -> Json.obj(
    "taxpayerDetails" -> Json.obj(
      "idType" -> "NINO",
      "idNumber" -> "BB123456A",
      "regimeType" -> "ITSA"
    ),
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30days" -> 100.00,
      "balanceNotDuein30Days" -> 0,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00,
      "unallocatedCredit" -> 400.00
    ),
    "codingDetails" -> Json.arr(
        Json.obj(
          "totalLiabilityAmount" -> 2300.00,
          "taxYearReturn" -> "2020"
        )
    ),
    "documentDetails" -> Json.arr(
      Json.obj(
        "taxYear" -> "2018",
        "documentID" -> "id",
        "documentDescription" -> "documentDescription",
        "documentText" -> "documentText",
        "totalAmount" -> 300.00,
        "documentOutstandingAmount" -> 200.00,
        "documentDate" -> "2018-03-29",
        "interestRate" -> 2.60,
        "interestFromDate" -> "2018-08-01",
        "interestEndDate" -> "2019-01-15",
        "latePaymentInterestID" -> "latePaymentInterestID",
        "latePaymentInterestAmount" -> 12.34,
        "interestOutstandingAmount" -> 31.00,
        "paymentLotItem" -> "paymentLotItem",
        "paymentLot" -> "paymentLot",
        "lpiWithDunningBlock" -> 12.50,
        "amountCodedOut" -> 3.21,
        "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
        "poaRelevantAmount" -> 1000.00
      ),
      Json.obj(
        "taxYear" -> "2019",
        "documentID" -> "id2",
        "documentDescription" -> "documentDescription2",
        "totalAmount" -> 100.00,
        "documentOutstandingAmount" -> 50.00,
        "documentDate" -> "2018-03-29",
        "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
        "poaRelevantAmount" -> 1000.00
      )
    ),
    "financialDetailsItem" -> Json.arr(
      Json.obj(
        "taxYear" -> "2018",
        "documentID" -> "id",
        "documentDate" -> "2022-06-23",
        "chargeReference" -> "chargeRef",
        "documentDescription" -> "type",
        "originalAmount" -> 1000.00,
        "totalAmount" -> 1000.00,
        "originalAmount" -> 500.00,
        "outstandingAmount" -> 500.00,
        "clearedAmount" -> 500.00,
        "chargeType" -> "POA1",
        "mainType" -> "4920",
        "mainTransaction" -> "4920",
        "accruedInterest" -> 1000,
        "items" -> Json.arr(
          Json.obj(
            "subItem" -> "1",
            "amount" -> 100.00,
            "clearingDate" -> "2022-06-23",
            "clearingReason" -> "clearingReason",
            "clearingSAPDocument" -> "012345678912",
            "outgoingPaymentMethod" -> "outgoingPaymentMethod",
            "interestLock" -> "interestLock",
            "dunningLock" -> "dunningLock",
            "paymentReference" -> "paymentReference",
            "paymentAmount" -> 2000.00,
            "dueDate" -> "2022-06-23",
            "paymentMethod" -> "paymentMethod",
            "paymentLot" -> "paymentLot",
            "paymentLotItem" -> "paymentLotItem",
            "codedOutStatus" -> "I"
          )
        )
      ),
      Json.obj(
        "taxYear" -> "2019",
        "documentID" -> "id2",
        "documentDate" -> "2022-06-23",
        "chargeReference" -> "chargeRef",
        "documentDescription" -> "type2",
        "totalAmount" -> 2000.00,
        "originalAmount" -> 500.00,
        "outstandingAmount" -> 200.00,
        "clearedAmount" -> 500.00,
        "chargeType" -> "POA1",
        "mainType" -> "4920",
        "mainTransaction" -> "4920",
        "accruedInterest" -> 2000,
        "items" -> Json.arr(
          Json.obj(
            "subItem" -> "2",
            "amount" -> 200.00,
            "clearingDate" -> "2022-06-23",
            "clearingReason" -> "clearingReason2",
            "clearingSAPDocument" -> "012345678912",
            "outgoingPaymentMethod" -> "outgoingPaymentMethod2",
            "interestLock" -> "interestLock2",
            "dunningLock" -> "dunningLock2",
            "paymentReference" -> "paymentReference2",
            "paymentAmount" -> 3000.00,
            "dueDate" -> "2022-06-23",
            "paymentMethod" -> "paymentMethod2",
            "paymentLot" -> "paymentLot2",
            "paymentLotItem" -> "paymentLotItem2",
            "codedOutStatus" -> "I"
          )
        )
      )
    )
  )
  )

  val taxpayerDetails: TaxpayerDetailsHip = TaxpayerDetailsHip(
    idType = "NINO",
    idNumber = "BB123456A",
    regimeType = "ITSA"
  )

  val balanceDetails: BalanceDetailsHip = BalanceDetailsHip(
    balanceDueWithin30Days = 100.00,
    overDueAmount = 200.00,
    totalBalance = 300.00,
    balanceNotDuein30Days = 0.00,
    unallocatedCredit = Some(400.00)
  )

  val codingDetails: CodingDetailsHip = CodingDetailsHip(
    totalLiabilityAmount = Some(2300.00),
    taxYearReturn = Some("2020")
  )

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
    latePaymentInterestAmount = Some(12.34),
    interestOutstandingAmount = Some(31.00),
    paymentLotItem = Some("paymentLotItem"),
    paymentLot = Some("paymentLot"),
    lpiWithDunningLock = Some(12.50),
    amountCodedOut = Some(3.21),
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    poaRelevantAmount = Some(1000.00)
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
    latePaymentInterestAmount = None,
    interestOutstandingAmount = None,
    paymentLotItem = None,
    paymentLot = None,
    lpiWithDunningLock = None,
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
    poaRelevantAmount = Some(1000.00)
  )

  val financialDetail: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2018",
    transactionId = "id",
    chargeReference = Some("chargeRef"),
    originalAmount = Some(BigDecimal("500.00")),
    outstandingAmount = Some(BigDecimal("500.00")),
    clearedAmount = Some(BigDecimal("500.00")),
    chargeType = Some("POA1"),
    mainType = Some("4920"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("1000")),
    items = Some(Seq(
      SubItemHip(
        subItem = Some("1"),
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
        paymentId = Some("paymentLot-paymentLotItem"),
        codedOutStatus = Some("I")
      )))
  )

  val financialDetail2: FinancialDetailHip = FinancialDetailHip(
    taxYear = "2019",
    transactionId = "id2",
    chargeReference = Some("chargeRef"),
    originalAmount = Some(BigDecimal("500.00")),
    outstandingAmount = Some(BigDecimal("200.00")),
    clearedAmount = Some(BigDecimal("500.00")),
    chargeType = Some("POA1"),
    mainType = Some("4920"),
    mainTransaction = Some("4920"),
    accruedInterest = Some(BigDecimal("2000")),
    items = Some(Seq(
      SubItemHip(
        subItem = Some("2"),
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
        codedOutStatus = Some("I")
      )))
  )


}

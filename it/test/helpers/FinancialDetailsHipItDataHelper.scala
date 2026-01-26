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

package helpers

import constants.FinancialDetailHipIntegrationTestConstants.{codingOutList, documentDetailsHip, financialDetailsHip, testBalanceHipDetails, testTaxPayerHipDetails}
import models.financialDetails.hip.model.ChargesHipResponse
import play.api.libs.json.{JsObject, JsValue, Json}

import java.time.LocalDate

trait FinancialDetailsHipItDataHelper {
  val nino = "AA123456A"
  val dateTo = "2019-12-12"
  val dateFrom = "2018-12-12"
  val documentId = "123456789"

  val queryParamsChargeDetails: Seq[(String, String)] = Seq(
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "dateFrom" -> dateFrom,
    "dateTo" -> dateTo,
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "false",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false"
  )

  val queryParamsPaymentAllocation: Seq[(String, String)] = Seq(
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "false",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false",
    "sapDocumentNumber" -> documentId
  )

  val queryParamsGetOnlyOpenItems: Seq[(String, String)] = Seq(
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "true",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false"
  )

  val chargesResponseJsonInvalid: JsValue = Json.obj("" -> "")

  val chargesHipResponse: ChargesHipResponse = ChargesHipResponse(
    taxpayerDetails = testTaxPayerHipDetails,
    balanceDetails = testBalanceHipDetails,
    documentDetails = List(documentDetailsHip),
    financialDetails = List(financialDetailsHip),
    codingDetails = codingOutList
  )

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
            "taxYearCoding" -> "2017",
            "coded" -> Json.obj(
              "amount" -> 1500,
              "initiationDate" -> "2023-10-30"
            )
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
            "latePaymentInterestAmount" -> 201.00,
            "lpiWithDunningBlock" -> 12.50,
            "interestOutstandingAmount" -> 31.00,
            "amountCodedOut" -> 3.21,
            "accruingInterestAmount" -> 12.34
          )
        ),
        "financialDetailsItem" -> Json.arr(
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
                "subItem" -> "001",
                "dueDate" -> "2025-07-10",
                "amount" -> -300,
                "clearingDate" -> "2024-10-29",
                "clearingReason" -> "Allocated to Charge",
                "paymentReference" -> "100207948",
                "paymentAmount" -> 500,
                "paymentMethod" -> "PAYMENTS MADE BY CHEQUE",
                "paymentLot" -> "24714",
                "paymentLotItem" -> "000002",
                "clearingSAPDocument" -> "003400044065",
                "codedOutStatus" -> "S"

              )
            )
          )
        ),
      )
    )


}

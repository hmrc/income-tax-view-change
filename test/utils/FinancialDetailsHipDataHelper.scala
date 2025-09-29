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

package utils

import constants.FinancialDataTestConstants.{documentDetailsHip, financialDetailsHip, testBalanceHipDetails, testCodingDetailsHip, testTaxPayerHipDetails}
import models.financialDetails.hip.model.{ChargesHipResponse, DocumentDetailHip, FinancialDetailHip}

trait FinancialDetailsHipDataHelper {

  val testNino: String = "AA123456A"
  val testFromDate: String = "2021-12-12"
  val testToDate: String = "2022-12-12"
  val testDocumentId: String = "123456789" // <== same as transactionId

  val queryParametersOnlyOpenItemsTrueHip: Seq[(String, String)] = Seq(
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

  val queryParametersPaymentAllocationHip: Seq[(String, String)] = Seq(
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "false",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false",
    "sapDocumentNumber" -> testDocumentId
  )

  val expectedQueryParameters: Seq[(String, String)] = Seq(
    "dateFrom" -> testFromDate,
    "dateTo" -> testToDate,
    "onlyOpenItems" -> "false",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePaymentonAccount" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "regimeType" -> "ITSA"
  )

  val expectedQueryParametersWithDocumentId: Seq[(String, String)] = Seq(
    "sapDocumentNumber" -> testDocumentId,
    "onlyOpenItems" -> "false",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePaymentonAccount" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "regimeType" -> "ITSA"
  )

  val expectedOnlyOpenItemsQueryParameters: Seq[(String, String)] = Seq(
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

  // Charges defs
  val documentDetails: List[DocumentDetailHip] = List(documentDetailsHip)

  val financialDetails: List[FinancialDetailHip] = List(financialDetailsHip)

  val chargeHipDef : ChargesHipResponse = ChargesHipResponse(testTaxPayerHipDetails,
    testBalanceHipDetails, List(testCodingDetailsHip),
    documentDetails, financialDetails)
  val chargeHipDefAvailableCredit : ChargesHipResponse = ChargesHipResponse(testTaxPayerHipDetails,
    testBalanceHipDetails.copy(totalCreditAvailableForRepayment = None, availableCredit = Some(99.00)), List(testCodingDetailsHip),
    documentDetails, financialDetails)
  val chargeHipDefTotalAvailableCredit : ChargesHipResponse = ChargesHipResponse(testTaxPayerHipDetails,
    testBalanceHipDetails.copy(totalCreditAvailableForRepayment = Some(100.00), availableCredit = None), List(testCodingDetailsHip),
    documentDetails, financialDetails)
  val chargeHipDefBothAvailableCredits : ChargesHipResponse = ChargesHipResponse(testTaxPayerHipDetails,
    testBalanceHipDetails.copy(totalCreditAvailableForRepayment = Some(99.00), availableCredit = Some(99.00)), List(testCodingDetailsHip),
    documentDetails, financialDetails)

  val successResponse: Right[Nothing, ChargesHipResponse] = Right(chargeHipDef)
  val successResponseTotalAvailableCredit: Right[Nothing, ChargesHipResponse] = Right(chargeHipDefTotalAvailableCredit)
  val successResponseAvailableCredit: Right[Nothing, ChargesHipResponse] = Right(chargeHipDefAvailableCredit)
  val successResponseBothAvailableCredits: Right[Nothing, ChargesHipResponse] = Right(chargeHipDefBothAvailableCredits)


}

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

import constants.FinancialDetailHipIntegrationTestConstants.{codingOutList,
  documentDetailsHip, financialDetailsHip, testBalanceHipDetails,
  testTaxPayerHipDetails}
import models.financialDetails.hip.model.ChargesHipResponse
import play.api.libs.json.{JsValue, Json}

trait FinancialDetailsHipItDataHelper {
  val nino = "AA123456A"
  val dateTo = "2019-12-12"
  val dateFrom = "2018-12-12"
  val documentId = "123456789"

  val queryParamsChargeDetails: Seq[(String, String)] = Seq(
    "dateFrom" -> dateFrom,
    "dateTo" -> dateTo,
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
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

}

/*
 * Copyright 2017 HM Revenue & Customs
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

import models.hip.repayments.{CreditReason, RepaymentViewerDetail, ResponseDetails, SuccessfulRepaymentResponse, TransactionHeader}
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime, LocalTime}

object HipRepaymentHistoryDetailsIntegrationTestConstants {

  val hipRepaymentHistoryList: SuccessfulRepaymentResponse = SuccessfulRepaymentResponse(
    TransactionHeader(status = "OK", processingDate = LocalDateTime.of(LocalDate.of(2025, 12, 17), LocalTime.of(9, 30, 17))),
    responseDetails = ResponseDetails(
      Seq(RepaymentViewerDetail(
        repaymentRequestNumber = "000000003135",
        actor = "Taxpayer",
        channel = "CESA Return",
        status = "Approved",
        amountRequested = BigDecimal(705.2),
        amountApprovedforRepayment = Some(BigDecimal(705.2)),
        totalAmountforRepaymentSupplement = Some(BigDecimal(100.2)),
        totalRepaymentAmount = Some(BigDecimal(12345)),
        repaymentMethod = Some("BACS"),
        creationDate = Some(LocalDate.parse("2021-07-21")),
        estimatedRepaymentDate = Some(LocalDate.parse("2021-07-23")),
        repaymentItems = Some(Seq(models.hip.repayments.RepaymentItem(
          creditItems = None,
          paymentItems = None,
          creditReasons = Some(Seq(CreditReason(
            creditReference = Some("002420002231000"),
            creditReason = "Credit",
            receivedDate = Some(LocalDate.parse("2021-07-23")),
            edp = Some(LocalDate.parse("2021-07-23")),
            amount = Some(700),
            originalChargeReduced = Some("Original Charge Reduced "),
            amendmentDate = Some(LocalDate.parse("2021-07-23")),
            taxYear = Some("2020")
          ))),
          repaymentSupplementItem = Some(Seq(models.hip.repayments.RepaymentSupplementItem(
            creditReference = Some("002420002231"),
            parentCreditReference = Some("002420002231"),
            amount = Some(BigDecimal(700)),
            fromDate = Some(LocalDate.parse("2021-07-23")),
            toDate = Some(LocalDate.parse("2021-08-23")),
            rate = Some(BigDecimal(12.12))
          )))
        )))
      )
      )
    )
  )

  val hipRepaymentHistorySingleItem: SuccessfulRepaymentResponse = SuccessfulRepaymentResponse(
    TransactionHeader(status = "OK", processingDate = LocalDateTime.of(LocalDate.of(2025, 12, 17), LocalTime.of(9, 30, 17))),
    responseDetails = ResponseDetails(
      Seq(RepaymentViewerDetail(
        repaymentRequestNumber = "000000003135",
        actor = "Taxpayer",
        channel = "CESA Return",
        status = "Approved",
        amountRequested = BigDecimal(200),
        amountApprovedforRepayment = Some(BigDecimal(200)),
        totalAmountforRepaymentSupplement = Some(BigDecimal(200)),
        totalRepaymentAmount = Some(BigDecimal(200)),
        repaymentMethod = Some("CARD"),
        creationDate = Some(LocalDate.parse("2020-12-03")),
        estimatedRepaymentDate = Some(LocalDate.parse("2020-12-05")),
        repaymentItems = Some(Seq(models.hip.repayments.RepaymentItem(
          creditItems = None,
          paymentItems = None,
          creditReasons = Some(Seq(CreditReason(
            creditReference = None,
            creditReason = "Credit",
            receivedDate = None,
            edp = None,
            amount = None,
            originalChargeReduced = None,
            amendmentDate = None,
            taxYear = None
          ))),
          repaymentSupplementItem = Some(Seq(models.hip.repayments.RepaymentSupplementItem(
            creditReference = Some("002420002231"),
            parentCreditReference = Some("002420002231"),
            amount = Some(BigDecimal(200)),
            fromDate = Some(LocalDate.parse("2020-12-01")),
            toDate = Some(LocalDate.parse("2020-12-03")),
            rate = Some(BigDecimal(7.25))
          )))
        )))
      )
      )
    )
  )

}

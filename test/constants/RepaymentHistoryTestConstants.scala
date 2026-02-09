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

import models.hip.repayments.{CreditReason, RepaymentViewerDetail, ResponseDetails, SuccessfulRepaymentResponse, TransactionHeader}
import models.repaymentHistory.*
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime, LocalTime}

object RepaymentHistoryTestConstants {


  val nino: String = "AA000000B"
  val repaymentId = "012345678912"
  val fromDate = "2021-11-25"
  val toDate = "2022-11-25"

  val repaymentHistoryFullJson: JsValue =
    Json.obj(
      "repaymentsViewerDetails" ->
        Json.arr(
          Json.obj(
            "repaymentRequestNumber" -> "000000003135",
            "amountApprovedforRepayment" -> Some(100.0),
            "amountRequested" -> 200.0,
            "repaymentMethod" -> "BACD",
            "totalRepaymentAmount" -> 300.0,
            "repaymentItems" -> Json.arr(
              Json.obj(
                "repaymentSupplementItem" -> Json.arr(
                  Json.obj(
                    "parentCreditReference" -> Some("002420002231"),
                    "amount" -> Some(400.0),
                    "fromDate" -> Some(LocalDate.parse("2021-07-23")),
                    "toDate" -> Some(LocalDate.parse("2021-08-23")),
                    "rate" -> Some(500.0)
                  )
                )
              )
            ),
            "estimatedRepaymentDate" -> LocalDate.parse("2021-08-11"),
            "creationDate" -> LocalDate.parse("2020-12-06"),
            "status" -> "A"
          )
        )
    )

  val testValidMultipleRepaymentHistoryModelJson: JsValue = Json.obj(
    "repaymentsViewerDetails" ->
      Json.arr(
        Json.obj(
          "repaymentRequestNumber" -> "000000003135",
          "amountApprovedforRepayment" -> Some(100.0),
          "amountRequested" -> 200.0,
          "repaymentMethod" -> "BACD",
          "totalRepaymentAmount" -> 300.0,
          "items" -> Json.arr(
            Json.obj(
              "parentCreditReference" -> Some("002420002231"),
              "amount" -> Some(400.0),
              "fromDate" -> Some(LocalDate.parse("2021-07-23")),
              "toDate" -> Some(LocalDate.parse("2021-08-23")),
              "rate" -> Some(500.0)
            )
          ),
          "estimatedRepaymentDate" -> LocalDate.parse("2021-08-01"),
          "creationDate" -> LocalDate.parse("2021-08-01")
        ),
        Json.obj(
          "repaymentRequestNumber" -> "000000003135",
          "amountApprovedforRepayment" -> Some(100.0),
          "amountRequested" -> 200.0,
          "repaymentMethod" -> "BACD",
          "totalRepaymentAmount" -> 300.0,
          "items" -> Json.arr(
            Json.obj(
              "parentCreditReference" -> Some("002420002231"),
              "amount" -> Some(400.0),
              "fromDate" -> Some(LocalDate.parse("2021-07-23")),
              "toDate" -> Some(LocalDate.parse("2021-08-23")),
              "rate" -> Some(500.0)
            )
          ),
          "estimatedRepaymentDate" -> LocalDate.parse("2021-08-03"),
          "creationDate" -> LocalDate.parse("2020-12-03")
        )
      )
  )

  val repaymentHistoryDetail: RepaymentHistory = RepaymentHistory(
    repaymentRequestNumber = "000000003135",
    amountApprovedforRepayment = Some(100.0),
    amountRequested = 200.0,
    repaymentMethod = Some("BACD"),
    totalRepaymentAmount = Some(300.0),
    repaymentItems = Some(Seq[RepaymentItem](
      RepaymentItem(
        repaymentSupplementItem =
          Some(Seq(
            RepaymentSupplementItem(
              parentCreditReference = Some("002420002231"),
              amount = Some(400.0),
              fromDate = Some(LocalDate.parse("2021-07-23")),
              toDate = Some(LocalDate.parse("2021-08-23")),
              rate = Some(500.0)
            )
          )
          )))
    ),
    estimatedRepaymentDate = Some(LocalDate.parse("2021-08-11")),
    creationDate = Some(LocalDate.parse("2020-12-03")),
    status = "A"
  )


  val hipRepaymentHistoryList: SuccessfulRepaymentResponse = SuccessfulRepaymentResponse(
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
      ),
        RepaymentViewerDetail(
          repaymentRequestNumber = "000000003136",
          actor = "Taxpayer",
          channel = "CESA Return",
          status = "Approved",
          amountRequested = BigDecimal(200),
          amountApprovedforRepayment = Some(BigDecimal(200)),
          totalAmountforRepaymentSupplement = Some(BigDecimal(200)),
          totalRepaymentAmount = Some(BigDecimal(200)),
          repaymentMethod = Some("CARD"),
          creationDate = Some(LocalDate.parse("2021-12-03")),
          estimatedRepaymentDate = Some(LocalDate.parse("2021-12-05")),
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
              creditReference = Some("002420002274"),
              parentCreditReference = Some("002420002274"),
              amount = Some(BigDecimal(200)),
              fromDate = Some(LocalDate.parse("2021-12-01")),
              toDate = Some(LocalDate.parse("2021-12-03")),
              rate = Some(BigDecimal(7.25))
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

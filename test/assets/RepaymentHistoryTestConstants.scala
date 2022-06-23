/*
 * Copyright 2022 HM Revenue & Customs
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

package assets

import models.repaymentHistory.{RepaymentHistory, RepaymentSupplementItem}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

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
            "amountApprovedforRepayment" -> Some(100.0),
            "amountRequested" -> 200.0,
            "repaymentMethod" -> "BACD",
            "totalRepaymentAmount" -> 300.0,
            "items" -> Json.arr(
              Json.obj(
                "parentCreditReference" -> Some("002420002231"),
                "amount" -> Some(400.0),
                "fromDate" -> Some("2021-07-23"),
                "toDate" -> Some("2021-08-23"),
                "rate" -> Some(500.0)
              )
            )
          )
        )
    )

  val testValidMultipleRepaymentHistoryModelJson: JsValue = Json.obj(
    "repaymentsViewerDetails" ->
      Json.arr(
        Json.obj(
          "amountApprovedforRepayment" -> Some(100.0),
          "amountRequested" -> 200.0,
          "repaymentMethod" -> "BACD",
          "totalRepaymentAmount" -> 300.0,
          "items" -> Json.arr(
            Json.obj(
              "parentCreditReference" -> Some("002420002231"),
              "amount" -> Some(400.0),
              "fromDate" -> Some("2021-07-23"),
              "toDate" -> Some("2021-08-23"),
              "rate" -> Some(500.0)
            )
          )
        ),
        Json.obj(
          "amountApprovedforRepayment" -> Some(100.0),
          "amountRequested" -> 200.0,
          "repaymentMethod" -> "BACD",
          "totalRepaymentAmount" -> 300.0,
          "items" -> Json.arr(
            Json.obj(
              "parentCreditReference" -> Some("002420002231"),
              "amount" -> Some(400.0),
              "fromDate" -> Some("2021-07-23"),
              "toDate" -> Some("2021-08-23"),
              "rate" -> Some(500.0)
            )
          )
        )
      )
  )

  val repaymentHistoryDetail: RepaymentHistory = RepaymentHistory(
    amountApprovedforRepayment = Some(100.0),
    amountRequested = 200.0,
    repaymentMethod = "BACD",
    totalRepaymentAmount = 300.0,
    items = Some(Seq(
      RepaymentSupplementItem(
        parentCreditReference = Some("002420002231"),
        amount = Some(400.0),
        fromDate = Some(LocalDate.parse("2021-07-23")),
        toDate = Some(LocalDate.parse("2021-08-23")),
        rate = Some(500.0)
      )
    )))

}
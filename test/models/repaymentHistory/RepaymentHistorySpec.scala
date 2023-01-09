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

package models.repaymentHistory

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsSuccess, JsValue, Json}

import java.time.LocalDate

class RepaymentHistorySpec extends WordSpec with Matchers {

  val repaymentHistoryFull: RepaymentHistory = RepaymentHistory(
    amountApprovedforRepayment = Some(705.2),
    amountRequested = 705.2,
    repaymentMethod = Some("BACS"),
    totalRepaymentAmount = Some(1234.56),
    repaymentItems = Some(Seq[RepaymentItem](
      RepaymentItem(
        Some(Seq(
          RepaymentSupplementItem(
            parentCreditReference = Some("002420002231"),
            amount = Some(700.0),
            fromDate = Some(LocalDate.parse("2021-07-23")),
            toDate = Some(LocalDate.parse("2021-08-23")),
            rate = Some(12.12)
          )
        )
      )))
    ),
    estimatedRepaymentDate = Some(LocalDate.parse("2021-07-23")),
    creationDate = Some(LocalDate.parse("2021-07-21")),
    repaymentRequestNumber = "000000003135"
  )

  val repaymentHistoryFullJson: JsValue = Json.obj(
    "amountApprovedforRepayment" -> Some(705.2),
    "amountRequested" -> 705.2,
    "repaymentMethod" -> Some("BACS"),
    "totalRepaymentAmount" -> Some(1234.56),
    "repaymentItems" -> Json.arr(
      Json.obj("creditItems" -> Json.arr(
        Json.obj(
          "creditReference" -> "002420002231",
          "creditChargeName" -> "creditChargeName",
          "amount" -> 700.0,
          "creationDate" -> LocalDate.parse("2021-07-23"),
          "taxYear" -> "2021"
        ))),
      Json.obj("paymentItems" -> Json.arr(
        Json.obj("paymentReference" -> "002420002231000",
          "amount" -> 700.0,
          "paymentSource" -> "Payment Source",
          "edp" -> LocalDate.parse("2021-07-23"))

      )),
      Json.obj("creditReasons" -> Json.arr(
        Json.obj(
          "creditReference" -> "002420002231",
          "creditReason" -> "Voluntary payment",
          "receivedDate" -> LocalDate.parse("2021-07-23"),
          "edp" -> LocalDate.parse("2021-07-23"),
          "amount" -> 700.0,
          "originalChargeReduced" -> "Original Charge Reduced",
          "amendmentDate" -> LocalDate.parse("2021-07-23"),
          "taxYear" -> "2020"
        ))),
      Json.obj(
        "repaymentSupplementItem" -> Some(Json.arr(
          Json.obj(
            "parentCreditReference" -> Some("002420002231"),
            "amount" -> Some(700.0),
            "fromDate" -> Some(LocalDate.parse("2021-07-23")),
            "toDate" -> Some(LocalDate.parse("2021-08-23")),
            "rate" -> Some(12.12)
          )
        ))
      )),
    "estimatedRepaymentDate" -> LocalDate.parse("2021-07-23"),
    "creationDate" -> LocalDate.parse("2021-07-21"),
    "repaymentRequestNumber" -> "000000003135"
  )

  val repaymentHistoryWithRepaymentSupplementItemJson: JsValue = Json.obj(
    "amountApprovedforRepayment" -> Some(705.2),
    "amountRequested" -> 705.2,
    "repaymentMethod" -> Some("BACS"),
    "totalRepaymentAmount" -> Some(1234.56),
    "repaymentItems" -> Json.arr(
      Json.obj(
        "repaymentSupplementItem" -> Some(Json.arr(
          Json.obj(
            "parentCreditReference" -> Some("002420002231"),
            "amount" -> Some(700.0),
            "fromDate" -> Some(LocalDate.parse("2021-07-23")),
            "toDate" -> Some(LocalDate.parse("2021-08-23")),
            "rate" -> Some(12.12)
          )
        ))
      )),
    "estimatedRepaymentDate" -> LocalDate.parse("2021-07-23"),
    "creationDate" -> LocalDate.parse("2021-07-21"),
    "repaymentRequestNumber" -> "000000003135"
  )

  "RepaymentHistory" should {
    "read from json" when {
      "the response has all possible repayment items" in {
        Json.fromJson[RepaymentHistory](repaymentHistoryFullJson) shouldBe JsSuccess(repaymentHistoryFull)
      }
    }
    "write to Json" when {
      "the model has all details" in {
        Json.toJson(repaymentHistoryFull) shouldBe repaymentHistoryWithRepaymentSupplementItemJson
      }
    }
  }
}

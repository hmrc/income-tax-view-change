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

  val repaymentHistoryNoRSI: RepaymentHistory = RepaymentHistory(
    amountApprovedforRepayment = Some(705.2),
    amountRequested = 705.2,
    repaymentMethod = Some("BACS"),
    totalRepaymentAmount = Some(1234.56),
    repaymentItems = None,
    estimatedRepaymentDate = Some(LocalDate.parse("2021-07-23")),
    creationDate = Some(LocalDate.parse("2021-07-21")),
    repaymentRequestNumber = "000000003135"
  )

  val repaymentHistoryOneRSI: RepaymentHistory = RepaymentHistory(
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

  val repaymentHistoryTwoRSI: RepaymentHistory = RepaymentHistory(
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
          ),
          RepaymentSupplementItem(
            parentCreditReference = Some("002420002232"),
            amount = Some(800.0),
            fromDate = Some(LocalDate.parse("2021-07-24")),
            toDate = Some(LocalDate.parse("2021-08-24")),
            rate = Some(13.13)

          )))))),
    estimatedRepaymentDate = Some(LocalDate.parse("2021-07-23")),
    creationDate = Some(LocalDate.parse("2021-07-21")),
    repaymentRequestNumber = "000000003135"
  )

  val repaymentHistoryAllRepaymentItemsJson: JsValue = Json.obj(
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
        ),
        Json.obj(
          "creditReference" -> "002420002232",
          "creditChargeName" -> "creditChargeName",
          "amount" -> 800.0,
          "creationDate" -> LocalDate.parse("2021-07-24"),
          "taxYear" -> "2021"
        )
      )),
      Json.obj("paymentItems" -> Json.arr(
        Json.obj("paymentReference" -> "002420002231000",
          "amount" -> 700.0,
          "paymentSource" -> "Payment Source",
          "edp" -> LocalDate.parse("2021-07-23")),
        Json.obj("paymentReference" -> "002420002231001",
          "amount" -> 800.0,
          "paymentSource" -> "Payment Source",
          "edp" -> LocalDate.parse("2021-07-24"))
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
        ),
        Json.obj(
          "creditReference" -> "002420002232",
          "creditReason" -> "Voluntary payment",
          "receivedDate" -> LocalDate.parse("2021-07-24"),
          "edp" -> LocalDate.parse("2021-07-24"),
          "amount" -> 800.0,
          "originalChargeReduced" -> "Original Charge Reduced",
          "amendmentDate" -> LocalDate.parse("2021-07-24"),
          "taxYear" -> "2020"
        )
      )),
      Json.obj(
        "repaymentSupplementItem" -> Some(Json.arr(
          Json.obj(
            "parentCreditReference" -> Some("002420002231"),
            "amount" -> Some(700.0),
            "fromDate" -> Some(LocalDate.parse("2021-07-23")),
            "toDate" -> Some(LocalDate.parse("2021-08-23")),
            "rate" -> Some(12.12)
          ),
          Json.obj(
            "parentCreditReference" -> Some("002420002232"),
            "amount" -> Some(800.0),
            "fromDate" -> Some(LocalDate.parse("2021-07-24")),
            "toDate" -> Some(LocalDate.parse("2021-08-24")),
            "rate" -> Some(13.13)
          )
        ))
      )),
    "estimatedRepaymentDate" -> LocalDate.parse("2021-07-23"),
    "creationDate" -> LocalDate.parse("2021-07-21"),
    "repaymentRequestNumber" -> "000000003135"
  )

  val repaymentHistoryNoRepaymentItemsJson: JsValue = Json.obj(
    "amountApprovedforRepayment" -> Some(705.2),
    "amountRequested" -> 705.2,
    "repaymentMethod" -> Some("BACS"),
    "totalRepaymentAmount" -> Some(1234.56),
    "estimatedRepaymentDate" -> LocalDate.parse("2021-07-23"),
    "creationDate" -> LocalDate.parse("2021-07-21"),
    "repaymentRequestNumber" -> "000000003135"
  )

  val repaymentHistoryNoRSIJson: JsValue = Json.obj(
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
        ),
        Json.obj(
          "creditReference" -> "002420002232",
          "creditChargeName" -> "creditChargeName",
          "amount" -> 800.0,
          "creationDate" -> LocalDate.parse("2021-07-24"),
          "taxYear" -> "2021"
        )
      )),
      Json.obj("paymentItems" -> Json.arr(
        Json.obj("paymentReference" -> "002420002231000",
          "amount" -> 700.0,
          "paymentSource" -> "Payment Source",
          "edp" -> LocalDate.parse("2021-07-23")),
        Json.obj("paymentReference" -> "002420002231001",
          "amount" -> 800.0,
          "paymentSource" -> "Payment Source",
          "edp" -> LocalDate.parse("2021-07-24"))
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
        ),
        Json.obj(
          "creditReference" -> "002420002232",
          "creditReason" -> "Voluntary payment",
          "receivedDate" -> LocalDate.parse("2021-07-24"),
          "edp" -> LocalDate.parse("2021-07-24"),
          "amount" -> 800.0,
          "originalChargeReduced" -> "Original Charge Reduced",
          "amendmentDate" -> LocalDate.parse("2021-07-24"),
          "taxYear" -> "2020"
        )
      )),
    ),
    "estimatedRepaymentDate" -> LocalDate.parse("2021-07-23"),
    "creationDate" -> LocalDate.parse("2021-07-21"),
    "repaymentRequestNumber" -> "000000003135"
  )


  val repaymentHistoryOneRSIJson: JsValue = Json.obj(
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

  val repaymentHistoryTwoRSIJson: JsValue = Json.obj(
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
          ),
          Json.obj(
            "parentCreditReference" -> Some("002420002232"),
            "amount" -> Some(800.0),
            "fromDate" -> Some(LocalDate.parse("2021-07-24")),
            "toDate" -> Some(LocalDate.parse("2021-08-24")),
            "rate" -> Some(13.13)
          )
        ))
      )),
    "estimatedRepaymentDate" -> LocalDate.parse("2021-07-23"),
    "creationDate" -> LocalDate.parse("2021-07-21"),
    "repaymentRequestNumber" -> "000000003135"
  )

  val repaymentHistoryMinimalJson: JsValue = Json.obj(
    "amountRequested" -> 0.00,
    "repaymentRequestNumber" -> "000000003135"
  )

  val repaymentHistoryMinimal: RepaymentHistory = RepaymentHistory(
    amountApprovedforRepayment = None,
    amountRequested = 0.00,
    repaymentMethod = None,
    totalRepaymentAmount = None,
    repaymentItems = None,
    estimatedRepaymentDate = None,
    creationDate = None,
    repaymentRequestNumber = "000000003135"
  )

  "RepaymentHistory" should {
    "read from json" when {
      "the response has multiple of all possible repayment items" in {
        Json.fromJson[RepaymentHistory](repaymentHistoryAllRepaymentItemsJson) shouldBe JsSuccess(repaymentHistoryTwoRSI)
      }
      "the response has single of all possible repayment items" in {
        Json.fromJson[RepaymentHistory](repaymentHistoryOneRSIJson) shouldBe JsSuccess(repaymentHistoryOneRSI)
      }
      "the response has multiple of creditItems, paymentItems and creditReasons but no repaymentSupplementItem" in {
        Json.fromJson[RepaymentHistory](repaymentHistoryNoRSIJson) shouldBe JsSuccess(repaymentHistoryNoRSI)
      }
      "the response is minimal" in {
        Json.fromJson[RepaymentHistory](repaymentHistoryMinimalJson) shouldBe JsSuccess(repaymentHistoryMinimal)
      }
    }
    "write to Json" when {
      "the model has multiple of all possible repayment items" in {
        Json.toJson(repaymentHistoryTwoRSI) shouldBe repaymentHistoryTwoRSIJson
      }
      "the model has single of all possible repayment items" in {
        Json.toJson(repaymentHistoryOneRSI) shouldBe repaymentHistoryOneRSIJson
      }
      "the model has multiple of creditItems, paymentItems and creditReasons but no repaymentSupplementItem" in {
        Json.toJson(repaymentHistoryNoRSI) shouldBe repaymentHistoryNoRepaymentItemsJson
      }
      "the model is minimal" in {
        Json.toJson(repaymentHistoryMinimal) shouldBe repaymentHistoryMinimalJson
      }
    }
  }
}

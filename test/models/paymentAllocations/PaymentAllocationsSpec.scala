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

package models.paymentAllocations

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, JsSuccess, Json}

class PaymentAllocationsSpec extends WordSpec with Matchers {

  val allocationDetail: AllocationDetail = AllocationDetail(
    transactionId = Some("transactionId"),
    from = Some("from"),
    to = Some("to"),
    `type` = Some("type"),
    amount = Some(1000.00),
    clearedAmount = Some(500.00)
  )

  val paymentAllocationsFull: PaymentAllocations = PaymentAllocations(
    amount = Some(500.00),
    method = Some("method"),
    transactionDate = Some("transactionDate"),
    allocations = Seq(allocationDetail)
  )

  val paymentAllocationsReadJsonFull: JsObject = Json.obj(
    "paymentDetails" -> Json.obj(
      "paymentAmount" -> 500.00,
      "paymentMethod" -> "method",
      "valueDate" -> "transactionDate",
      "sapClearingDocsDetails" -> Json.arr(
        Json.obj(
          "sapDocNumber" -> "transactionId",
          "taxPeriodStartDate" -> "from",
          "taxPeriodEndDate" -> "to",
          "chargeType" -> "type",
          "amount" -> 1000.00,
          "clearedAmount" -> 500.00
        )
      )
    )
  )

  val paymentAllocationsWriteJsonFull: JsObject = Json.obj(
    "amount" -> 500.00,
    "method" -> "method",
    "transactionDate" -> "transactionDate",
    "allocations" -> Json.arr(
      Json.obj(
        "transactionId" -> "transactionId",
        "from" -> "from",
        "to" -> "to",
        "type" -> "type",
        "amount" -> 1000.00,
        "clearedAmount" -> 500.00
      )
    )
  )

  val paymentAllocationsEmpty: PaymentAllocations = PaymentAllocations(None, None, None, Seq.empty[AllocationDetail])

  val paymentAllocationsEmptyJson: JsObject = Json.obj(
    "allocations" -> Json.arr()
  )

  "PaymentAllocations" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[PaymentAllocations](paymentAllocationsReadJsonFull) shouldBe JsSuccess(paymentAllocationsFull)
      }
      "the json is empty" in {
        Json.fromJson[PaymentAllocations](Json.obj()) shouldBe JsSuccess(paymentAllocationsEmpty)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(paymentAllocationsFull) shouldBe paymentAllocationsWriteJsonFull
      }
      "the model is empty" in {
        Json.toJson(paymentAllocationsEmpty) shouldBe paymentAllocationsEmptyJson
      }
    }
  }

}

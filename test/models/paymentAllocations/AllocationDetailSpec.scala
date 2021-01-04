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

class AllocationDetailSpec extends WordSpec with Matchers {

  val allocationDetailFull: AllocationDetail = AllocationDetail(
    transactionId = Some("transactionId"),
    from = Some("from"),
    to = Some("to"),
    `type` = Some("type"),
    amount = Some(1000.00),
    clearedAmount = Some(500.00)
  )

  val allocationDetailReadJsonFull: JsObject = Json.obj(
    "sapDocNumber" -> "transactionId",
    "taxPeriodStartDate" -> "from",
    "taxPeriodEndDate" -> "to",
    "chargeType" -> "type",
    "amount" -> 1000.00,
    "clearedAmount" -> 500.00
  )

  val allocationDetailWriteJsonFull: JsObject = Json.obj(
    "transactionId" -> "transactionId",
    "from" -> "from",
    "to" -> "to",
    "type" -> "type",
    "amount" -> 1000.00,
    "clearedAmount" -> 500.00
  )

  val allocationDetailEmpty: AllocationDetail = AllocationDetail.emptyAllocation

  val emptyJson: JsObject = Json.obj()

  "AllocationDetail" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[AllocationDetail](allocationDetailReadJsonFull) shouldBe JsSuccess(allocationDetailFull)
      }
      "the json is empty" in {
        Json.fromJson[AllocationDetail](emptyJson) shouldBe JsSuccess(allocationDetailEmpty)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(allocationDetailFull) shouldBe allocationDetailWriteJsonFull
      }
      "the model is empty" in {
        Json.toJson(allocationDetailEmpty) shouldBe emptyJson
      }
    }
  }

}

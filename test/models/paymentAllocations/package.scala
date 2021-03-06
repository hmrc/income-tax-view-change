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

package models

import play.api.libs.json.{JsObject, Json}

package object paymentAllocations {

  val allocationDetailFull: AllocationDetail = AllocationDetail(
    transactionId = Some("transactionId"),
    from = Some("from"),
    to = Some("to"),
    `type` = Some("type"),
    amount = Some(1000.00),
    clearedAmount = Some(500.00)
  )

  val allocationDetailMinimum: AllocationDetail = AllocationDetail.emptyAllocation

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

  val allocationDetailJsonMinimum: JsObject = Json.obj()

  val paymentAllocationsFull: PaymentAllocations = PaymentAllocations(
    amount = Some(500.00),
    method = Some("method"),
    reference = Some("reference"),
    transactionDate = Some("transactionDate"),
    allocations = Seq(allocationDetailFull)
  )

  val paymentAllocationsMinimum: PaymentAllocations = PaymentAllocations(
    None, None, None, None, Seq.empty[AllocationDetail]
  )

  val paymentAllocationsReadJsonFull: JsObject = Json.obj(
    "paymentAmount" -> 500.00,
    "paymentMethod" -> "method",
    "paymentReference" -> "reference",
    "valueDate" -> "transactionDate",
    "sapClearingDocsDetails" -> Json.arr(
      allocationDetailReadJsonFull
    )
  )

  val paymentAllocationsWriteJsonFull: JsObject = Json.obj(
    "amount" -> 500.00,
    "method" -> "method",
    "reference" -> "reference",
    "transactionDate" -> "transactionDate",
    "allocations" -> Json.arr(
      allocationDetailWriteJsonFull
    )
  )

  val paymentAllocationsReadJsonMinimum: JsObject = Json.obj()

  val paymentAllocationsWriteJsonMinimum: JsObject = Json.obj(
    "allocations" -> Json.arr()
  )

  val paymentDetailsFull: PaymentDetails = PaymentDetails(
    paymentDetails = Seq(paymentAllocationsFull)
  )

  val paymentDetailsMinimum: PaymentDetails = PaymentDetails(
    paymentDetails = Seq.empty[PaymentAllocations]
  )

  val paymentDetailsReadJsonFull: JsObject = Json.obj(
    "paymentDetails" -> Json.arr(paymentAllocationsReadJsonFull)
  )

  val paymentDetailsWriteJsonFull: JsObject = Json.obj(
    "paymentDetails" -> Json.arr(paymentAllocationsWriteJsonFull)
  )

  val paymentDetailsJsonMinimum: JsObject = Json.obj(
    "paymentDetails" -> Json.arr()
  )

}

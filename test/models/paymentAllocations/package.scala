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

package models

import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

package object paymentAllocations {

  val allocationDetailFull: AllocationDetail = AllocationDetail(
    transactionId = Some("transactionId"),
    from = Some(LocalDate.parse("2022-06-23")),
    to = Some(LocalDate.parse("2022-06-23")),
    chargeType = Some("type"),
    mainType = Some("mainType"),
    amount = Some(1000.00),
    clearedAmount = Some(500.00),
    chargeReference = Some("chargeReference")
  )

  val allocationDetailMinimum: AllocationDetail = AllocationDetail.emptyAllocation

  val allocationDetailReadJsonFull: JsObject = Json.obj(
    "sapDocNumber" -> "transactionId",
    "taxPeriodStartDate" -> LocalDate.parse("2022-06-23"),
    "taxPeriodEndDate" -> LocalDate.parse("2022-06-23"),
    "chargeType" -> "type",
    "mainType" -> "mainType",
    "amount" -> 1000.00,
    "clearedAmount" -> 500.00,
    "chargeReference" -> "chargeReference"
  )

  val allocationDetailWriteJsonFull: JsObject = Json.obj(
    "transactionId" -> "transactionId",
    "from" -> LocalDate.parse("2022-06-23"),
    "to" -> LocalDate.parse("2022-06-23"),
    "chargeType" -> "type",
    "mainType" -> "mainType",
    "amount" -> 1000.00,
    "clearedAmount" -> 500.00,
    "chargeReference" -> "chargeReference"
  )

  val allocationDetailJsonMinimum: JsObject = Json.obj()

  val paymentAllocationsFull: PaymentAllocations = PaymentAllocations(
    amount = Some(500.00),
    method = Some("method"),
    reference = Some("reference"),
    transactionDate = Some(LocalDate.parse("2022-06-23")),
    allocations = Seq(allocationDetailFull)
  )

  val paymentAllocationsMinimum: PaymentAllocations = PaymentAllocations(
    None, None, None, None, Seq.empty[AllocationDetail]
  )

  val paymentAllocationsReadJsonFull: JsObject = Json.obj(
    "paymentAmount" -> 500.00,
    "paymentMethod" -> "method",
    "paymentReference" -> "reference",
    "valueDate" -> LocalDate.parse("2022-06-23"),
    "sapClearingDocsDetails" -> Json.arr(
      allocationDetailReadJsonFull
    )
  )

  val paymentAllocationsWriteJsonFull: JsObject = Json.obj(
    "amount" -> 500.00,
    "method" -> "method",
    "reference" -> "reference",
    "transactionDate" -> LocalDate.parse("2022-06-23"),
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

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

package models.financialDetails

import assets.FinancialDataTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsSuccess, JsValue, Json}

import java.time.LocalDate

class FinancialDetailSpec extends AnyWordSpec with Matchers {

  val financialDetailEmpty: FinancialDetail = FinancialDetail("2019", "id", None, None, None, None, None, None, None, None, None, None)

  val financialDetailEmptyJsonRead: JsObject = Json.obj("taxYear" -> "2019", "documentId" -> "id")
  val financialDetailEmptyJsonWrite: JsObject = Json.obj("taxYear" -> "2019", "transactionId" -> "id")

  val financialDetailFullJson: JsValue = Json.obj(
    "taxYear" -> "2018",
    "documentId" -> "id",
    "documentDate" -> LocalDate.parse("2022-06-23"),
    "documentDescription" -> "type",
    "totalAmount" -> 1000.00,
    "originalAmount" -> 500.00,
    "clearedAmount" -> 500.00,
    "documentOutstandingAmount" -> 500.00,
    "chargeType" -> "POA1",
    "mainType" -> "4920",
    "accruedInterest" -> 1000,
    "items" -> Json.arr(
      Json.obj(
        "subItem" -> "1",
        "amount" -> 100.00,
        "clearingDate" -> LocalDate.parse("2022-06-23"),
        "clearingReason" -> "clearingReason",
        "clearingSAPDocument" -> "012345678912",
        "outgoingPaymentMethod" -> "outgoingPaymentMethod",
        "interestLock" -> "interestLock",
        "dunningLock" -> "dunningLock",
        "paymentReference" -> "paymentReference",
        "paymentAmount" -> 2000.00,
        "dueDate" -> LocalDate.parse("2022-06-23"),
        "paymentMethod" -> "paymentMethod",
        "paymentLot" -> "paymentLot",
        "paymentLotItem" -> "paymentLotItem"
      )
    )
  )

  "FinancialDetail" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[FinancialDetail](financialDetailFullJson) shouldBe JsSuccess(financialDetail)
      }
      "the json is minimal" in {
        Json.fromJson[FinancialDetail](financialDetailEmptyJsonRead) shouldBe JsSuccess(financialDetailEmpty)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(financialDetail) shouldBe validFinancialDetailJsonAfterWrites
      }
      "the model is empty" in {
        Json.toJson(financialDetailEmpty) shouldBe financialDetailEmptyJsonWrite
      }
    }
  }
}

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

import constants.FinancialDataTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsSuccess, JsValue, Json}

import java.time.LocalDate

class DocumentDetailSpec extends AnyWordSpec with Matchers {

  val documentDetailMin: DocumentDetail = DocumentDetail(2019, "id", None, None, 1000.00, 300.00,
    LocalDate.parse("2018-03-29"), None, None, None, None, None, None, None, None, None, None,
    effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")), None, Some(1000.00))

  val documentDetailMinJsonRead: JsObject =
    Json.obj("taxYear" -> "2019", "documentId" -> "id", "totalAmount" -> 1000.00,
      "documentOutstandingAmount" -> 300.00, "documentDate" -> LocalDate.parse("2018-03-29"),
      "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"), "poaRelevantAmount" -> Some(1000.00))

  val documentDetailMinJsonWrite: JsObject =
    Json.obj("taxYear" -> 2019, "transactionId" -> "id", "originalAmount" -> 1000.00, "outstandingAmount" -> 300.00,
      "documentDate" -> LocalDate.parse("2018-03-29"), "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"), "poaRelevantAmount" -> Some(1000.00))

  val documentDetailFull: DocumentDetail = documentDetail

  val documentDetailFullJsonRead: JsValue = Json.obj(
    "taxYear" -> "2018",
    "documentId" -> "id",
    "documentDescription" -> "documentDescription",
    "documentText" -> "documentText",
    "totalAmount" -> 300.00,
    "documentOutstandingAmount" -> 200.00,
    "documentDate" -> LocalDate.parse("2018-03-29"),
    "interestRate" -> 2.60,
    "interestFromDate" -> LocalDate.parse("2018-08-01"),
    "interestEndDate" -> LocalDate.parse("2019-01-15"),
    "latePaymentInterestID" -> "latePaymentInterestID",
    "latePaymentInterestAmount" -> 12.34,
    "interestOutstandingAmount" -> 31.00,
    "paymentLotItem" -> "paymentLotItem",
    "paymentLot" -> "paymentLot",
    "lpiWithDunningBlock" -> 12.50,
    "amountCodedOut" -> 3.21,
    "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
    "documentDueDate" -> LocalDate.parse("2019-03-29"),
    "poaRelevantAmount" -> Some(1000.00)
  )

  val documentDetailFullJsonWrite: JsValue = Json.obj(
    "taxYear" -> 2018,
    "transactionId" -> "id",
    "documentDescription" -> "documentDescription",
    "documentText" -> "documentText",
    "originalAmount" -> 300.00,
    "outstandingAmount" -> 200.00,
    "documentDate" -> LocalDate.parse("2018-03-29"),
    "interestRate" -> 2.60,
    "interestFromDate" -> LocalDate.parse("2018-08-01"),
    "interestEndDate" -> LocalDate.parse("2019-01-15"),
    "latePaymentInterestId" -> "latePaymentInterestID",
    "latePaymentInterestAmount" -> 12.34,
    "interestOutstandingAmount" -> 31.00,
    "paymentLotItem" -> "paymentLotItem",
    "paymentLot" -> "paymentLot",
    "lpiWithDunningBlock" -> 12.50,
    "amountCodedOut" -> 3.21,
    "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
    "documentDueDate" -> LocalDate.parse("2019-03-29"),
    "poaRelevantAmount" -> Some(1000.00)
  )

  val documentDetailLpiWithDunningLockJsonRead: JsValue = Json.obj("taxYear" -> "2018", "documentId" -> "id", "totalAmount" -> 1000.00,
    "documentOutstandingAmount" -> 300.00, "documentDate" -> LocalDate.parse("2018-03-29"), "lpiWithDunningLock" -> 13.70,
    "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"), "poaRelevantAmount" -> Some(1000.00))

  val documentDetailLpiWithDunningBlock: DocumentDetail = DocumentDetail(2018, "id", None, None, 1000.00, 300.00,LocalDate.parse("2018-03-29"),
    None, None, None, None, None, None, None, None, lpiWithDunningBlock = Some(13.70), effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")), poaRelevantAmount = Some(1000.00))

  val documentDetailLpiWithDunningBlockJsonWrite: JsObject =
    Json.obj("taxYear" -> "2018", "transactionId" -> "id", "originalAmount" -> 1000.00,
      "outstandingAmount" -> "300.00", "documentDate" -> LocalDate.parse("2018-03-29"), "lpiWithDunningBlock" -> 13.70)


  "DocumentDetail" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[DocumentDetail](documentDetailFullJsonRead) shouldBe JsSuccess(documentDetailFull)
      }
      "the json is minimal" in {
        Json.fromJson[DocumentDetail](documentDetailMinJsonRead) shouldBe JsSuccess(documentDetailMin)
      }
      "the json contains r7c lpiWithDunningLock but not lpiWithDunningBlock (legacy)" in {
        Json.fromJson[DocumentDetail](documentDetailLpiWithDunningLockJsonRead) shouldBe JsSuccess(documentDetailLpiWithDunningBlock)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(documentDetailFull) shouldBe documentDetailFullJsonWrite
      }
      "the model is empty" in {
        Json.toJson(documentDetailMin) shouldBe documentDetailMinJsonWrite
      }
    }
  }
}

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

package models.financialDetails

import assets.FinancialDataTestConstants._
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, JsSuccess, JsValue, Json}

import java.time.LocalDate

class DocumentDetailSpec extends WordSpec with Matchers {

  val documentDetailMin: DocumentDetail = DocumentDetail("2019", "id", None, None, None, None, LocalDate.parse("2018-03-29"),
    None, None, None, None, None, None, None, None, None)

  val documentDetailMinJsonRead: JsObject =
    Json.obj("taxYear" -> "2019", "documentId" -> "id", "documentDate" -> LocalDate.parse("2018-03-29"))

  val documentDetailMinJsonWrite: JsObject =
    Json.obj("taxYear" -> "2019", "transactionId" -> "id", "documentDate" -> LocalDate.parse("2018-03-29"))

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
    "accruingInterestAmount" -> 1.27
  )

  val documentDetailFullJsonWrite: JsValue = Json.obj(
    "taxYear" -> "2018",
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
    "accruingInterestAmount" -> 1.27
  )

  val documentDetailLpiWithDunningLockJsonRead: JsValue = Json.obj("taxYear" -> "2018", "documentId" -> "id", "documentDate" -> LocalDate.parse("2018-03-29"), "lpiWithDunningLock" -> 13.70)

  val documentDetailLpiWithDunningBlock: DocumentDetail = DocumentDetail("2018", "id", None, None, None, None, LocalDate.parse("2018-03-29"),
    None, None, None, None, None, None, None, None, lpiWithDunningBlock = Some(13.70))

  val documentDetailLpiWithDunningBlockJsonWrite: JsObject =
    Json.obj("taxYear" -> "2018", "transactionId" -> "id", "documentDate" -> LocalDate.parse("2018-03-29"), "lpiWithDunningBlock" -> 13.70)


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
      "the model converts r7c lpiWithDunningLock to lpiWithDunningBlock (legacy)" in {
        Json.toJson(documentDetailLpiWithDunningBlock) shouldBe documentDetailLpiWithDunningBlockJsonWrite
      }
    }
  }
}

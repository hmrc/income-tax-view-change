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
import models.financialDetails.hip.model.DocumentDetailHip
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsSuccess, JsValue, Json}

import java.time.LocalDate

class DocumentDetailSpec extends AnyWordSpec with Matchers {

  val documentDetailMin: DocumentDetailHip = DocumentDetailHip(taxYear=2019, transactionId="id", documentDate=LocalDate.parse("2018-03-29"), documentText=None, None, documentDescription=None, originalAmount=1001.00, outstandingAmount=300.00,
    Some(1000.00), None, None, None, effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")), None, None, None, None, None, None,
     None, None)

  val documentDetailMinJsonRead: JsObject =
    Json.obj("taxYear" -> "2019", "documentID" -> "id", "totalAmount" -> 1001.00,
      "documentOutstandingAmount" -> 300.00, "documentDate" -> LocalDate.parse("2018-03-29"),
      "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"), "poaRelevantAmount" -> Some(1000.00))

  val documentDetailMinJsonWrite: JsObject =
    Json.obj("taxYear" -> 2019, "transactionId" -> "id", "originalAmount" -> 1001.00, "outstandingAmount" -> 300.00,
      "documentDate" -> LocalDate.parse("2018-03-29"), "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"), "poaRelevantAmount" -> Some(1000.00))

  val documentDetailFull: DocumentDetailHip = documentDetail

  val documentDetailFullJsonRead: JsValue = Json.obj(
    "taxYear" -> "2018",
    "documentID" -> "id",
    "documentDescription" -> "documentDescription",
    "documentText" -> "documentText",
    "totalAmount" -> 300.00,
    "documentOutstandingAmount" -> 200.00,
    "documentDate" -> LocalDate.parse("2018-03-29"),
    "interestRate" -> 2.60,
    "interestFromDate" -> LocalDate.parse("2018-08-01"),
    "interestEndDate" -> LocalDate.parse("2019-01-15"),
    "latePaymentInterestID" -> "latePaymentInterestID",
    "interestOutstandingAmount" -> 31.00,
    "paymentLotItem" -> "paymentLotItem",
    "paymentLot" -> "paymentLot",
    "lpiWithDunningBlock" -> 12.50,
    "amountCodedOut" -> 3.21,
    "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
    "documentDueDate" -> LocalDate.parse("2019-03-29"),
    "poaRelevantAmount" -> Some(1000.00),
    "accruingInterestAmount" -> 12.34
  )

  val documentDetailFullJsonWrite: JsValue = Json.obj(
    "taxYear" -> 2018,
    "transactionId" -> "id",
    "documentDate" -> LocalDate.parse("2018-03-29"),
    "documentDescription" -> "documentDescription",
    "documentText" -> "documentText",
    "originalAmount" -> 300.00,
    "outstandingAmount" -> 200.00,
    "interestRate" -> 2.60,
    "interestFromDate" -> LocalDate.parse("2018-08-01"),
    "interestEndDate" -> LocalDate.parse("2019-01-15"),
    "latePaymentInterestId" -> "latePaymentInterestID",
    "interestOutstandingAmount" -> 31.00,
    "paymentLotItem" -> "paymentLotItem",
    "paymentLot" -> "paymentLot",
    "lpiWithDunningLock" -> 12.50,
    "amountCodedOut" -> 3.21,
    "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
    "documentDueDate" -> LocalDate.parse("2019-03-29"),
    "poaRelevantAmount" -> Some(1000.00),
    "accruingInterestAmount" -> 12.34
  )

  val documentDetailLpiWithDunningLockJsonRead: JsValue = Json.obj("taxYear" -> "2018", "documentID" -> "id", "totalAmount" -> 1000.00,
    "documentOutstandingAmount" -> 300.00, "documentDate" -> LocalDate.parse("2018-03-29"), "lpiWithDunningLock" -> 13.70,
    "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"), "poaRelevantAmount" -> Some(1000.00))

  val documentDetailLpiWithDunningBlock: DocumentDetailHip = DocumentDetailHip(taxYear=2018, transactionId="id", documentDate=LocalDate.parse("2018-03-29"), documentText=None, documentDueDate=None, documentDescription=None, originalAmount=1000.00, outstandingAmount=300.00, poaRelevantAmount = Some(1000.00),
    lastClearedAmount=None, paymentLot=None, paymentLotItem=None, effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")), lpiWithDunningLock = Some(13.70))

  val documentDetailLpiWithDunningBlockJsonWrite: JsObject =
    Json.obj("taxYear" -> "2018", "transactionId" -> "id", "originalAmount" -> 1000.00,
      "outstandingAmount" -> "300.00", "documentDate" -> LocalDate.parse("2018-03-29"), "lpiWithDunningBlock" -> 13.70)


  "DocumentDetail" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[DocumentDetailHip](documentDetailFullJsonRead) shouldBe JsSuccess(documentDetailFull)
      }
      "the json is minimal" in {
        Json.fromJson[DocumentDetailHip](documentDetailMinJsonRead) shouldBe JsSuccess(documentDetailMin)
      }
      "the json contains r7c lpiWithDunningLock but not lpiWithDunningBlock (legacy)" in {
        Json.fromJson[DocumentDetailHip](documentDetailLpiWithDunningLockJsonRead) shouldBe JsSuccess(documentDetailLpiWithDunningBlock)
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

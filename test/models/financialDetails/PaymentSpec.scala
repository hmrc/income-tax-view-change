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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

class PaymentSpec extends AnyWordSpec with Matchers {

  val paymentEmpty: Payment = Payment(None, 1000.00, 300.00, None, None, None, None, None, LocalDate.parse("2022-06-23"), "DOCID01", None, None)

  val paymentEmptyJson: JsObject = Json.obj("amount" -> 1000.00, "outstandingAmount" -> 300.00, "documentDate" -> LocalDate.parse("2022-06-23"), "transactionId" -> "DOCID01")

  val paymentFull: Payment = Payment(
    reference = Some("reference"),
    amount = 100.00,
    outstandingAmount = 100.00,
    documentDescription = Some("documentDescriptionX"),
    method = Some("method"),
    lot = Some("lot"),
    lotItem = Some("lotItem"),
    dueDate = Some(LocalDate.parse("2022-06-23")),
    documentDate = LocalDate.parse("2022-06-23"),
    transactionId = "DOCID01",
    None,
    None
  )

  val paymentFullJson: JsObject = Json.obj(
    "reference" -> "reference",
    "amount" -> 100.00,
    "outstandingAmount" -> 100.00,
    "documentDescription" -> "documentDescriptionX",
    "method" -> "method",
    "lot" -> "lot",
    "lotItem" -> "lotItem",
    "dueDate" -> "2022-06-23",
    "documentDate" -> "2022-06-23",
    "transactionId" -> "DOCID01"
  )

  "Charge" should {
    "write to json" when {
      "the model is complete" in {
        Json.toJson(paymentFull) shouldBe paymentFullJson
      }
      "the model is empty" in {
        Json.toJson(paymentEmpty) shouldBe paymentEmptyJson
      }
    }
  }
}

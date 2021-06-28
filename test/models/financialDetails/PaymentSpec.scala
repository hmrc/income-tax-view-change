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

package models.financialDetails

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, JsSuccess, Json}

class PaymentSpec extends WordSpec with Matchers {

  val paymentEmpty: Payment = Payment(None, None, None, None, None, None, "DOCID01")

  val paymentEmptyJson: JsObject = Json.obj("transactionId" -> "DOCID01")

  val paymentFull: Payment = Payment(
    reference = Some("reference"),
    amount = Some(100.00),
    method = Some("method"),
    lot = Some("lot"),
    lotItem = Some("lotItem"),
    date = Some("date"),
		transactionId = "DOCID01"
  )

  val paymentFullJson: JsObject = Json.obj(
    "reference" -> "reference",
    "amount" -> 100.00,
    "method" -> "method",
    "lot" -> "lot",
    "lotItem" -> "lotItem",
    "date" -> "date",
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

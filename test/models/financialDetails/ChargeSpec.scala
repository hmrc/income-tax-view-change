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

import assets.FinancialDataTestConstants._
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, JsSuccess, Json}

class ChargeSpec extends WordSpec with Matchers {

  val chargeEmpty: Charge = Charge("2019", "id", None, None, None, None, None, None, None, None, None)

  val chargeEmptyJsonInput: JsObject = Json.obj("taxYear" -> "2019", "documentId" -> "id")
  val chargeEmptyJsonOutput: JsObject = Json.obj("taxYear" -> "2019", "transactionId" -> "id")

  "Charge" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[Charge](validChargeJson) shouldBe JsSuccess(charges1)
      }
      "the json is minimal" in {
        Json.fromJson[Charge](chargeEmptyJsonInput) shouldBe JsSuccess(chargeEmpty)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(charges1) shouldBe validChargeJsonAfterWrites
      }
      "the model is empty" in {
        Json.toJson(chargeEmpty) shouldBe chargeEmptyJsonOutput
      }
    }
  }
}

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

package models.paymentAllocations

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsSuccess, Json}

class PaymentDetailsSpec extends WordSpec with Matchers {

  "PaymentDetails" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[PaymentDetails](paymentDetailsReadJsonFull) shouldBe JsSuccess(paymentDetailsFull)
      }
      "the json is empty" in {
        Json.fromJson[PaymentDetails](paymentDetailsJsonMinimum) shouldBe JsSuccess(paymentDetailsMinimum)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(paymentDetailsFull) shouldBe paymentDetailsWriteJsonFull
      }
      "the model is empty" in {
        Json.toJson(paymentDetailsMinimum) shouldBe paymentDetailsJsonMinimum
      }
    }
  }

}

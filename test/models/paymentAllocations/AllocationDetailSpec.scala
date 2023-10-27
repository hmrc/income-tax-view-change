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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class AllocationDetailSpec extends AnyWordSpec with Matchers {

  "AllocationDetail" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[AllocationDetail](allocationDetailReadJsonFull) shouldBe JsSuccess(allocationDetailFull)
      }
      "the json is empty" in {
        Json.fromJson[AllocationDetail](allocationDetailJsonMinimum) shouldBe JsSuccess(allocationDetailMinimum)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(allocationDetailFull) shouldBe allocationDetailWriteJsonFull
      }
      "the model is empty" in {
        Json.toJson(allocationDetailMinimum) shouldBe allocationDetailJsonMinimum
      }
    }
  }

}

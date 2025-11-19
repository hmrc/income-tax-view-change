/*
 * Copyright 2025 HM Revenue & Customs
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

package models.incomeSourceDetails

import models.hip.GetChargeHistoryHipApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HipApiSpec extends AnyWordSpec with Matchers {
  "GetChargeHistoryHipApi" should {

    "have the correct name value" in {
      GetChargeHistoryHipApi.name shouldBe "get-charge-history"
    }

    "return the correct value when apply() is called" in {
      GetChargeHistoryHipApi.apply() shouldBe "get-charge-history"
    }

    "behave as a singleton object" in {
      val ref1 = GetChargeHistoryHipApi
      val ref2 = GetChargeHistoryHipApi

      ref1 shouldBe theSameInstanceAs(ref2)
    }
  }
}

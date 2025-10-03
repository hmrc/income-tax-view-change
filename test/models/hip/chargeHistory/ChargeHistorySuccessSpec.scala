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

package models.hip.chargeHistory

import constants.hip.ChargeHistoryTestConstants.{chargeHistorySuccess, chargeHistorySuccessJson}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class ChargeHistorySuccessSpec extends AnyWordSpec with Matchers {

  "ChargeHistorySuccess" should {
    "write to JSON" in {
      val result = Json.toJson(chargeHistorySuccess)
      result shouldBe chargeHistorySuccessJson
    }

    "read from JSON" in {
      val result = Json.fromJson[ChargeHistorySuccess](chargeHistorySuccessJson)
      result shouldBe chargeHistorySuccess
    }
  }
}

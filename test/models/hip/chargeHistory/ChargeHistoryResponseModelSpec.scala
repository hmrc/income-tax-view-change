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

import constants.hip.ChargeHistoryTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class ChargeHistoryResponseModelSpec extends AnyWordSpec with Matchers {

  "ChargeHistorySuccess" should {
    "write to JSON" in {
      val result = Json.toJson(chargeHistorySuccess)
      result shouldBe chargeHistorySuccessJsonWrites
    }

    "read from JSON" in {
      val result = Json.fromJson[ChargeHistorySuccess](chargeHistorySuccessJsonReads)
      result shouldBe JsSuccess(chargeHistorySuccess)
    }
  }

  "ChargeHistorySuccessWrapper" should {
    "write to JSON" in {
      val result = Json.toJson(chargeHistorySuccessWrapperModel)
      result shouldBe chargeHistorySuccessWrapperJsonWrites
    }

    "read to JSON" in {
      val result = Json.fromJson[ChargeHistorySuccessWrapper](chargeHistorySuccessWrapperJsonReads)
      result shouldBe JsSuccess(chargeHistorySuccessWrapperModel)
    }
  }

  ".toChargeHistoryModel" should {
    "convert a ChargeHistorySuccessWrapper model to a ChargeHistoryDetails model" in {
      val result = ChargeHistorySuccessWrapper.toChargeHistoryModel(chargeHistorySuccessWrapperModel)
      result shouldBe chargeHistoryDetails
    }
  }
}

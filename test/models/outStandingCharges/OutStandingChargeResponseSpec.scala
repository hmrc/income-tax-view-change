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

package models.outStandingCharges

import constants.OutStandingChargesConstant._
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsSuccess, Json}
import utils.TestSupport

class OutStandingChargeResponseSpec extends TestSupport with Matchers {

  "OutStandingChargesResponse" should {
    "read from json" when {
      "there is a single charge" in {
        Json.fromJson[OutstandingChargesSuccessResponse](validSingleOutStandingChargeResponseJson) shouldBe JsSuccess(SingleOutStandingChargeResponseModel)
      }

      "there is a Multiple charges" in {
        Json.fromJson[OutstandingChargesSuccessResponse](validMultipleOutStandingChargeResponseJson) shouldBe JsSuccess(MultipleOutStandingChargeResponseModel)
      }
    }
    "write to json" when {
      "there is a single charge" in {
        Json.toJson(SingleOutStandingChargeResponseModel) shouldBe validSingleOutStandingChargeResponseJson
      }

      "there is a Multiple charges" in {
        Json.toJson(MultipleOutStandingChargeResponseModel) shouldBe validMultipleOutStandingChargeResponseJson
      }
    }
  }
}

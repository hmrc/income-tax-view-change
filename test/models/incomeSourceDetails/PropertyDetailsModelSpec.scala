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

package models.incomeSourceDetails

import constants.HipPropertyDetailsTestConstants._
import models.hip.incomeSourceDetails.PropertyDetailsModel._
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import utils.TestSupport

class PropertyDetailsModelSpec extends TestSupport with Matchers {

  "The PropertyDetailsModel" should {

    "read from Json with all fields" in {
      Json.fromJson(testPropertyDetailsJson) shouldBe JsSuccess(testPropertyDetailsModel)
    }

    "read from Json where Ints are Strings" in {
      Json.fromJson(testPropertyDetailsJsonString) shouldBe JsSuccess(testPropertyDetailsModel)
    }

    "read from Json with minimum fields" in {
      Json.fromJson(testMinimumPropertyDetailsJson) shouldBe JsSuccess(testMinimumPropertyDetailsModel)
    }

    "write to Json" in {
      Json.toJson(testPropertyDetailsModel) shouldBe testPropertyDetailsModelToJson
    }
  }
}


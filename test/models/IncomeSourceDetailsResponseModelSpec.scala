/*
 * Copyright 2018 HM Revenue & Customs
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

package models

import org.scalatest.Matchers
import play.api.libs.json._
import assets.TestConstants.BusinessDetails._
import assets.TestConstants._
import play.api.http.Status
import utils.TestSupport

class IncomeSourceDetailsResponseModelSpec extends TestSupport with Matchers {

  "The IncomeSourceDetailsResponseModel" should {
    "read from Json when all fields are returned" in {
      Json.fromJson[IncomeSourceDetailsModel](testIncomeSourceDetailsJson) shouldBe JsSuccess(testIncomeSourceDetailsModel)
    }
    "read from Json when minimum fields are returned" in {
      Json.fromJson[IncomeSourceDetailsModel](testMinimumIncomeSourceDetailsJson) shouldBe JsSuccess(testMinimumIncomeSourceDetailsModel)
    }
    "write to Json" in {
      Json.toJson(testIncomeSourceDetailsModel) shouldBe testIncomeSourceDetailsToJson
    }
  }

  "The DesResponseErrorModel" should {
    "have the correct status code in the model" in {
      testIncomeSourceDetailsError.status shouldBe Status.INTERNAL_SERVER_ERROR
    }
    "have the correct Error Message" in {
      testIncomeSourceDetailsError.reason shouldBe "Dummy error message"
    }
  }
}

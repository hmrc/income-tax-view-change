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

import assets.IncomeSourceDetailsTestConstants._
import org.scalatest.matchers.should.Matchers
import play.api.http.Status
import play.api.libs.json._
import utils.TestSupport

class IncomeSourceDetailsResponseModelSpec extends TestSupport with Matchers {

  "The IncomeSourceDetailsResponseModel" should {
    "read from IF Json when all fields are returned" in {
      Json.fromJson(testIfIncomeSourceDetailsJson)(IncomeSourceDetailsModel.ifReads) shouldBe JsSuccess(testIncomeSourceDetailsModel)
    }
    "read from IF Json when minimum fields are returned" in {
      Json.fromJson(testIfMinimumIncomeSourceDetailsJson)(IncomeSourceDetailsModel.ifReads) shouldBe JsSuccess(testMinimumIncomeSourceDetailsModel)
    }
    "write to Json" in {
      Json.toJson(testIncomeSourceDetailsModel) shouldBe testIncomeSourceDetailsToJson
    }
  }

  "The IncomeSourceDetailsErrorModel" should {
    "have the correct status code in the model" in {
      testIncomeSourceDetailsError.status shouldBe Status.INTERNAL_SERVER_ERROR
    }
    "have the correct Error Message" in {
      testIncomeSourceDetailsError.reason shouldBe "Dummy error message"
    }
  }
}

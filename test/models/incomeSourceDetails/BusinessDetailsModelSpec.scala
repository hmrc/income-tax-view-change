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

package models.incomeSourceDetails

import assets.BusinessDetailsTestConstants._
import org.scalatest.Matchers
import play.api.libs.json._
import utils.TestSupport

class BusinessDetailsModelSpec extends TestSupport with Matchers {

  "The BusinessDetailsaModel" should {

    "read from DES Json with all fields" in {
      Json.fromJson(testBusinessDetailsJson)(BusinessDetailsModel.desReads) shouldBe JsSuccess(testBusinessDetailsModel)
    }

    "read from DES Json with minimum fields" in {
      Json.fromJson(testMinimumBusinessDetailsJson)(BusinessDetailsModel.desReads) shouldBe JsSuccess(testMinimumBusinessDetailsModel)
    }

    "write to Json" in {
      Json.toJson(testBusinessDetailsModel) shouldBe testBusinessDetailsToJson
    }

  }
}
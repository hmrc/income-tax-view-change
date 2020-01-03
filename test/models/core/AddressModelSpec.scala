/*
 * Copyright 2020 HM Revenue & Customs
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

package models.core

import assets.AddressDetailsTestConstants._
import org.scalatest.Matchers
import play.api.libs.json._
import utils.TestSupport

class AddressModelSpec extends TestSupport with Matchers {

  "The AddressModel" should {

    "read from DES Json with all fields" in {
      Json.fromJson(testAddressJson)(AddressModel.desReads) shouldBe JsSuccess(testAddressModel)
    }

    "read from DES Json with minimum fields" in {
      Json.fromJson(testMinimumAddressJson)(AddressModel.desReads) shouldBe JsSuccess(testMinimumAddressModel)
    }

    "write to Json" in {
      Json.toJson(testAddressModel) shouldBe testAddressToJson
    }

  }

}

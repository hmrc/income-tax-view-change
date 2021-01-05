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

package models.financialDetails

import assets.FinancialDataTestConstants._
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, JsPath, JsSuccess, Json}

class SubItemSpec extends WordSpec with Matchers {

  val subItemEmpty: SubItem = SubItem(None, None, None, None, None, None, None, None, None)

  val subItemEmptyJson: JsObject = Json.obj(
  )

  "SubItem" should {
    "read from json" when {
      "the json is complete" in {
        Json.fromJson[SubItem](validSubItemJson) shouldBe JsSuccess(subItems1, JsPath \ "paymentLotItem")
      }
      "the json is empty" in {
        Json.fromJson[SubItem](Json.obj()) shouldBe JsSuccess(subItemEmpty)
      }
    }
    "write to json" when {
      "the model is complete" in {
        Json.toJson(subItems1) shouldBe validSubItemJsonAfterWrites
      }
      "the model is empty" in {
        Json.toJson(subItemEmpty) shouldBe subItemEmptyJson
      }
    }
  }
}

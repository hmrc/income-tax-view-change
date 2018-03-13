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

import assets.TestConstants.BusinessDetails._
import assets.TestConstants._
import org.scalatest.Matchers
import play.api.http.Status
import play.api.libs.json._
import utils.TestSupport

class PropertiesRentedModelSpec extends TestSupport with Matchers {

  "The PropertiesRentedModel" should {

    "read from Json with all fields" in {
      Json.fromJson[PropertiesRentedModel](testPropertiesRentedJson) shouldBe JsSuccess(testPropertiesRentedModel)
    }

    "read from Json with minimum fields" in {
      Json.fromJson[PropertiesRentedModel](Json.obj()) shouldBe JsSuccess(PropertiesRentedModel(None,None,None,None))
    }

    "write to Json" in {
      Json.toJson(testPropertiesRentedModel) shouldBe testPropertiesRentedToJson
    }


    "return Contact details containing only an email when PropertiesRentedModel.propertiesRented is given at least one value" in {
      PropertiesRentedModel.propertiesRented(Some(3),Some(2),Some(1),Some(4)) shouldBe Some(PropertiesRentedModel(Some(3),Some(2),Some(1),Some(4)))
      PropertiesRentedModel.propertiesRented(None,Some(2),Some(1),Some(4)) shouldBe Some(PropertiesRentedModel(None,Some(2),Some(1),Some(4)))
      PropertiesRentedModel.propertiesRented(Some(3),None,Some(1),Some(4)) shouldBe Some(PropertiesRentedModel(Some(3),None,Some(1),Some(4)))
      PropertiesRentedModel.propertiesRented(Some(3),Some(2),None,Some(4)) shouldBe Some(PropertiesRentedModel(Some(3),Some(2),None,Some(4)))
      PropertiesRentedModel.propertiesRented(Some(3),Some(2),Some(1),None) shouldBe Some(PropertiesRentedModel(Some(3),Some(2),Some(1),None))
    }

    "return None when PropertiesRentedModel.propertiesRented is given None" in {
      PropertiesRentedModel.propertiesRented(None,None,None,None) shouldBe None
    }
  }

}

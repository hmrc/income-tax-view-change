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

package models.core

import assets.ContactDetailsTestConstants._
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import utils.TestSupport

class ContactDetailsModelSpec extends TestSupport with Matchers {

  "The ContactDetailsModel" should {

    "read from DES json with all fields" in {
      Json.fromJson(testContactDetailsJson)(ContactDetailsModel.reads) shouldBe JsSuccess(testContactDetailsModel)
    }

    "read from DES Json with minimum fields" in {
      Json.fromJson(Json.obj())(ContactDetailsModel.reads) shouldBe JsSuccess(ContactDetailsModel(None, None, None, None))
    }

    "write to Json" in {
      Json.toJson(testContactDetailsModel) shouldBe testContactDetailsJson
    }


    "return Contact details containing only an email when ContactDetailsModel.propertyContactDetails is given an email" in {
      ContactDetailsModel.propertyContactDetails(Some("email@email.com")) shouldBe
        Some(ContactDetailsModel(None, None, None, Some("email@email.com")))
    }

    "return None when ContactDetailsModel.propertyContactDetails is given None" in {
      ContactDetailsModel.propertyContactDetails(None) shouldBe None
    }
  }

}

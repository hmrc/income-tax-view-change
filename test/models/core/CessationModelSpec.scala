/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.LocalDate

import assets.CessationTestConstants._
import org.scalatest.Matchers
import play.api.libs.json._
import utils.TestSupport

class CessationModelSpec extends TestSupport with Matchers {

  "The CessationModel" should {

    "read from DES Json with all fields" in {
      Json.fromJson(testCessationJson)(CessationModel.desReads) shouldBe JsSuccess(testCessationModel)
    }

    "read from DES Json with minimum fields" in {
      Json.fromJson(Json.obj())(CessationModel.desReads) shouldBe JsSuccess(CessationModel(None,None))
    }

    "write to Json" in {
      Json.toJson(testCessationModel) shouldBe testCessationToJson
    }

    "return Some Cessation Model when CessationModel.cessation is given either a date, reason or both" in {
      CessationModel.cessation(Some(LocalDate.parse("2017-06-01")),Some("Dummy reason")) shouldBe Some(testCessationModel)
      CessationModel.cessation(None,Some("")) shouldBe Some(CessationModel(None,Some("")))
      CessationModel.cessation(Some(LocalDate.parse("2017-06-01")),None) shouldBe Some(CessationModel(Some(LocalDate.parse("2017-06-01")),None))
    }

    "return None when CessationModel.cessation is given two Nones" in {
      CessationModel.cessation(None,None) shouldBe None
    }

  }

}


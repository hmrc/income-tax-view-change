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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*

import java.time.LocalDate

class CessationModelSpec extends AnyWordSpec with Matchers {

  "CessationModel format" should {

    "serialize to JSON correctly when both fields are defined" in {
      val model = CessationModel(Some(LocalDate.parse("2025-03-31")), Some("Business ceased"))

      val json = Json.toJson(model)

      val expectedJson = Json.obj(
        "date" -> "2025-03-31",
        "reason" -> "Business ceased"
      )

      json shouldBe expectedJson
    }

    "serialize to JSON correctly when fields are None" in {
      val model = CessationModel(None, None)

      val json = Json.toJson(model)

      val expectedJson = Json.obj()

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly when both fields are defined" in {
      val json = Json.obj(
        "date" -> "2025-03-31",
        "reason" -> "Business ceased"
      )

      val result = json.as[CessationModel]

      result shouldBe CessationModel(Some(LocalDate.parse("2025-03-31")), Some("Business ceased"))
    }

    "deserialize from JSON correctly when fields are null" in {
      val json = Json.obj(
        "date" -> JsNull,
        "reason" -> JsNull
      )

      val result = json.as[CessationModel]

      result shouldBe CessationModel(None, None)
    }

    "round-trip serialize/deserialize keeps the object unchanged" in {
      val original = CessationModel(Some(LocalDate.parse("2025-03-31")), Some("Business ceased"))

      val roundTrip = Json.toJson(original).as[CessationModel]

      roundTrip shouldBe original
    }
  }
}



/*
 * Copyright 2025 HM Revenue & Customs
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

package models.hip.createIncomeSource

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

class CreateBusinessDetailsRequestErrorSpec extends AnyWordSpec with Matchers {

  "CreateBusinessDetailsRequestError format" should {

    "serialize to JSON correctly" in {
      val model = CreateBusinessDetailsRequestError("Invalid business details")

      val json = Json.toJson(model)

      val expectedJson = Json.obj(
        "reason" -> "Invalid business details"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "reason" -> "Missing required field"
      )

      val result = json.as[CreateBusinessDetailsRequestError]

      result shouldBe CreateBusinessDetailsRequestError("Missing required field")
    }

    "round-trip serialize/deserialize keeps the object unchanged" in {
      val original = CreateBusinessDetailsRequestError("Duplicate entry detected")

      val roundTrip = Json.toJson(original).as[CreateBusinessDetailsRequestError]

      roundTrip shouldBe original
    }

    "fail to deserialize if 'reason' field is missing" in {
      val invalidJson = Json.obj()

      val result = invalidJson.validate[CreateBusinessDetailsRequestError]

      result.isError shouldBe true
    }
  }
}
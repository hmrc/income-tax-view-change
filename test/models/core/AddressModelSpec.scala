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

class AddressModelSpec extends AnyWordSpec with Matchers {

  "AddressModel format" should {

    "serialize to JSON correctly when all fields are defined" in {
      val model = AddressModel(
        addressLine1 = "10 High Street",
        addressLine2 = Some("Flat 2"),
        addressLine3 = Some("Westminster"),
        addressLine4 = Some("London"),
        postCode = Some("SW1A 1AA"),
        countryCode = "GB"
      )

      val json = Json.toJson(model)

      val expectedJson = Json.obj(
        "addressLine1" -> "10 High Street",
        "addressLine2" -> "Flat 2",
        "addressLine3" -> "Westminster",
        "addressLine4" -> "London",
        "postCode" -> "SW1A 1AA",
        "countryCode" -> "GB"
      )

      json shouldBe expectedJson
    }

    "serialize to JSON correctly when optional fields are None" in {
      val model = AddressModel(
        addressLine1 = "20 Baker Street",
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        postCode = None,
        countryCode = "GB"
      )

      val json = Json.toJson(model)

      (json \ "addressLine2").toOption shouldBe None
      (json \ "addressLine3").toOption shouldBe None
      (json \ "addressLine4").toOption shouldBe None
      (json \ "postCode").toOption shouldBe None

      (json \ "addressLine1").as[String] shouldBe "20 Baker Street"
      (json \ "countryCode").as[String] shouldBe "GB"
    }

    "deserialize from JSON correctly when all fields are defined" in {
      val json = Json.obj(
        "addressLine1" -> "10 High Street",
        "addressLine2" -> "Flat 2",
        "addressLine3" -> "Westminster",
        "addressLine4" -> "London",
        "postCode" -> "SW1A 1AA",
        "countryCode" -> "GB"
      )

      val result = json.as[AddressModel]

      result shouldBe AddressModel(
        addressLine1 = "10 High Street",
        addressLine2 = Some("Flat 2"),
        addressLine3 = Some("Westminster"),
        addressLine4 = Some("London"),
        postCode = Some("SW1A 1AA"),
        countryCode = "GB"
      )
    }

    "deserialize from JSON correctly when optional fields are missing" in {
      val json = Json.obj(
        "addressLine1" -> "99 Fleet Street",
        "countryCode" -> "GB"
      )

      val result = json.as[AddressModel]

      result shouldBe AddressModel(
        addressLine1 = "99 Fleet Street",
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        postCode = None,
        countryCode = "GB"
      )
    }

    "round-trip serialize/deserialize keeps the object unchanged" in {
      val original = AddressModel(
        addressLine1 = "221B Baker Street",
        addressLine2 = Some("Flat 1"),
        addressLine3 = None,
        addressLine4 = Some("London"),
        postCode = Some("NW1 6XE"),
        countryCode = "GB"
      )

      val roundTrip = Json.toJson(original).as[AddressModel]

      roundTrip shouldBe original
    }
  }
}


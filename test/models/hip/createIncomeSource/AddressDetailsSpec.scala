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

package models.hip.createIncomeSource

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

class AddressDetailsSpec extends AnyWordSpec with Matchers {

  "AddressDetails" should {

    "serialize to JSON correctly" in {
      val address = AddressDetails(
        addressLine1 = "123 High Street",
        addressLine2 = Some("Suite 4B"),
        addressLine3 = None,
        addressLine4 = None,
        countryCode = "GB",
        postcode = Some("AB12 3CD")
      )

      val json = Json.toJson(address)

      val expectedJson = Json.obj(
        "addressLine1" -> "123 High Street",
        "addressLine2" -> "Suite 4B",
        "countryCode" -> "GB",
        "postcode" -> "AB12 3CD"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "addressLine1" -> "456 Market Road",
        "addressLine2" -> "Building A",
        "addressLine3" -> JsNull,
        "addressLine4" -> "Townsville",
        "countryCode" -> "GB",
        "postcode" -> "TS1 1TT"
      )

      val result = json.as[AddressDetails]

      result shouldBe AddressDetails(
        addressLine1 = "456 Market Road",
        addressLine2 = Some("Building A"),
        addressLine3 = None,
        addressLine4 = Some("Townsville"),
        countryCode = "GB",
        postcode = Some("TS1 1TT")
      )
    }

    "handle optional fields correctly when None" in {
      val address = AddressDetails(
        addressLine1 = "1 Main Street",
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        countryCode = "US",
        postcode = None
      )

      val json = Json.toJson(address)

      (json \ "addressLine2").asOpt[String] shouldBe None
      (json \ "addressLine3").asOpt[String] shouldBe None
      (json \ "addressLine4").asOpt[String] shouldBe None
      (json \ "postcode").asOpt[String] shouldBe None

      val backAgain = json.as[AddressDetails]
      backAgain shouldBe address
    }

    "fail to deserialize if required fields are missing" in {
      val invalidJson = Json.obj(
        "countryCode" -> "GB",
        "postcode" -> "AB12 3CD"
      )

      val result = invalidJson.validate[AddressDetails]
      result.isError shouldBe true
    }
  }
}

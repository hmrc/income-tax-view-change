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

class BusinessDetailsSpec extends AnyWordSpec with Matchers {

  "BusinessDetails" should {

    "serialize to JSON correctly" in {
      val businessDetails = BusinessDetails(
        accountingPeriodStartDate = "2024-04-01",
        accountingPeriodEndDate = "2025-03-31",
        tradingName = "Tech Innovators Ltd",
        address = AddressDetails(
          addressLine1 = "123 High Street",
          addressLine2 = Some("Suite 4B"),
          addressLine3 = None,
          addressLine4 = Some("Techville"),
          countryCode = "GB",
          postcode = Some("AB12 3CD")
        ),
        typeOfBusiness = "Software Development",
        tradingStartDate = "2020-01-15",
        cashAccrualsFlag = Some("Y"),
        cessationDate = None,
        cessationReason = None
      )

      val json = Json.toJson(businessDetails)

      val expectedJson = Json.obj(
        "accountingPeriodStartDate" -> "2024-04-01",
        "accountingPeriodEndDate" -> "2025-03-31",
        "tradingName" -> "Tech Innovators Ltd",
        "address" -> Json.obj(
          "addressLine1" -> "123 High Street",
          "addressLine2" -> "Suite 4B",
          "addressLine4" -> "Techville",
          "countryCode" -> "GB",
          "postcode" -> "AB12 3CD"
        ),
        "typeOfBusiness" -> "Software Development",
        "tradingStartDate" -> "2020-01-15",
        "cashAccrualsFlag" -> "Y"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "accountingPeriodStartDate" -> "2024-04-01",
        "accountingPeriodEndDate" -> "2025-03-31",
        "tradingName" -> "Tech Innovators Ltd",
        "address" -> Json.obj(
          "addressLine1" -> "123 High Street",
          "addressLine2" -> "Suite 4B",
          "addressLine3" -> JsNull,
          "addressLine4" -> "Techville",
          "countryCode" -> "GB",
          "postcode" -> "AB12 3CD"
        ),
        "typeOfBusiness" -> "Software Development",
        "tradingStartDate" -> "2020-01-15",
        "cashAccrualsFlag" -> JsNull,
        "cessationDate" -> "2025-05-01",
        "cessationReason" -> "Business closed"
      )

      val result = json.as[BusinessDetails]

      result shouldBe BusinessDetails(
        accountingPeriodStartDate = "2024-04-01",
        accountingPeriodEndDate = "2025-03-31",
        tradingName = "Tech Innovators Ltd",
        address = AddressDetails(
          addressLine1 = "123 High Street",
          addressLine2 = Some("Suite 4B"),
          addressLine3 = None,
          addressLine4 = Some("Techville"),
          countryCode = "GB",
          postcode = Some("AB12 3CD")
        ),
        typeOfBusiness = "Software Development",
        tradingStartDate = "2020-01-15",
        cashAccrualsFlag = None,
        cessationDate = Some("2025-05-01"),
        cessationReason = Some("Business closed")
      )
    }

    "handle optional fields correctly when None" in {
      val businessDetails = BusinessDetails(
        accountingPeriodStartDate = "2024-04-01",
        accountingPeriodEndDate = "2025-03-31",
        tradingName = "Simple Biz",
        address = AddressDetails(
          addressLine1 = "1 Main St",
          addressLine2 = None,
          addressLine3 = None,
          addressLine4 = None,
          countryCode = "GB",
          postcode = None
        ),
        typeOfBusiness = "Retail",
        tradingStartDate = "2021-01-01",
        cashAccrualsFlag = None,
        cessationDate = None,
        cessationReason = None
      )

      val json = Json.toJson(businessDetails)

      (json \ "cashAccrualsFlag").asOpt[String] shouldBe None
      (json \ "cessationDate").asOpt[String] shouldBe None
      (json \ "cessationReason").asOpt[String] shouldBe None

      val roundTrip = json.as[BusinessDetails]
      roundTrip shouldBe businessDetails
    }

    "fail to deserialize if required fields are missing" in {
      val invalidJson = Json.obj(
        "tradingName" -> "Incomplete Ltd"
      )

      val result = invalidJson.validate[BusinessDetails]
      result.isError shouldBe true
    }
  }
}
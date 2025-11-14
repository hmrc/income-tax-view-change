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

package models.financialDetails

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import java.time.LocalDate

class PaymentSpec extends AnyWordSpec with Matchers {

  "Payment" should {

    "serialize to JSON correctly" in {
      val payment = Payment(
        reference = Some("REF12345"),
        amount = BigDecimal(500.50),
        outstandingAmount = BigDecimal(200.25),
        documentDescription = Some("Payment on account"),
        method = Some("Bank Transfer"),
        lot = Some("LOT123"),
        lotItem = Some("ITEM1"),
        dueDate = Some(LocalDate.parse("2025-03-31")),
        documentDate = LocalDate.parse("2025-02-01"),
        transactionId = "TXN123",
        mainType = Some("SA Payment"),
        mainTransaction = Some("Payment on Account")
      )

      val json = Json.toJson(payment)

      val expectedJson = Json.obj(
        "reference" -> "REF12345",
        "amount" -> 500.50,
        "outstandingAmount" -> 200.25,
        "documentDescription" -> "Payment on account",
        "method" -> "Bank Transfer",
        "lot" -> "LOT123",
        "lotItem" -> "ITEM1",
        "dueDate" -> "2025-03-31",
        "documentDate" -> "2025-02-01",
        "transactionId" -> "TXN123",
        "mainType" -> "SA Payment",
        "mainTransaction" -> "Payment on Account"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "reference" -> "REF12345",
        "amount" -> 500.50,
        "outstandingAmount" -> 200.25,
        "documentDescription" -> "Payment on account",
        "method" -> "Bank Transfer",
        "lot" -> "LOT123",
        "lotItem" -> "ITEM1",
        "dueDate" -> "2025-03-31",
        "documentDate" -> "2025-02-01",
        "transactionId" -> "TXN123",
        "mainType" -> "SA Payment",
        "mainTransaction" -> "Payment on Account"
      )

      val result = json.as[Payment]

      result shouldBe Payment(
        reference = Some("REF12345"),
        amount = BigDecimal(500.50),
        outstandingAmount = BigDecimal(200.25),
        documentDescription = Some("Payment on account"),
        method = Some("Bank Transfer"),
        lot = Some("LOT123"),
        lotItem = Some("ITEM1"),
        dueDate = Some(LocalDate.parse("2025-03-31")),
        documentDate = LocalDate.parse("2025-02-01"),
        transactionId = "TXN123",
        mainType = Some("SA Payment"),
        mainTransaction = Some("Payment on Account")
      )
    }

    "handle optional fields correctly when None" in {
      val payment = Payment(
        reference = None,
        amount = BigDecimal(100.00),
        outstandingAmount = BigDecimal(0.00),
        documentDescription = None,
        method = None,
        lot = None,
        lotItem = None,
        dueDate = None,
        documentDate = LocalDate.parse("2025-02-01"),
        transactionId = "TXN999",
        mainType = None,
        mainTransaction = None
      )

      val json = Json.toJson(payment)

      (json \ "reference").asOpt[String] shouldBe None
      (json \ "dueDate").asOpt[String] shouldBe None
      (json \ "method").asOpt[String] shouldBe None

      val roundTrip = json.as[Payment]
      roundTrip shouldBe payment
    }

    "fail to deserialize if required fields are missing" in {
      val invalidJson = Json.obj(
        "amount" -> 123.45
      )

      val result = invalidJson.validate[Payment]
      result.isError shouldBe true
    }
  }
}



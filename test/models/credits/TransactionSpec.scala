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

package models.credits

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import java.time.LocalDate

class TransactionSpec extends AnyWordSpec with Matchers {

  "Transaction" should {

    "serialize to JSON correctly" in {
      val transaction = Transaction(
        transactionType = MfaCreditType,
        amount = BigDecimal(250.75),
        taxYear = Some(2025),
        dueDate = Some(LocalDate.parse("2025-03-31")),
        effectiveDateOfPayment = Some(LocalDate.parse("2025-03-31")),
        transactionId = "TXN12345"
      )

      val json = Json.toJson(transaction)

      val expectedJson = Json.obj(
        "transactionType" -> "mfa",
        "amount" -> 250.75,
        "taxYear" -> 2025,
        "dueDate" -> "2025-03-31",
        "effectiveDateOfPayment" -> "2025-03-31",
        "transactionId" -> "TXN12345"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "transactionType" -> "mfa",
        "amount" -> 100.50,
        "taxYear" -> JsNull,
        "dueDate" -> "2025-04-15",
        "effectiveDateOfPayment" -> "2025-04-15",
        "transactionId" -> "TXN67890"
      )

      val result = json.as[Transaction]

      result shouldBe Transaction(
        transactionType = MfaCreditType,
        amount = BigDecimal(100.50),
        taxYear = None,
        dueDate = Some(LocalDate.parse("2025-04-15")),
        effectiveDateOfPayment = Some(LocalDate.parse("2025-04-15")),
        transactionId = "TXN67890"
      )
    }

    "handle optional fields correctly when None" in {
      val transaction = Transaction(
        transactionType = MfaCreditType,
        amount = BigDecimal(500),
        taxYear = None,
        dueDate = None,
        effectiveDateOfPayment = None,
        transactionId = "TXN999"
      )

      val json = Json.toJson(transaction)

      (json \ "taxYear").asOpt[Int] shouldBe None
      (json \ "dueDate").asOpt[Int] shouldBe None
      (json \ "effectiveDateOfPayment").asOpt[String] shouldBe None

      val roundTrip = json.as[Transaction]
      roundTrip shouldBe transaction
    }

    "fail to deserialize if required fields are missing" in {
      val invalidJson = Json.obj(
        "amount" -> 100.0,
        "transactionId" -> "TXN111"
      )

      val result = invalidJson.validate[Transaction]
      result.isError shouldBe true
    }
  }
}
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

import models.hip.core.AccountingPeriodModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*

import java.time.LocalDate

class AccountingPeriodModelSpec extends AnyWordSpec with Matchers {

  "AccountingPeriodModel format" should {

    "serialize to JSON correctly" in {
      val model = AccountingPeriodModel(
        start = LocalDate.parse("2024-04-01"),
        end = LocalDate.parse("2025-03-31")
      )

      val json = Json.toJson(model)

      val expectedJson = Json.obj(
        "start" -> "2024-04-01",
        "end" -> "2025-03-31"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "start" -> "2024-04-01",
        "end" -> "2025-03-31"
      )

      val result = json.as[AccountingPeriodModel]

      result shouldBe AccountingPeriodModel(
        start = LocalDate.parse("2024-04-01"),
        end = LocalDate.parse("2025-03-31")
      )
    }

    "round-trip serialize/deserialize keeps the object unchanged" in {
      val original = AccountingPeriodModel(
        start = LocalDate.parse("2023-01-01"),
        end = LocalDate.parse("2023-12-31")
      )

      val roundTrip = Json.toJson(original).as[AccountingPeriodModel]

      roundTrip shouldBe original
    }
  }
}

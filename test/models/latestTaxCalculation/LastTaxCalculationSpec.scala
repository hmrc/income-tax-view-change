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

import models.latestTaxCalculation.{LastTaxCalculation, LastTaxCalculationError, LastTaxCalculationResponseModel}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.*

class LastTaxCalculationResponseModelSpec extends AnyWordSpec with Matchers {

  "LastTaxCalculation" should {

    "serialize to JSON correctly" in {
      val model = LastTaxCalculation(
        calcID = "12345",
        calcTimestamp = "2025-01-01T00:00:00Z",
        calcAmount = BigDecimal(123.45),
        calcStatus = Some("Success")
      )

      val json = Json.toJson(model)
      val expectedJson = Json.obj(
        "calcID" -> "12345",
        "calcTimestamp" -> "2025-01-01T00:00:00Z",
        "calcAmount" -> 123.45,
        "calcStatus" -> "Success"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "calcID" -> "67890",
        "calcTimestamp" -> "2025-10-10T10:10:10Z",
        "calcAmount" -> 999.99,
        "calcStatus" -> JsNull
      )

      val result = json.as[LastTaxCalculation]
      result shouldBe LastTaxCalculation("67890", "2025-10-10T10:10:10Z", 999.99, None)
    }
  }

  "LastTaxCalculationError" should {

    "serialize to JSON correctly" in {
      val error = LastTaxCalculationError(status = 404, message = "Not Found")

      val json = Json.toJson(error)
      val expectedJson = Json.obj(
        "status" -> 404,
        "message" -> "Not Found"
      )

      json shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "status" -> 500,
        "message" -> "Internal Server Error"
      )

      val result = json.as[LastTaxCalculationError]
      result shouldBe LastTaxCalculationError(500, "Internal Server Error")
    }

    "fail to deserialize if required fields are missing" in {
      val invalidJson = Json.obj("status" -> 400)
      val result = invalidJson.validate[LastTaxCalculationError]

      result.isError shouldBe true
    }
  }

  "LastTaxCalculationResponseModel" should {

    "allow pattern matching on the trait" in {
      val models: Seq[LastTaxCalculationResponseModel] = Seq(
        LastTaxCalculation("1", "2025-01-01", BigDecimal(10.0)),
        LastTaxCalculationError(400, "Bad Request")
      )

      val results = models.map {
        case LastTaxCalculation(_, _, amount, _) => s"Calc: £$amount"
        case LastTaxCalculationError(status, _)  => s"Error $status"
      }

      results should contain allOf ("Calc: £10.0", "Error 400")
    }
  }
}

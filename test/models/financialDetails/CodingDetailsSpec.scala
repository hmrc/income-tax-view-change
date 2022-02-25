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

package models.financialDetails

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class CodingDetailsSpec extends WordSpec with Matchers {

  val codingDetailsMin: CodingDetails = CodingDetails(
    taxYearReturn = "2018",
    totalReturnAmount = None,
    amountNotCoded = None,
    amountNotCodedDueDate = None,
    amountCodedOut = 300.00,
    taxYearCoding = "2019")

  val codingDetailMinJson: JsObject = Json.obj(
    "taxYearReturn" -> "2018",
    "amountCodedOut" -> 300.00,
    "taxYearCoding" -> "2019"
  )

  val codingDetailsFull: CodingDetails = CodingDetails(
    taxYearReturn = "2018",
    totalReturnAmount = Some(100.00),
    amountNotCoded = Some(200.00),
    amountNotCodedDueDate = Some("2018-01-01"),
    amountCodedOut = 300.00,
    taxYearCoding = "2019")

  val codingDetailFullJson: JsObject = Json.obj(
    "taxYearReturn" -> "2018",
    "totalReturnAmount" -> 100.00,
    "amountNotCoded" -> 200.00,
    "amountNotCodedDueDate" -> "2018-01-01",
    "amountCodedOut" -> 300.00,
    "taxYearCoding" -> "2019"
  )

  "CodingDetails" should {
    "write to Json" when {
      "the model has all details" in {
        Json.toJson(codingDetailsFull) shouldBe codingDetailFullJson
      }
      "the model has the minimal details" in {
        Json.toJson(codingDetailsMin) shouldBe codingDetailMinJson
      }
    }
  }
}

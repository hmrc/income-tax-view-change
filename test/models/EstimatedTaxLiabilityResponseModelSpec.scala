/*
 * Copyright 2017 HM Revenue & Customs
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

package models

import org.scalatest.Matchers
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class EstimatedTaxLiabilityResponseModelSpec extends UnitSpec with Matchers{

  "EstimatedTaxLiability model" should {

    val total = 10
    val nic4 = 2
    val nic2 = 0
    val incomeTax = 8

    val model = EstimatedTaxLiability(total, nic2, nic4, incomeTax)

    "have the correct Income Tax value assigned in the model" in {
      model.incomeTax shouldBe 8
    }

    "have the correct C2 NIC value assigned in the model" in {
      model.nic2 shouldBe 0
    }

    "have the correct C4 NIC value assigned in the model" in {
      model.nic4 shouldBe 2
    }

    "have the correct Year to Date value assigned in the model" in {
      model.total shouldBe 10
    }

    "be translated to Json correctly" in {
      Json.toJson(model) should be
        s"""
          |{
          |  "total":"$total",
          |  "nic2" : "$nic2",
          |  "nic4" : "$nic4",
          |  "incomeTax" : "$incomeTax"
          |}
          |""".stripMargin.trim
    }
  }

  "EstimatedTaxLiabilityError model" should {

    val errorStatus = Status.INTERNAL_SERVER_ERROR
    val errorMessage = "Error Text"
    val model = EstimatedTaxLiabilityError(errorStatus, errorMessage)

    "have the correct Error Status Code value assigned in the model" in {
      model.status shouldBe errorStatus
    }

    "have the correct Error Message value assigned in the model" in {
      model.message shouldBe errorMessage
    }

    "be translated to Json correctly" in {
      Json.toJson(model) should be
      s"""
        |{
        |  "status" : "$errorStatus",
        |  "message" : "$errorMessage"
        |}
        |""".stripMargin.trim
    }
  }


}

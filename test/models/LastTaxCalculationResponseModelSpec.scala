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
import utils.TestSupport

class LastTaxCalculationResponseModelSpec extends TestSupport with Matchers {

  "LastTaxCalculationResponseMode model" when {

    val calcId = "01234567"
    val calcTimestamp = "2017-07-06T12:34:56.789Z"
    val calcAmount = 2345.67
    val errorStatus = 500
    val errorMessage = "Error Message"

    val successModel = LastTaxCalculation(calcId, calcTimestamp, calcAmount)
    val errorModel = LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")

    "successful" should {
      "have the correct calcId assigned in the model" in {
        successModel.calcID shouldBe "01234567"
      }

      "have the correct timestamp assigned in the model" in {
        successModel.calcTimestamp shouldBe "2017-07-06T12:34:56.789Z"
      }

      "have the correct calculation amount assigned in the model" in {
        successModel.calcAmount shouldBe 2345.67
      }

      "be translated to Json correctly" in {
        Json.toJson(successModel) should be
        s"""
           |{
           |  "calcID":"$calcId",
           |  "calcTimestamp" : "$calcTimestamp",
           |  "calcAmouunt" : "$calcAmount"
           |}
           |""".stripMargin.trim
      }
    }
    "not successful" should {
      "have the correct Status (500)" in {
        errorModel.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
      "have the correct message" in {
        errorModel.message shouldBe "Error Message"
      }
      "be translated into Json correctly" in {
        Json.toJson(errorModel) should be
          s"""
             |{
             |  "error": $errorStatus,
             |  "message": "$errorMessage"
             |}
           """.stripMargin.trim
      }
    }
  }

}

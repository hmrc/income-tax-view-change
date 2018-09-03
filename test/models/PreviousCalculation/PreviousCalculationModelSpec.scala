/*
 * Copyright 2018 HM Revenue & Customs
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

package models.PreviousCalculation

import org.scalatest.Matchers
import play.api.libs.json.Json
import utils.TestSupport

class PreviousCalculationModelSpec extends TestSupport with Matchers {

  "PreviousCalculationModel" when {

    val calcId = "01234567"
    val crystallised = true
    val calcTimestamp = "2017-07-06T12:34:56.789Z"
    val calcAmount = 22.56
    val incomeTaxNicYtd = 500.68
    val incomeTaxNicAmount = 125.63
    val errorStatus = 500
    val errorMessage = "Error Message"

    val successModel: PreviousCalculationModel =
      PreviousCalculationModel(
        CalcOutput(calcID = calcId, calcAmount = Some(calcAmount), calcTimestamp = Some(calcTimestamp), crystallised = Some(crystallised),
          calcResult = Some(CalcResult(incomeTaxNicYtd = incomeTaxNicYtd, Some(EoyEstimate(incomeTaxNicAmount = incomeTaxNicAmount))))))

    val singleError = Error(code = "CODE", reason = "ERROR MESSAGE")
    val multiError = MultiError(
      failures = Seq(
        Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
        Error(code = "ERROR CODE 2" +
          "", reason = "ERROR MESSAGE 2")
      )
    )

    "successful" should {
      "have the correct calcId assigned in the model" in {
        successModel.calcOutput.calcID shouldBe "01234567"
      }

      "have the correct timestamp assigned in the model" in {
        successModel.calcOutput.calcTimestamp.getOrElse("") shouldBe "2017-07-06T12:34:56.789Z"
      }

      "have the correct calculation amount assigned in the model" in {
        successModel.calcOutput.calcAmount.getOrElse(0) shouldBe 22.56
      }

      "have the correct crystallised value assigned in the model" in {
        successModel.calcOutput.crystallised.getOrElse(false) shouldBe true
      }

      val calcResult = successModel.calcOutput.calcResult.get

      "have the correct incomeTaxNicAmount value assigned in the model" in {
        calcResult.incomeTaxNicYtd shouldBe 500.68
      }

      "have the correct incomeTaxNicYtd value assigned in the model" in {
        calcResult.eoyEstimate.get.incomeTaxNicAmount shouldBe 125.63
      }

      "be translated to Json correctly" in {
        Json.toJson(successModel) should be
        """
            |{"calcOutput":{
            |"calcID":"1",
            |"calcAmount":22.56,
            |"calcTimestamp":"2008-05-01",
            |"crystallised":true,
            |"calcResult":{"incomeTaxNicYtd":500.68,"eoyEstimate":{"incomeTaxNicAmount":125.63}}}}
          """.stripMargin.trim
      }
    }

  }

}

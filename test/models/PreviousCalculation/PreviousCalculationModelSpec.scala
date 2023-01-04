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

package models.PreviousCalculation

import org.scalatest.Matchers
import play.api.libs.json.{JsSuccess, Json}
import utils.TestSupport

class PreviousCalculationModelSpec extends TestSupport with Matchers {

  "PreviousCalculationModel" when {

    val calcId = "01234567"
    val crystallised = true
    val calcTimestamp = "2017-07-06T12:34:56.789Z"
    val calcAmount = 22.56
    val incomeTaxNicYtd = 500.68
    val incomeTaxNicAmount = 125.63
    val eoyEstimate = Some(EoyEstimate(incomeTaxNicAmount = incomeTaxNicAmount))

    val annualAllowancesWithValues = Some(AnnualAllowancesModel(Some(101.98), Some(12.89)))
    val annualAllowancesWithoutValues = Some(AnnualAllowancesModel(None, None))


    def successModel(incomeTaxNicYtd: BigDecimal, eoyEstimate: Option[EoyEstimate], annualAllowances: Option[AnnualAllowancesModel]): PreviousCalculationModel =
      PreviousCalculationModel(
        CalcOutput(calcID = calcId, calcAmount = Some(calcAmount), calcTimestamp = Some(calcTimestamp), crystallised = Some(crystallised),
          calcResult = Some(CalcResult(incomeTaxNicYtd, eoyEstimate, annualAllowances = annualAllowances))))

    "successful" should {
      "have the correct calcId assigned in the model" in {
        successModel(incomeTaxNicYtd, eoyEstimate, None).calcOutput.calcID shouldBe "01234567"
      }

      "have the correct timestamp assigned in the model" in {
        successModel(incomeTaxNicYtd, eoyEstimate, None).calcOutput.calcTimestamp.getOrElse("") shouldBe "2017-07-06T12:34:56.789Z"
      }

      "have the correct calculation amount assigned in the model" in {
        successModel(incomeTaxNicYtd, eoyEstimate, None).calcOutput.calcAmount.getOrElse(0) shouldBe 22.56
      }

      "have the correct crystallised value assigned in the model" in {
        successModel(incomeTaxNicYtd, eoyEstimate, None).calcOutput.crystallised.getOrElse(false) shouldBe true
      }

      val calcResult = successModel(incomeTaxNicYtd, eoyEstimate, None).calcOutput.calcResult.get

      "have the correct incomeTaxNicAmount value assigned in the model" in {
        calcResult.incomeTaxNicYtd shouldBe 500.68
      }

      "have the correct incomeTaxNicYtd value assigned in the model" in {
        calcResult.eoyEstimate.get.incomeTaxNicAmount shouldBe 125.63
      }

      "be translated to Json correctly" in {
        Json.toJson(successModel(incomeTaxNicYtd, eoyEstimate, None)) should be
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

    "processing the personal annual allowance object" should {

      "have the correct personal allowance and gift aid extender with a value" in {
        val calcResult = successModel(incomeTaxNicAmount, eoyEstimate, annualAllowancesWithValues).calcOutput.calcResult.get

        calcResult.annualAllowances.get.personalAllowance shouldBe annualAllowancesWithValues.get.personalAllowance
        calcResult.annualAllowances.get.giftAidExtender shouldBe annualAllowancesWithValues.get.giftAidExtender
      }

      "have the correct personal allowance and gift aid extender without a value" in {
        val calcResult = successModel(incomeTaxNicAmount, eoyEstimate, annualAllowancesWithoutValues).calcOutput.calcResult.get

        calcResult.annualAllowances.get.personalAllowance shouldBe None
        calcResult.annualAllowances.get.giftAidExtender shouldBe None
      }

      "be translated to Json correctly with the values" in {
        Json.toJson(successModel(incomeTaxNicYtd, eoyEstimate, None)) should be
        """
          |{"calcOutput":{
          |"calcID":"1",
          |"calcAmount":22.56,
          |"calcTimestamp":"2008-05-01",
          |"crystallised":true,
          |"calcResult":{
          |   "incomeTaxNicYtd":500.68,
          |   "eoyEstimate":{"incomeTaxNicAmount":125.63},
          |   "annualAllowances":{"personalAllowance" :101.98 ,"giftAidExtender" :12.89}
          |}}}
        """.stripMargin.trim
      }


      "be translated to Json correctly without the values" in {
        Json.toJson(successModel(incomeTaxNicYtd, eoyEstimate, annualAllowancesWithoutValues)) should be
        """
          |{"calcOutput":{
          |"calcID":"1",
          |"calcAmount":22.56,
          |"calcTimestamp":"2008-05-01",
          |"crystallised":true,
          |"calcResult":{"incomeTaxNicYtd":500.68,"eoyEstimate":{"incomeTaxNicAmount":125.63}, "annualAllowances":{}}}}
        """.stripMargin.trim
      }
    }

  }

  "NicModel" should {

    val nicJsonFull = Json.obj(
      "class2" -> Json.obj("amount" -> 100.00),
      "class4" -> Json.obj("totalAmount" -> 200.00)
    )

    val nicModelFull = NicModel(Some(100.00), Some(200.00))

    val nicJsonNoAmounts = Json.obj(
      "class2" -> Json.obj(),
      "class4" -> Json.obj()
    )

    val nicJsonEmpty = Json.obj()

    val nicModelEmpty = NicModel(None, None)

    "read from json successfully" when {
      "the json is full" in {
        Json.fromJson[NicModel](nicJsonFull) shouldBe JsSuccess(nicModelFull)
      }
      "the json is empty" in {
        Json.fromJson[NicModel](nicJsonEmpty) shouldBe JsSuccess(nicModelEmpty)
      }
      "the class2 and class4 objects are there but are empty" in {
        Json.fromJson[NicModel](nicJsonNoAmounts) shouldBe JsSuccess(nicModelEmpty)
      }
    }

    "write to json successfully with a full model" in {
      Json.toJson(nicModelFull) shouldBe nicJsonFull
    }
    "write to json successfully with an empty model" in {
      Json.toJson(nicModelEmpty) shouldBe nicJsonEmpty
    }
  }

}

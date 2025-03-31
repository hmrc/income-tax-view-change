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

package models.incomeSourceDetails

import constants.AccountingPeriodTestConstants.testAccountingPeriodModel
import constants.BusinessDetailsTestConstants._
import models.incomeSourceDetails.BusinessDetailsModel._
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import utils.TestSupport

class BusinessDetailsModelSpec extends TestSupport with Matchers {

  "The BusinessDetailsModel" should {

    "read from Json with all fields" in {
      Json.fromJson(testBusinessDetailsJson) shouldBe JsSuccess(testBusinessDetailsModel)
    }

    "read from Json with minimum fields" in {
      Json.fromJson(testMinimumBusinessDetailsJson) shouldBe JsSuccess(testMinimumBusinessDetailsModel)
    }

    "write to Json" in {
      Json.toJson(testBusinessDetailsModel) shouldBe testBusinessDetailsToJson
    }

    "write cashOrAccruals `cash` (pre-R10 value) to Some(false) (post-R10 value)" in {

      val businessDetailsJsonBeforeR10: JsObject =
        Json.obj(
          "incomeSourceId" -> "111111111111111",
          "incomeSource" -> "Fruit Ltd",
          "accountingPeriodStartDate" -> "2017-06-01",
          "accountingPeriodEndDate" -> "2018-05-31",
          "cashOrAccruals" -> false
        )

      val businessDetailsModelBeforeR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        incomeSource = Some("Fruit Ltd"),
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        contextualTaxYear = None,
        tradingStartDate = None,
        cashOrAccruals = false,
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None,
        quarterTypeElection = None
      )

      Json.fromJson(businessDetailsJsonBeforeR10) shouldBe JsSuccess(businessDetailsModelBeforeR10)
    }

    "write cashOrAccruals `accruals` (pre-R10 value) to Some(true) (post-R10 value)" in {

      val businessDetailsJsonBeforeR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "incomeSource" -> "Fruit Ltd",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> true)

      val businessDetailsModelBeforeR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        incomeSource = Some("Fruit Ltd"),
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        contextualTaxYear = None,
        tradingStartDate = None,
        cashOrAccruals = true,
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None,
        quarterTypeElection = None
      )

      Json.fromJson(businessDetailsJsonBeforeR10) shouldBe JsSuccess(businessDetailsModelBeforeR10)
    }

    "write cashOrAccruals true (post-R10 value) to Some(true) (post-R10 value)" in {
      val businessDetailsJsonAfterR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "incomeSource" -> "Fruit Ltd",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> true)

      val businessDetailsModelAfterR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        incomeSource = Some("Fruit Ltd"),
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        contextualTaxYear = None,
        tradingStartDate = None,
        cashOrAccruals = true,
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None,
        quarterTypeElection = None
      )

      Json.fromJson(businessDetailsJsonAfterR10) shouldBe JsSuccess(businessDetailsModelAfterR10)
    }

    "write cashOrAccruals false (post-R10 value) to Some(false) (post-R10 value)" in {
      val businessDetailsJsonAfterR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "incomeSource" -> "Fruit Ltd",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> false)

      val businessDetailsModelAfterR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        incomeSource = Some("Fruit Ltd"),
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        contextualTaxYear = None,
        tradingStartDate = None,
        cashOrAccruals = false,
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None,
        quarterTypeElection = None
      )

      Json.fromJson(businessDetailsJsonAfterR10) shouldBe JsSuccess(businessDetailsModelAfterR10)
    }

  }
}

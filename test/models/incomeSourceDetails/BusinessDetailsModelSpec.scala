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

import assets.AccountingPeriodTestConstants.testAccountingPeriodModel
import assets.BusinessDetailsTestConstants._
import org.scalatest.Matchers
import play.api.libs.json._
import utils.TestSupport

class BusinessDetailsModelSpec extends TestSupport with Matchers {

  "The BusinessDetailsModel" should {

    "read from DES Json with all fields" in {
      Json.fromJson(testBusinessDetailsJson)(BusinessDetailsModel.desReads) shouldBe JsSuccess(testBusinessDetailsModel)
    }

    "read from DES Json with minimum fields" in {
      Json.fromJson(testMinimumBusinessDetailsJson)(BusinessDetailsModel.desReads) shouldBe JsSuccess(testMinimumBusinessDetailsModel)
    }

    "write to Json" in {
      Json.toJson(testBusinessDetailsModel) shouldBe testBusinessDetailsToJson
    }

    "write cashOrAccruals `cash` (pre-R10 value) to Some(false) (post-R10 value)" in {
      val businessDetailsJsonBeforeR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> "cash")

      val businessDetailsModelBeforeR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        tradingStartDate = None,
        cashOrAccruals = Some(false),
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None
      )

      Json.fromJson(businessDetailsJsonBeforeR10)(BusinessDetailsModel.desReads) shouldBe JsSuccess(businessDetailsModelBeforeR10)
    }

    "write cashOrAccruals `accruals` (pre-R10 value) to Some(true) (post-R10 value)" in {
      val businessDetailsJsonBeforeR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> "accruals")

      val businessDetailsModelBeforeR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        tradingStartDate = None,
        cashOrAccruals = Some(true),
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None
      )

      Json.fromJson(businessDetailsJsonBeforeR10)(BusinessDetailsModel.desReads) shouldBe JsSuccess(businessDetailsModelBeforeR10)
    }

    "write cashOrAccruals true (post-R10 value) to Some(true) (post-R10 value)" in {
      val businessDetailsJsonAfterR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> true)

      val businessDetailsModelAfterR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        tradingStartDate = None,
        cashOrAccruals = Some(true),
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None
      )

      Json.fromJson(businessDetailsJsonAfterR10)(BusinessDetailsModel.desReads) shouldBe JsSuccess(businessDetailsModelAfterR10)
    }

    "write cashOrAccruals false (post-R10 value) to Some(false) (post-R10 value)" in {
      val businessDetailsJsonAfterR10: JsObject = Json.obj(
        "incomeSourceId" -> "111111111111111",
        "accountingPeriodStartDate" -> "2017-06-01",
        "accountingPeriodEndDate" -> "2018-05-31",
        "cashOrAccruals" -> false)

      val businessDetailsModelAfterR10 = BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        accountingPeriod = testAccountingPeriodModel,
        tradingName = None,
        address = None,
        contactDetails = None,
        tradingStartDate = None,
        cashOrAccruals = Some(false),
        seasonal = None,
        cessation = None,
        paperless = None,
        firstAccountingPeriodEndDate = None,
        latencyDetails = None
      )

      Json.fromJson(businessDetailsJsonAfterR10)(BusinessDetailsModel.desReads) shouldBe JsSuccess(businessDetailsModelAfterR10)
    }

  }
}
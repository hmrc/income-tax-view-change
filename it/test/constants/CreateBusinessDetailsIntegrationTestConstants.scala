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

package constants

import models.createIncomeSource._
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsModel, IncomeSource}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object CreateBusinessDetailsIntegrationTestConstants {

  val testIncomeSourceId = "AAIS12345678901"

  val successResponse: JsValue = {
    Json.obj(
      "incomeSourceId" -> testIncomeSourceId
    )
  }

  val jsonSuccessOutput: JsValue = {
    Json.parse(
      s"""
         |[{
         |	"incomeSourceId":"$testIncomeSourceId"
         |}]
         |""".stripMargin)
  }

  val testDate: String = LocalDate.of(2022, 5, 1).toString

  def createBusinessIncomeSourceRequest
    (cashOrAccFlag: Option[String]): CreateBusinessIncomeSourceRequest =
      CreateBusinessIncomeSourceRequest(
        List(
          BusinessDetails(
            accountingPeriodStartDate = testDate,
            accountingPeriodEndDate = testDate,
            tradingName = "Big Business",
            addressDetails = AddressDetails("10 FooBar Street", None, None, None, None, None),
            typeOfBusiness = None,
            tradingStartDate = testDate,
            cashOrAccrualsFlag = cashOrAccFlag,
            cessationDate = None,
            cessationReason = None
          )
        )
      )

  def testCreateSelfEmploymentIncomeSourceRequest(cashOrAccFlag: Option[String] = Some("CASH")): JsValue =
    Json.toJson(createBusinessIncomeSourceRequest(cashOrAccFlag))

  val testCreateUKPropertyRequest: JsValue =
    Json.toJson(
      CreateUKPropertyIncomeSourceRequest(
        PropertyDetails(
          tradingStartDate = testDate,
          cashOrAccrualsFlag = Some("CASH"),
          startDate = testDate
        )
      )
    )

  val testCreateForeignPropertyRequest: JsValue =
    Json.toJson(
      CreateForeignPropertyIncomeSourceRequest(
        PropertyDetails(
          tradingStartDate = testDate,
          cashOrAccrualsFlag = Some("ACCRUALS"),
          startDate = testDate
        )
      )
    )

  val testCreateForeignPropertyRequestNoFlag: JsValue =
    Json.toJson(
      CreateForeignPropertyIncomeSourceRequest(
        PropertyDetails(
          tradingStartDate = testDate,
          cashOrAccrualsFlag = None,
          startDate = testDate
        )
      )
    )

  val testCreateBusinessDetailsSuccessResponse: JsValue = {
    Json.toJson(
      CreateBusinessDetailsModel(
       List(
         IncomeSource(incomeSourceId = "AAIS12345678901")
       )
      )
    )
  }
}
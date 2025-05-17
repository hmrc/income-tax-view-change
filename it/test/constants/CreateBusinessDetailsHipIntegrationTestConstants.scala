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

import models.createIncomeSource.{AddressDetails, BusinessDetails, CreateBusinessIncomeSourceRequest, CreateForeignPropertyIncomeSourceRequest, CreateUKPropertyIncomeSourceRequest, PropertyDetails}
import models.hip.createIncomeSource.{CreateBusinessIncomeSourceHipRequest, CreateForeignPropertyIncomeSourceHipRequest, CreateUKPropertyIncomeSourceHipRequest}
import models.hip.incomeSourceDetails.{CreateBusinessDetailsHipModel, IncomeSource, IncomeSourceIdDetails}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object CreateBusinessDetailsHipIntegrationTestConstants {

  val testMtdbsa = "XIAT00000000000"
  val testIncomeSourceId = "AAIS12345678901"

  val successResponse: JsValue = {
    Json.obj(
      "incomeSourceId" -> testIncomeSourceId
    )
  }

  val jsonSuccessOutput: JsValue = {
    Json.parse(
      s"""
         |{
         |  "success": {
         |      "processingDate": "2023-05-19T19:34:22Z",
         |      "incomeSourceIdDetails": [
         |        {
         |	        "incomeSourceId":"$testIncomeSourceId"
         |        }
         |      ]
         |  }
         |}
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
          addressDetails = AddressDetails("10 FooBar Street", None, None, None, Some("GB"), None),
          typeOfBusiness = Some("test business type"),
          tradingStartDate = testDate,
          cashOrAccrualsFlag = cashOrAccFlag,
          cessationDate = None,
          cessationReason = None
        )
      )
    )

  def createBusinessHipIncomeSourceRequest
  (cashOrAccFlag: Option[String]): CreateBusinessIncomeSourceHipRequest =
    CreateBusinessIncomeSourceHipRequest(
      testMtdbsa,
      List(
        models.hip.createIncomeSource.BusinessDetails(
          accountingPeriodStartDate = testDate,
          accountingPeriodEndDate = testDate,
          tradingName = "Big Business",
          address = models.hip.createIncomeSource.AddressDetails("10 FooBar Street", None, None, None, "GB", None),
          typeOfBusiness = "test business type",
          tradingStartDate = testDate,
          cashOrAccrualsFlag = cashOrAccFlag,
          cessationDate = None,
          cessationReason = None
        )
      )
    )

  def testCreateSelfEmploymentIncomeSourceRequest(cashOrAccFlag: Option[String] = Some("CASH")): JsValue =
    Json.toJson(createBusinessIncomeSourceRequest(cashOrAccFlag))

  def testCreateSelfEmploymentHipIncomeSourceRequest(cashOrAccFlag: Option[String] = Some("C")): JsValue =
    Json.toJson(createBusinessHipIncomeSourceRequest(cashOrAccFlag))

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

  val testCreateUKPropertyHipRequest: JsValue =
    Json.toJson(
      CreateUKPropertyIncomeSourceHipRequest(
        testMtdbsa,
        models.hip.createIncomeSource.PropertyDetails(
          tradingStartDate = Some(testDate),
          cashOrAccrualsFlag = Some("C"),
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

  val testCreateForeignPropertyHipRequest: JsValue =
    Json.toJson(
      CreateForeignPropertyIncomeSourceHipRequest(
        testMtdbsa,
        models.hip.createIncomeSource.PropertyDetails(
          tradingStartDate = Some(testDate),
          cashOrAccrualsFlag = Some("A"),
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

  val testCreateHipForeignPropertyRequestNoFlag: JsValue =
    Json.toJson(
      CreateForeignPropertyIncomeSourceHipRequest(
        testMtdbsa,
        models.hip.createIncomeSource.PropertyDetails(
          tradingStartDate = Some(testDate),
          cashOrAccrualsFlag = None,
          startDate = testDate
        )
      )
    )

  val testCreateBusinessDetailsSuccessResponse: JsValue = {
    Json.toJson(
      CreateBusinessDetailsHipModel(
        IncomeSourceIdDetails(
          List(
            IncomeSource(incomeSourceId = "AAIS12345678901")
          )
        ))
    )
  }
}
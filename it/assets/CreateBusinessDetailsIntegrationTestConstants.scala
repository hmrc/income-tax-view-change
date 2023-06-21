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

package assets

import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.CreateBusinessDetailsModel
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
         |{
         |	"incomeSourceId":"$testIncomeSourceId"
         |}
         |""".stripMargin)
  }

  val testBusinessDetails: JsValue = {
    Json.toJson(
      CreateBusinessDetailsModel(
        tradingName = Some("Big Business"),
        tradingStartDate = Some(LocalDate.of(2021,1,1)),
        typeOfBusiness = Some("Plumbing"),
        addressLine1 = Some("12 Green Ave."),
        addressLine2 = Some("Kensington"),
        addressLine3 = Some("London"),
        addressLine4 = None,
        postalCode = Some("W24PL"),
        countryCode = Some("GB"),
        cashOrAccrualsFlag = true,
        accountingPeriodStartDate = Some(LocalDate.of(2021,4,6)),
        accountingPeriodEndDate = Some(LocalDate.of(2022,4,5))
      )
    )
  }
}
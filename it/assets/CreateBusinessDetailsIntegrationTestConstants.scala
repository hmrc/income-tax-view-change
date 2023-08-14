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

import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsModel, IncomeSource}
import play.api.libs.json.{JsValue, Json}

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

  val testBusinessDetails: JsValue = {
    Json.toJson(
      CreateBusinessDetailsModel(
       List(
         IncomeSource(incomeSourceId = "AAIS12345678901")
       )
      )
    )
  }
}
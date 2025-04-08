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

package constants

import models.calculationList.{CalculationListModel, CalculationListResponseModel}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}

object CalculationListIntegrationTestConstants {

  val successResponse: JsValue =
    Json.arr(
      Json.obj(
        "calculationId" -> "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        "calculationTimestamp" -> "2023-10-31T12:55:51.159Z",
        "calculationType" -> "finalDeclaration",
        "crystallised" -> JsBoolean(true)
      )
    )

  val successResponseHip: JsValue =
    Json.arr(
      Json.obj(
        "calculationId" -> "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        "calculationTimestamp" -> "2023-10-31T12:55:51Z",
        "calculationType" -> "crystallisation",
        "crystallised" -> JsBoolean(true)
      )
    )

  val failureResponse: (String, String) => JsObject = (code: String, reason: String) =>
    Json.obj("code" -> code, "reason" -> reason)

  val calculationListFull: CalculationListResponseModel = {
    CalculationListResponseModel(
      calculations = Seq(CalculationListModel(
        calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        calculationTimestamp = "2023-10-31T12:55:51.159Z",
        calculationType = "finalDeclaration",
        crystallised = Some(true)
      ))
    )
  }

  val calculationListHipFull: CalculationListResponseModel = {
    CalculationListResponseModel(
      calculations = Seq(CalculationListModel(
        calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        calculationTimestamp = "2023-10-31T12:55:51Z",
        calculationType = "crystallisation",
        crystallised = Some(true)
      ))
    )
  }
}

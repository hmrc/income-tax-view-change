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

import constants.BaseTestConstants._
import constants.HipBusinessDetailsTestConstants._
import constants.HipPropertyDetailsTestConstants._
import models.hip.core.{NinoErrorModel, NinoModel}
import models.hip.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel}
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse

object HipIncomeSourceDetailsTestConstants {

  val testIncomeSourceDetailsModel = IncomeSourceDetailsModel(
    nino = testNino,
    mtdbsa = testMtdId,
    yearOfMigration = Some("2019"),
    businesses = List(testBusinessDetailsModel, testMinimumBusinessDetailsModel),
    properties = List(testPropertyDetailsModel)
  )

  val testMinimumIncomeSourceDetailsModel = IncomeSourceDetailsModel(
    nino = testNino,
    mtdbsa = testMtdId,
    yearOfMigration = None,
    businesses = List(),
    properties = List()
  )

  val testHipIncomeSourceDetailsJson = Json.obj(
    "success" -> Json.obj(
    "taxPayerDisplayResponse" -> Json.obj(
      "safeId" -> "XAIT12345678908",
      "nino" -> testNino,
      "mtdId" -> testMtdId,
      "yearOfMigration" -> "2019",
      "businessData" -> Json.arr(testBusinessDetailsJson, testMinimumBusinessDetailsJson),
      "propertyData" -> Json.arr(testPropertyDetailsJson))
  ))


  val testIncomeSourceDetailsToJson = Json.obj(
    "nino" -> testNino,
    "mtdbsa" -> testMtdId,
    "yearOfMigration" -> "2019",
    "businesses" -> Json.arr(
      testBusinessDetailsToJson,
      testMinimumBusinessDetailsToJson),
    "properties" -> Json.arr(
      testPropertyDetailsToJson)
  )

  val testHipMinimumIncomeSourceDetailsJson = Json.obj(
    "success" -> Json.obj(
    "taxPayerDisplayResponse" -> Json.obj(
      "nino" -> testNino,
      "mtdId" -> testMtdId
    )
  ))

  val testNinoModel = NinoModel(testNino)
  val testIncomeSourceDetailsError = IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
  val testNinoError = NinoErrorModel(Status.INTERNAL_SERVER_ERROR, "Dummy error message")

  val successResponse = HttpResponse(Status.OK, testHipIncomeSourceDetailsJson, Map.empty)
  val badJson = HttpResponse(Status.OK, Json.toJson("{}"), Map.empty)
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
  val notFoundBadResponse = HttpResponse(Status.NOT_FOUND, "Dummy error message")
}

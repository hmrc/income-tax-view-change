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

import constants.BaseIntegrationTestConstants.{testMtdbsa, testNino}
import constants.HipBusinessDetailsIntegrationTestConstants.{testBusinessModel, testPropertyDetailsModel}
import models.hip.core.NinoErrorModel
import models.hip.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status

object HipIncomeSourceIntegrationTestConstants {

  val incomeSourceDetailsSuccess: IncomeSourceDetailsModel =
    IncomeSourceDetailsModel(
      nino = testNino,
      mtdbsa = testMtdbsa,
      yearOfMigration = Some("2019"),
      businesses = testBusinessModel,
      properties = List(testPropertyDetailsModel)
    )

  val errorJson: JsValue = {
    Json.obj("error" ->
      Json.obj(
        "code" -> "500",
        "message" -> "ISE"
      ))
  }
  val incomeSourceDetailsError: IncomeSourceDetailsError = IncomeSourceDetailsError(500, errorJson.toString())

  val ninoLookupError: NinoErrorModel = NinoErrorModel(Status.INTERNAL_SERVER_ERROR, errorJson.toString())

}

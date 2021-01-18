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

import assets.BaseIntegrationTestConstants.testNino
import assets.BusinessDetailsIntegrationTestConstants.{testBusinessModel, testPropertyDetailsModel}
import assets.NinoIntegrationTestConstants.ninoErrorJson
import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel}
import play.mvc.Http.Status

object IncomeSourceIntegrationTestConstants {

  val incomeSourceDetailsSuccess: IncomeSourceDetailsModel =
    IncomeSourceDetailsModel(
      nino = testNino,
      businesses = testBusinessModel,
      property = Some(testPropertyDetailsModel)
    )

  val incomeSourceDetailsError: IncomeSourceDetailsError = IncomeSourceDetailsError(500,ninoErrorJson.toString())

  val ninoLookupError = IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Error Message")

}

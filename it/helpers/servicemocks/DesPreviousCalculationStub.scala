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

package helpers.servicemocks

import assets.PreviousCalculationIntegrationTestConstants._
import helpers.WiremockHelper
import models.PreviousCalculation.PreviousCalculationModel
import play.api.http.Status

object DesPreviousCalculationStub {

  def url(nino: String, year:String): String =  s"""/income-tax/previous-calculation/$nino?year=$year"""

  def stubGetDesPreviousCalculation(nino: String, year: String, response: PreviousCalculationModel): Unit = {
    val previousCalculationResponse = successResponse.toString

    WiremockHelper.stubGet(url(nino, year), Status.OK, previousCalculationResponse)
  }

  def stubGetPreviousCalculationError(nino: String, year: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(nino, year), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetPreviousCalculation(nino: String, year: String): Unit =
    WiremockHelper.verifyGet(url(nino, year))

}

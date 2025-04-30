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

import constants.HipBusinessDetailsIntegrationTestConstants._
import constants.HipBusinessDetailsIntegrationTestConstants
import helpers.WiremockHelper
import models.hip.incomeSourceDetails.IncomeSourceDetailsModel
import play.api.http.Status

object BusinessDetailsHipCallWithNinoStub {

  val url: (String) => String = (nino) => s"""/etmp/RESTAdapter/itsa/taxpayer/business-details?nino=$nino"""

  def stubGetHipBusinessDetails(nino: String, response: IncomeSourceDetailsModel): Unit = {
    val ifBusinessDetailsResponse = HipBusinessDetailsIntegrationTestConstants.successResponseHip(response.nino).toString
    WiremockHelper.stubGet(url(nino), Status.OK, ifBusinessDetailsResponse)
  }

  def stubGetHipBusinessDetailsNotFound(nino: String, response: IncomeSourceDetailsModel): Unit = {
    val ifBusinessDetailsResponse = successResponseHip(response.nino).toString
    WiremockHelper.stubGet(url(nino), Status.NOT_FOUND, ifBusinessDetailsResponse)
  }

  def stubGetHipBusinessDetailsError(nino: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(nino), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetHipBusinessDetails(nino: String): Unit = {
    WiremockHelper.verifyGet(url(nino))
  }

}

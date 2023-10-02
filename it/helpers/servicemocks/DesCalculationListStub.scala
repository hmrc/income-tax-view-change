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

import assets.CalculationListIntegrationTestConstants.{failureResponse, successResponse}
import helpers.WiremockHelper
import play.api.http.Status

object DesCalculationListStub {
  def url(nino: String, taxYear: String): String = s"""/income-tax/list-of-calculation-results/$nino?taxYear=$taxYear"""

  def urlTYS(nino: String, taxYear: String): String = s"""/income-tax/view/calculations/liability/$taxYear/$nino"""


  def stubGetDesCalculationList(nino: String, taxYear: String): Unit = {
    val calculationListResponse = successResponse.toString

    WiremockHelper.stubGet(url(nino, taxYear), Status.OK, calculationListResponse)
  }

  def stubGetDesCalculationListTYS(nino: String, taxYear: String): Unit = {
    val calculationListResponse = successResponse.toString

    WiremockHelper.stubGet(urlTYS(nino, taxYear), Status.OK, calculationListResponse)
  }

  def stubGetCalculationListError(nino: String, taxYear: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(nino, taxYear), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def stubGetCalculationListTYSError(nino: String, taxYear: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(urlTYS(nino, taxYear), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetCalculationList(nino: String, taxYear: String): Unit =
    WiremockHelper.verifyGet(url(nino, taxYear))

  def verifyGetCalculationListTYS(nino: String, taxYear: String): Unit =
    WiremockHelper.verifyGet(urlTYS(nino, taxYear))
}

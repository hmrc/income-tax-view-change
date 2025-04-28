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

import constants.CalculationListIntegrationTestConstants.{failureResponse, successResponse, successResponse2083Crys, successResponse2083NotCrys}
import helpers.WiremockHelper
import play.api.http.Status
import play.api.libs.json.Json

object DesCalculationListStub {
  def url(nino: String, taxYear: String): String = s"""/income-tax/list-of-calculation-results/$nino?taxYear=$taxYear"""

  def urlTYS(nino: String, taxYear: String): String = s"""/income-tax/view/calculations/liability/$taxYear/$nino"""
  def url2083(nino: String, taxYear: String): String = s"""/income-tax/$taxYear/view/$nino/calculations-summary"""


  def stubGetDesCalculationList(nino: String, taxYear: String): Unit = {
    val calculationListResponse = successResponse.toString

    WiremockHelper.stubGet(url(nino, taxYear), Status.OK, calculationListResponse)
  }

  def stubGetDesCalculationListNotFound(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      url(nino, taxYear),
      Status.NOT_FOUND,
      Json.obj(
        "failures" ->
          Json.arr(
            Json.obj(
              "code" -> "NOT_FOUND",
              "reason" -> "The remote endpoint has indicated that the requested resource could not be found.")
          )
      ).toString())
  }

  def stubGetDesCalculationListTYS(nino: String, taxYear: String): Unit = {
    val calculationListResponse = successResponse.toString

    WiremockHelper.stubGet(urlTYS(nino, taxYear), Status.OK, calculationListResponse)
  }

  def stubGetDesCalculationList2083(nino: String, taxYear: String, isCryst: Boolean): Unit = {
    val resp = if(isCryst) successResponse2083Crys else successResponse2083NotCrys

    WiremockHelper.stubGet(url2083(nino, taxYear), Status.OK, resp.toString())
  }

  def stubGetDesCalculationListTYSNotFound(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      urlTYS(nino, taxYear),
      Status.NOT_FOUND,
      Json.obj(
        "failures" ->
          Json.arr(
            Json.obj(
              "code" -> "NOT_FOUND",
              "reason" -> "The remote endpoint has indicated that the requested resource could not be found.")
          )
      ).toString())
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

  def verifyGetCalculationList8023(nino: String, taxYear: String): Unit =
    WiremockHelper.verifyGet(url2083(nino, taxYear))
}

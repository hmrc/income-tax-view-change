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

import assets.CalculationListIntegrationTestConstants.{failureResponse, successResponse, successResponseHip}
import helpers.WiremockHelper
import play.api.http.Status
import play.api.libs.json.Json

object HipLegacyCalculationListStub {
  def url(nino: String, taxYear: String): String = s"""/itsd/calculations/liability/$nino?taxYear=$taxYear"""

  def stubGetHipCalculationList(nino: String, taxYear: String): Unit = {
    val calculationListResponse = successResponseHip.toString

    WiremockHelper.stubGet(url(nino, taxYear), Status.OK, calculationListResponse)
  }

  def stubGetHipCalculationListBadRequest(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      url(nino, taxYear),
      Status.BAD_REQUEST,
      Json.obj(
        "origin" -> "HIP",
        "response" ->
          Json.arr(
            Json.obj(
              "errorCode" -> "1117",
              "errorDescription" -> "The tax year provided is invalid")
          )
      ).toString())
  }

  def stubGetHipCalculationListUnAuthorized(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      url(nino, taxYear),
      Status.UNAUTHORIZED,
      Json.arr(
        Json.obj(
          "errorCode" -> "5009",
          "errorDescription" -> "Unsuccessful authorisation")
      ).toString())
  }

  def stubGetHipCalculationListNotFound(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      url(nino, taxYear),
      Status.NOT_FOUND,
        Json.arr(
          Json.obj(
            "errorCode" -> "5010",
            "errorDescription" -> "The Requested resource could not be found.")
        ).toString())
  }

  def stubGetHipCalculationListBadGateway(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      url(nino, taxYear),
      Status.BAD_GATEWAY,
      ""
    )
  }

  def stubGetHipCalculationListInternalServerError(nino: String, taxYear: String): Unit = {
    WiremockHelper.stubGet(
      url(nino, taxYear),
      Status.INTERNAL_SERVER_ERROR,
      Json.obj(
        "origin" -> "HIP",
        "response" ->
          Json.obj("failures" ->
            Json.arr(
              Json.obj(
                "type" -> "9999",
                "reason" -> "Random error")
            )
          )
      ).toString())
  }

  def verifyGetCalculationList(nino: String, taxYear: String): Unit =
    WiremockHelper.verifyGet(url(nino, taxYear))
}

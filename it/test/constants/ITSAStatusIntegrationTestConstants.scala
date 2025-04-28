/*
 * Copyright 2024 HM Revenue & Customs
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

import models.itsaStatus._
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.libs.json.{JsValue, Json}

object ITSAStatusIntegrationTestConstants {
  val taxableEntityId = "AA000000A"
  val taxYear = "2020"
  val statusDetail = StatusDetail("2023-06-15T15:38:33.960Z", "No Status", "Sign up - return available", Some(8000.25))
  def getStatusDetail(status: String, statusReason: String) = StatusDetail("2023-06-15T15:38:33.960Z", status, statusReason, Some(8000.25))
  val successITSAStatusResponseModel = ITSAStatusResponseModel("2019-20", Some(List(statusDetail)))
  def getSuccessITSAStatusResponseModel(status: String, statusReason: String) = ITSAStatusResponseModel("2019-20", Some(List(getStatusDetail(status, statusReason))))
  val errorITSAStatusNotFoundError = ITSAStatusResponseNotFound(NOT_FOUND, "The remote endpoint has indicated that no match found for the reference provided.")
  val failedFutureITSAStatusError = ITSAStatusResponseError(INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")

  val listSuccessITSAStatusResponseModel = List(successITSAStatusResponseModel)
  def getListSuccessITSAStatusResponseModel(status: String, statusReason: String) =
    List(getSuccessITSAStatusResponseModel(status, statusReason))

  val failureInvalidRequestResponse =
    """{
      |  "failures": [
      |    {
      |      "code": "INVALID_TAX_YEAR",
      |      "reason": "Submission has not passed validation. Invalid parameter taxYear."
      |    }
      |  ]
      |}""".stripMargin

  val errorITSAStatusError = ITSAStatusResponseError(BAD_REQUEST, failureInvalidRequestResponse)

  val successITSAStatusListResponseJson = Json.parse(
    """
      |[{
      |    "taxYear": "2019-20",
      |    "itsaStatusDetails": [
      |      {
      |        "submittedOn": "2023-06-15T15:38:33.960Z",
      |        "status": "No Status",
      |        "statusReason": "Sign up - return available",
      |        "businessIncomePriorTo2Years": 8000.25
      |      }
      |    ]
      |  }]
      |""".stripMargin)

  val successITSAStatusListHIPResponseJson: (String, String) => JsValue = (status, statusReason) =>
    Json.parse(
    s"""
      |[{
      |    "taxYear": "2019-20",
      |    "itsaStatusDetails": [
      |      {
      |        "submittedOn": "2023-06-15T15:38:33.960Z",
      |        "status": "$status",
      |        "statusReason": "$statusReason",
      |        "businessIncomePriorTo2Years": 8000.25
      |      }
      |    ]
      |  }]
      |""".stripMargin)
}

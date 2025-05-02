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

import models.itsaStatus._
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse


object ITSAStatusTestConstants {
  val statusDetail = StatusDetail("2023-06-15T15:38:33.960Z", "No Status", "Sign up - return available", Some(8000.25))
  val statusDetailHip: (String, String) => StatusDetail = (status, statusReason) =>
    statusDetail.copy(status = status, statusReason = statusReason)
  val statusDetailMinimal = StatusDetail("2023-06-15T15:38:33.960Z", "No Status", "Sign up - return available", None)
  val statusDetailMinimalHip: (String, String) => StatusDetail = (status, statusReason) =>
    statusDetailMinimal.copy(status = status, statusReason = statusReason)
  val successITSAStatusResponseModel = ITSAStatusResponseModel("2019-20", Some(List(statusDetail)))
  val successITSAStatusResponseModelHip: (String, String) => ITSAStatusResponseModel = (status, statusReason) =>
    ITSAStatusResponseModel("2019-20", Some(List(statusDetailHip(status, statusReason))))
  val successITSAStatusResponseModelMinimal = ITSAStatusResponseModel("2019-20", None)
  val errorITSAStatusError = ITSAStatusResponseError(BAD_REQUEST, "Dummy message")
  val badJsonErrorITSAStatusError = ITSAStatusResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing ITSA Status Response")
  val errorITSAStatusNotFoundError = ITSAStatusResponseNotFound(NOT_FOUND, "Dummy message")
  val failedFutureITSAStatusError = ITSAStatusResponseError(INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")


  val statusDetailMinimalJson = Json.parse(
    """
      |{
      | "submittedOn": "2023-06-15T15:38:33.960Z",
      | "status": "No Status",
      | "statusReason": "Sign up - return available"
      |}
      |""".stripMargin)

  val statusDetailMinHipJson: (String, String) => JsValue = (status, statusReason) => Json.parse(
    s"""
      |{
      | "submittedOn": "2023-06-15T15:38:33.960Z",
      | "status": "$status",
      | "statusReason": "$statusReason"
      |}
      |""".stripMargin)


  val successITSAStatusResponseModelMinimalJson = Json.parse(
    """
      |{
      | "taxYear": "2019-20"
      |}
      |""".stripMargin
  )
  val successITSAStatusResponseHipJson: (String, String) => JsValue = (status, statusReason) => Json.parse(
    s"""
      |{
      |    "taxYear": "2019-20",
      |    "itsaStatusDetails": [
      |      {
      |        "submittedOn": "2023-06-15T15:38:33.960Z",
      |        "status": "$status",
      |        "statusReason": "$statusReason",
      |        "businessIncomePriorTo2Years": 8000.25
      |      }
      |    ]
      |  }
      |""".stripMargin)


  val successITSAStatusResponseJson = Json.parse(
    """
      |{
      |    "taxYear": "2019-20",
      |    "itsaStatusDetails": [
      |      {
      |        "submittedOn": "2023-06-15T15:38:33.960Z",
      |        "status": "No Status",
      |        "statusReason": "Sign up - return available",
      |        "businessIncomePriorTo2Years": 8000.25
      |      }
      |    ]
      |  }
      |""".stripMargin)

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

  val successHttpResponse = HttpResponse(Status.OK, Json.arr(successITSAStatusResponseJson), Map.empty)
  val errorHttpResponse = HttpResponse(Status.BAD_REQUEST, "Dummy message", Map.empty)
  val notFoundHttpResponse = HttpResponse(Status.NOT_FOUND, "Dummy message", Map.empty)
  val badJsonHttpResponse = HttpResponse(Status.OK, Json.obj(), Map.empty)
}

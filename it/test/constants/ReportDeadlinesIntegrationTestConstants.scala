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

import constants.BaseIntegrationTestConstants._
import models.obligations.ObligationStatus.Fulfilled
import models.obligations.{ObligationsModel, GroupedObligationsModel, SingleObligationModel, ObligationsErrorModel}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status

import java.time.LocalDate

object ReportDeadlinesIntegrationTestConstants {

  //ReportDeadlineModels
  def testReportDeadline(status: String = Fulfilled.name): SingleObligationModel = SingleObligationModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = None,
    obligationType = "Quarterly",
    status = status
  )

  def testDeadlineFromJson(status: String = Fulfilled.code): JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "#001",
    "status" -> status
  )

  //ReportDeadlinesModels
  val groupedObligationsModelNino = GroupedObligationsModel(testNino, Seq(testReportDeadline(), testReportDeadline()))

  def groupedObligationsModelById(id: String, status: String = Fulfilled.name): GroupedObligationsModel = GroupedObligationsModel(id, Seq(testReportDeadline(status), testReportDeadline(status)))

  val obligationsModel = ObligationsModel(Seq(groupedObligationsModelById("XAIS009998898"), groupedObligationsModelNino, groupedObligationsModelById("XAIS0000067890")))

  def obligationsModelWithStatus(nino: String, status: String) = ObligationsModel(Seq(groupedObligationsModelById(nino, status)))

  val obligationsError = ObligationsErrorModel(Status.INTERNAL_SERVER_ERROR, "ISE")

  def successResponse(nino: String): JsValue = {
    Json.obj(
      "obligations" -> Json.arr(
        Json.obj(
          "identification" -> Json.obj(
            "incomeSourceType" -> "ITSB",
            "referenceNumber" -> "XAIS009998898",
            "referenceType" -> "MTDBIS"
          ),
          "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson(), testDeadlineFromJson()))
        ),
        Json.obj(
          "identification" -> Json.obj(
            "incomeSourceType" -> "ITSB",
            "referenceNumber" -> nino,
            "referenceType" -> "MTDBIS"
          ),
          "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson(), testDeadlineFromJson()))
        ),
        Json.obj(
          "identification" -> Json.obj(
            "incomeSourceType" -> "ITSB",
            "referenceNumber" -> "XAIS0000067890",
            "referenceType" -> "MTDBIS"
          ),
          "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson(), testDeadlineFromJson()))
        )
      )
    )
  }

  def successResponseWithStatus(nino: String, statusCode: String): JsValue = {
    Json.obj(
      "obligations" -> Json.arr(
        Json.obj(
          "identification" -> Json.obj(
            "incomeSourceType" -> "ITSB",
            "referenceNumber" -> nino,
            "referenceType" -> "MTDBIS"
          ),
          "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson(statusCode), testDeadlineFromJson(statusCode)))
        )
      )
    )
  }

  def failureResponse(code: String, reason: String): JsValue =
    Json.obj(
      "code" -> code,
      "reason" -> reason
    )

}

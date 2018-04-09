/*
 * Copyright 2018 HM Revenue & Customs
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

import java.time.LocalDate

import models.reportDeadlines.{IncomeSourceModel, ReportDeadlineModel, ReportDeadlinesErrorModel, ReportDeadlinesModel}
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.HttpResponse
import play.mvc.Http.Status


object ReportDeadlinesTestConstants {

  //Report Deadline
  val testReceivedDeadline: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001"
  )

  val testReceivedDeadlineFromJson: JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "#001",
    "inboundCorrespondenceDateReceived" -> "2018-05-01"
  )

  val testReceivedDeadlineToJson: JsValue = Json.obj(
    "start" -> "2017-06-01",
    "end" -> "2018-05-31",
    "due" -> "2018-06-01",
    "periodKey" -> "#001"
  )

  val testDeadline: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001"
  )

  val testDeadlineFromJson: JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "#001"
  )

  val testDeadlineToJson: JsValue = Json.obj(
    "start" -> "2017-06-01",
    "end" -> "2018-05-31",
    "due" -> "2018-06-01",
    "periodKey" -> "#001"
  )


  //IncomeSourceModels
  val testIncomeSourceModel: IncomeSourceModel = IncomeSourceModel("12345" ,"Biz" ,Seq(testDeadline, testDeadline, testReceivedDeadline, testDeadline))

  val testIncomeSourceModelFromJson: JsValue =
    Json.obj(
      "identification" -> Json.obj(
        "incomeSourceType" -> "ITSB",
        "referenceNumber" -> "12345",
        "referenceType" -> "MTDBSA"
      ),
      "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson, testDeadlineFromJson, testReceivedDeadlineFromJson, testDeadlineFromJson))
    )

  val testIncomeSourceModelPropFromJson: JsValue =
    Json.obj(
      "identification" -> Json.obj(
        "incomeSourceType" -> "ITSP",
        "referenceNumber" -> "12345",
        "referenceType" -> "MTDBSA"
      ),
      "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson, testDeadlineFromJson, testReceivedDeadlineFromJson, testDeadlineFromJson))
    )

  val testIncomeSourceModelITSAFromJson: JsValue =
    Json.obj(
      "identification" -> Json.obj(
        "incomeSourceType" -> "ITSA",
        "referenceNumber" -> "12345",
        "referenceType" -> "MTDBSA"
      ),
      "obligationDetails" -> Json.toJson(Seq(testDeadlineFromJson, testDeadlineFromJson, testReceivedDeadlineFromJson, testDeadlineFromJson))
    )

  val testIncomeSourceModelToJson: JsValue =
    Json.obj(
      "incomeSourceId" -> "12345",
      "propOrBiz" -> "Biz",
      "obligationDetails" -> Json.toJson(Seq(testDeadlineToJson, testDeadlineToJson, testReceivedDeadlineToJson, testDeadlineToJson))
    )


  //Report Deadlines
  val testReportDeadlines: ReportDeadlinesModel =
    ReportDeadlinesModel(Seq(testIncomeSourceModel))

  val testReportDeadlinesFromJson: JsValue = Json.obj(
    "obligations" -> Json.arr(
      testIncomeSourceModelFromJson
    )
  )

  val testReportDeadlinesToJson: JsValue = Json.obj(
    "obligations" -> Seq(testIncomeSourceModelToJson)
  )

  val testReportDeadlinesError: ReportDeadlinesErrorModel = ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR,"Error Message")

  //Connector Responses
  val successResponse = HttpResponse(Status.OK, Some(testReportDeadlinesFromJson))
  val badJson = HttpResponse(Status.OK, responseJson = Some(Json.parse("{}")))
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Error Message"))
}

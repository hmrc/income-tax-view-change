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

import models.reportDeadlines.{ReportDeadlineModel, ReportDeadlinesModel}
import play.api.libs.json.{JsObject, JsValue, Json}

object ReportDeadlinesTestConstants {

  //Report Deadline
  val testReceivedDeadline: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = Some(LocalDate.parse("2018-05-01"))
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
    "periodKey" -> "#001",
    "dateReceived" -> "2018-05-01"
  )

  val testDeadline: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = None
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


  //Report Deadlines
  val testReportDeadlines: ReportDeadlinesModel =
    ReportDeadlinesModel(Seq(testDeadline, testDeadline, testReceivedDeadline, testDeadline))

  val testReportDeadlinesFromJson: JsValue = Json.obj(
    "obligations" -> Json.toJson(Seq(testDeadlineFromJson, testDeadlineFromJson, testReceivedDeadlineFromJson, testDeadlineFromJson))
  )

  val testReportDeadlinesToJson: JsValue = Json.obj(
    "obligations" -> Json.toJson(Seq(testDeadlineToJson, testDeadlineToJson, testReceivedDeadlineToJson, testDeadlineToJson))
  )
}

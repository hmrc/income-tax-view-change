/*
 * Copyright 2019 HM Revenue & Customs
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

import assets.BaseTestConstants._
import models.reportDeadlines.{ObligationsModel, ReportDeadlineModel, ReportDeadlinesErrorModel, ReportDeadlinesModel}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse


object ReportDeadlinesTestConstants {

  //Report Deadline
  val testReceivedDeadlineQuarterly: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = Some(LocalDate.parse("2018-05-01")),
    obligationType = "Quarterly"
  )

  val testReceivedDeadlineEOPS: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "EOPS",
    dateReceived = Some(LocalDate.parse("2018-05-01")),
    obligationType = "EOPS"
  )

  val testReceivedDeadlineCrystallised: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = Some(LocalDate.parse("2018-05-01")),
    obligationType = "Crystallised"
  )

  val testReceivedDeadlineFromJson: JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "#001",
    "inboundCorrespondenceDateReceived" -> "2018-05-01"
  )

  val testReceivedEOPSDeadlineFromJson: JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "EOPS",
    "inboundCorrespondenceDateReceived" -> "2018-05-01"
  )

  val testReceivedDeadlineToJson: JsValue = Json.obj(
    "start" -> "2017-06-01",
    "end" -> "2018-05-31",
    "due" -> "2018-06-01",
    "periodKey" -> "#001",
    "dateReceived" -> "2018-05-01",
    "obligationType" -> "Quarterly"
  )

  val testDeadline: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = None,
    obligationType = "Quarterly"
  )

  val testCrystallised: ReportDeadlineModel = ReportDeadlineModel(
    start = LocalDate.parse("2017-01-01"),
    end = LocalDate.parse("2018-01-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = None,
    obligationType = "Crystallised"
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
    "periodKey" -> "#001",
    "obligationType" -> "Quarterly"
  )

  //Report Deadlines
  val testReportDeadlines_1: ReportDeadlinesModel =
    ReportDeadlinesModel(testIncomeSourceID_1, Seq(testDeadline, testDeadline, testReceivedDeadlineQuarterly, testDeadline))

  val testReportDeadlines_2: ReportDeadlinesModel =
    ReportDeadlinesModel(testIncomeSourceID_2, Seq(testDeadline, testDeadline, testReceivedDeadlineQuarterly, testDeadline))

  val testReportDeadlines_3: ReportDeadlinesModel =
    ReportDeadlinesModel(testIncomeSourceID_3, Seq(testDeadline, testDeadline, testReceivedDeadlineQuarterly, testDeadline))

  val testReportDeadlines_4: ReportDeadlinesModel =
    ReportDeadlinesModel(testIncomeSourceID_4, Seq(testCrystallised))

  val testReportDeadlinesFromJson: JsValue =
    Json.obj(
      "identification" -> Json.obj(
        "incomeSourceType" -> "ITSB",
        "referenceNumber" -> testIncomeSourceID_1
      ),
      "obligationDetails" -> Json.arr(
        testDeadlineFromJson,
        testDeadlineFromJson,
        testReceivedDeadlineFromJson,
        testDeadlineFromJson
      )
    )

  val testReportDeadlinesToJson: JsValue = Json.obj(
    "identification" -> testIncomeSourceID_1,
    "obligations" -> Json.toJson(Seq(testDeadlineToJson, testDeadlineToJson, testReceivedDeadlineToJson, testDeadlineToJson))
  )

  val testReportDeadlinesError: ReportDeadlinesErrorModel = ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR,"Error Message")


  val testObligations: ObligationsModel = ObligationsModel(Seq(testReportDeadlines_1, testReportDeadlines_2, testReportDeadlines_3))

  val testObligationsFromJson: JsValue = Json.obj(
    "obligations" -> Json.arr(
      Json.obj(
        "identification" -> Json.obj(
          "incomeSourceType" -> "ITSB",
          "referenceNumber" -> testIncomeSourceID_1
        ),
        "obligationDetails" -> Json.arr(
          testDeadlineFromJson,
          testDeadlineFromJson,
          testReceivedDeadlineFromJson,
          testDeadlineFromJson
        )
      ),
      Json.obj(
        "identification" -> Json.obj(
          "incomeSourceType" -> "ITSB",
          "referenceNumber" -> testIncomeSourceID_2
        ),
        "obligationDetails" -> Json.arr(
          testDeadlineFromJson,
          testDeadlineFromJson,
          testReceivedDeadlineFromJson,
          testDeadlineFromJson
        )
      ),
      Json.obj(
        "identification" -> Json.obj(
          "incomeSourceType" -> "ITSB",
          "referenceNumber" -> testIncomeSourceID_3
        ),
        "obligationDetails" -> Json.arr(
          testDeadlineFromJson,
          testDeadlineFromJson,
          testReceivedDeadlineFromJson,
          testDeadlineFromJson
        )
      )
    )
  )

  val testObligationsToJson: JsValue = Json.obj(
    "obligations" -> Json.arr(
      Json.obj(
        "identification" -> testIncomeSourceID_1,
        "obligations" -> Json.toJson(Seq(testDeadlineToJson, testDeadlineToJson, testReceivedDeadlineToJson, testDeadlineToJson))
      ),
      Json.obj(
        "identification" -> testIncomeSourceID_2,
        "obligations" -> Json.toJson(Seq(testDeadlineToJson, testDeadlineToJson, testReceivedDeadlineToJson, testDeadlineToJson))
      ),
      Json.obj(
        "identification" -> testIncomeSourceID_3,
        "obligations" -> Json.toJson(Seq(testDeadlineToJson, testDeadlineToJson, testReceivedDeadlineToJson, testDeadlineToJson))
      )
    )
  )

  //Connector Responses
  val successResponse = HttpResponse(Status.OK, Some(testObligationsFromJson))
  val badJson = HttpResponse(Status.OK, responseJson = Some(Json.parse("{}")))
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Error Message"))
}

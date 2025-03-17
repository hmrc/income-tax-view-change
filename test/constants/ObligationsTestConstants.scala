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

import java.time.LocalDate
import BaseTestConstants._
import models.obligations.ObligationStatus._
import models.obligations.{GroupedObligationsModel, ObligationStatus, ObligationsErrorModel, ObligationsModel, SingleObligationModel}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse

object ObligationsTestConstants {

  //Report Deadline
  def testReceivedDeadlineQuarterly(status: String = ObligationStatus.Fulfilled.name): SingleObligationModel = SingleObligationModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = Some(LocalDate.parse("2018-05-01")),
    obligationType = "Quarterly",
    status = status
  )

  val testReceivedDeadlineEOPS: SingleObligationModel = SingleObligationModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "EOPS",
    dateReceived = Some(LocalDate.parse("2018-05-01")),
    obligationType = "EOPS",
    status = Fulfilled.name
  )

  def testReceivedDeadlineCrystallised(status: String = ObligationStatus.Fulfilled.name): SingleObligationModel = SingleObligationModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = Some(LocalDate.parse("2018-05-01")),
    obligationType = "Crystallisation",
    status = status
  )

  def testReceivedDeadlineFromJson(status: String = ObligationStatus.Fulfilled.name): JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "#001",
    "inboundCorrespondenceDateReceived" -> "2018-05-01",
    "status" -> status
  )

  def testReceivedEOPSDeadlineFromJson(status: String = ObligationStatus.Fulfilled.code): JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "EOPS",
    "inboundCorrespondenceDateReceived" -> "2018-05-01",
    "status" -> status
  )

  def testReceivedDeadlineToJson(status: String = ObligationStatus.Fulfilled.name): JsValue = Json.obj(
    "start" -> "2017-06-01",
    "end" -> "2018-05-31",
    "due" -> "2018-06-01",
    "periodKey" -> "#001",
    "dateReceived" -> "2018-05-01",
    "obligationType" -> "Quarterly",
    "status" -> status
  )

  def testDeadline(status: String = Fulfilled.name): SingleObligationModel = SingleObligationModel(
    start = LocalDate.parse("2017-06-01"),
    end = LocalDate.parse("2018-05-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = None,
    obligationType = "Quarterly",
    status = status
  )

  def testCrystallised(status: String = Fulfilled.name): SingleObligationModel = SingleObligationModel(
    start = LocalDate.parse("2017-01-01"),
    end = LocalDate.parse("2018-01-31"),
    due = LocalDate.parse("2018-06-01"),
    periodKey = "#001",
    dateReceived = None,
    obligationType = "Crystallisation",
    status = status
  )

  def testDeadlineFromJson(status: String = Fulfilled.name): JsValue = Json.obj(
    "inboundCorrespondenceFromDate" -> "2017-06-01",
    "inboundCorrespondenceToDate" -> "2018-05-31",
    "inboundCorrespondenceDueDate" -> "2018-06-01",
    "periodKey" -> "#001",
    "status" -> status
  )

  def testDeadlineToJson(status: String = Fulfilled.name): JsValue = Json.obj(
    "start" -> "2017-06-01",
    "end" -> "2018-05-31",
    "due" -> "2018-06-01",
    "periodKey" -> "#001",
    "obligationType" -> "Quarterly",
    "status" -> status
  )

  //Report Deadlines
  val testGroupedObligationsModel_1: GroupedObligationsModel =
    GroupedObligationsModel(testNino, Seq(testDeadline(), testDeadline(), testReceivedDeadlineQuarterly(), testDeadline()))

  val testGroupedObligationsModel_2: GroupedObligationsModel =
    GroupedObligationsModel(testNino, Seq(testDeadline(), testDeadline(), testReceivedDeadlineQuarterly(), testDeadline()))

  val testGroupedObligationsModel_3: GroupedObligationsModel =
    GroupedObligationsModel(testNino, Seq(testDeadline(), testDeadline(), testReceivedDeadlineQuarterly(), testDeadline()))

  val testGroupedObligationsModelFromJson: JsValue =
    Json.obj(
      "identification" -> Json.obj(
        "incomeSourceType" -> "ITSB",
        "referenceNumber" -> testNino
      ),
      "obligationDetails" -> Json.arr(
        testDeadlineFromJson(),
        testDeadlineFromJson(),
        testReceivedDeadlineFromJson(),
        testDeadlineFromJson()
      )
    )

  val testReportDeadlinesToJson: JsValue = Json.obj(
    "identification" -> testNino,
    "obligations" -> Json.toJson(Seq(testDeadlineToJson(), testDeadlineToJson(), testReceivedDeadlineToJson(), testDeadlineToJson()))
  )

  val testReportDeadlinesError: ObligationsErrorModel =
    ObligationsErrorModel(Status.INTERNAL_SERVER_ERROR, "Error Message")

  val testReportDeadlinesErrorJson: ObligationsErrorModel =
    ObligationsErrorModel(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data")

  def testReportDeadlinesErrorFutureFailed(exceptionMessage: String): ObligationsErrorModel =
    ObligationsErrorModel(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, $exceptionMessage")

  val testReportDeadlinesNoContentNino: ObligationsErrorModel =
    ObligationsErrorModel(Status.NO_CONTENT, "Could not retrieve report deadlines for nino provided")

  val testObligations: ObligationsModel = ObligationsModel(Seq(testGroupedObligationsModel_1, testGroupedObligationsModel_2, testGroupedObligationsModel_3))

  val testObligationsFromJson: JsValue = Json.obj(
    "obligations" -> Json.arr(
      Json.obj(
        "identification" -> Json.obj(
          "incomeSourceType" -> "ITSB",
          "referenceNumber" -> testNino
        ),
        "obligationDetails" -> Json.arr(
          testDeadlineFromJson(),
          testDeadlineFromJson(),
          testReceivedDeadlineFromJson(),
          testDeadlineFromJson()
        )
      ),
      Json.obj(
        "identification" -> Json.obj(
          "incomeSourceType" -> "ITSB",
          "referenceNumber" -> testNino
        ),
        "obligationDetails" -> Json.arr(
          testDeadlineFromJson(),
          testDeadlineFromJson(),
          testReceivedDeadlineFromJson(),
          testDeadlineFromJson()
        )
      ),
      Json.obj(
        "identification" -> Json.obj(
          "incomeSourceType" -> "ITSB",
          "referenceNumber" -> testNino
        ),
        "obligationDetails" -> Json.arr(
          testDeadlineFromJson(),
          testDeadlineFromJson(),
          testReceivedDeadlineFromJson(),
          testDeadlineFromJson()
        )
      )
    )
  )

  val testObligationsToJson: JsValue = Json.obj(
    "obligations" -> Json.arr(
      Json.obj(
        "identification" -> testNino,
        "obligations" -> Json.toJson(Seq(testDeadlineToJson(), testDeadlineToJson(), testReceivedDeadlineToJson(), testDeadlineToJson()))
      ),
      Json.obj(
        "identification" -> testNino,
        "obligations" -> Json.toJson(Seq(testDeadlineToJson(), testDeadlineToJson(), testReceivedDeadlineToJson(), testDeadlineToJson()))
      ),
      Json.obj(
        "identification" -> testNino,
        "obligations" -> Json.toJson(Seq(testDeadlineToJson(), testDeadlineToJson(), testReceivedDeadlineToJson(), testDeadlineToJson()))
      )
    )
  )

  //Connector Responses
  val successResponse = HttpResponse(Status.OK, testObligationsFromJson, Map.empty)
  val badJson = HttpResponse(Status.OK, Json.parse("{}"), Map.empty)
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "Error Message")
}

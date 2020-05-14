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

import java.time.LocalDate

import assets.ReportDeadlinesIntegrationTestConstants._
import helpers.WiremockHelper
import play.api.http.Status

object DesReportDeadlinesStub {

  def url(nino: String, openObligations: Boolean = true): String = {
    val status: String = if (openObligations) "O" else "F"
    val toDate: LocalDate = LocalDate.now()
    val fromDate: LocalDate = toDate.minusDays(365)
    val dateParameters: String = if (openObligations) "" else s"&from=$fromDate&to=$toDate"
    s"/enterprise/obligation-data/nino/$nino/ITSA?status=$status$dateParameters"
  }

  def stubGetDesOpenReportDeadlines(nino: String): Unit = {
    val desReportDeadlinesResponse = successResponse(nino).toString
    WiremockHelper.stubGet(url(nino), Status.OK, desReportDeadlinesResponse)
  }

  def stubGetDesOpenReportDeadlinesError(nino: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(nino), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetOpenDesReportDeadlines(nino: String): Unit =
    WiremockHelper.verifyGet(url(nino))

  def stubGetDesFulfilledReportDeadlines(nino: String): Unit = {
    val desReportDeadlinesResponse = successResponse(nino).toString
    WiremockHelper.stubGet(url(nino, openObligations = false), Status.OK, desReportDeadlinesResponse)
  }

  def stubGetDesFulfilledReportDeadlinesError(nino: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(nino, openObligations = false), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetFulfilledDesReportDeadlines(nino: String): Unit =
    WiremockHelper.verifyGet(url(nino, openObligations = false))

}

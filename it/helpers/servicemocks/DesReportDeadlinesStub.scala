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

import assets.BaseIntegrationTestConstants.testIncomeSourceId
import helpers.WiremockHelper
import models.reportDeadlines.ReportDeadlinesResponseModel
import assets.ReportDeadlinesIntegrationTestConstants._
import play.api.http.Status

object DesReportDeadlinesStub {

  val url: String => String = nino => s"""/enterprise/obligation-data/nino/$nino/ITSA?status=O"""

  def stubGetDesReportDeadlines(nino: String): Unit = {
    val desReportDeadlinesResponse = successResponse(testIncomeSourceId).toString
    WiremockHelper.stubGet(url(nino), Status.OK, desReportDeadlinesResponse)
  }

  def stubGetDesReportDeadlinesError(nino: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(nino), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetDesReportDeadlines(nino: String): Unit =
    WiremockHelper.verifyGet(url(nino))

}

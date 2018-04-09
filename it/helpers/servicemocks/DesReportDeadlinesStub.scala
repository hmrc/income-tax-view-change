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

import assets.BaseIntegrationTestConstants.testSelfEmpId
import helpers.WiremockHelper
import models.reportDeadlines.ReportDeadlinesResponseModel
import assets.ReportDeadlinesIntegrationTestConstants._
import play.api.http.Status

object DesReportDeadlinesStub {

  val url: (String) => String = (selfEmpId) => s"""/enterprise/obligation-data/mtdbis/$selfEmpId/ITSA?status=O"""

  def stubGetDesReportDeadlines(response: ReportDeadlinesResponseModel): Unit = {
    val desReportDeadlinesResponse = successResponse(testSelfEmpId).toString
    WiremockHelper.stubGet(url(testSelfEmpId), Status.OK, desReportDeadlinesResponse)
  }

  def stubGetDesReportDeadlinesError(selfEmpId: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(url(selfEmpId), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetDesReportDeadlines(selfEmpId: String): Unit =
    WiremockHelper.verifyGet(url(selfEmpId))

}

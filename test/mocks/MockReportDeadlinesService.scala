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

package mocks

import assets.BaseTestConstants.{mtdRef, testNino}
import models.reportDeadlines.ReportDeadlinesResponseModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import services.ReportDeadlinesService
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

trait MockReportDeadlinesService extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockReportDeadlinesService: ReportDeadlinesService = mock[ReportDeadlinesService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockReportDeadlinesService)
  }

  def setupMockReportDeadlinesResponse(incomeSourceId: String, nino: String)(response: ReportDeadlinesResponseModel):
  OngoingStubbing[Future[ReportDeadlinesResponseModel]] = when(mockReportDeadlinesService.getReportDeadlines(
    ArgumentMatchers.eq(Some(incomeSourceId)), ArgumentMatchers.eq(nino))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def mockReportDeadlinesResponse(desResponse: ReportDeadlinesResponseModel):
  OngoingStubbing[Future[ReportDeadlinesResponseModel]] = setupMockReportDeadlinesResponse(mtdRef, testNino)(desResponse)


}

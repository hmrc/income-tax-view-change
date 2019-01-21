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

import connectors.ReportDeadlinesConnector
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesErrorModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future


trait MockReportDeadlinesConnector extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockReportDeadlinesConnector: ReportDeadlinesConnector = mock[ReportDeadlinesConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockReportDeadlinesConnector)
  }

  def setupMockReportDeadlinesResponse(nino: String)(response: Either[ReportDeadlinesErrorModel,ObligationsModel]):
  OngoingStubbing[Future[Either[ReportDeadlinesErrorModel, ObligationsModel]]] = when(mockReportDeadlinesConnector.getReportDeadlines(
    ArgumentMatchers.eq(nino))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

}

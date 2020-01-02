/*
 * Copyright 2020 HM Revenue & Customs
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

package services

import assets.BaseTestConstants._
import assets.ReportDeadlinesTestConstants._
import connectors.ReportDeadlinesConnector
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesResponseModel}
import org.mockito.ArgumentMatchers.{any, eq => matches}
import org.mockito.Mockito.when
import utils.TestSupport

import scala.concurrent.Future

class ReportDeadlinesServiceSpec extends TestSupport {

  trait Setup {
    val reportDeadlinesConnector: ReportDeadlinesConnector = mock[ReportDeadlinesConnector]
    val service: ReportDeadlinesService = new ReportDeadlinesService(reportDeadlinesConnector)
  }

  "getReportDeadlines" should {
    "return obligations retrieved from the connector" when {
      "they match the income source id" in new Setup {
        when(reportDeadlinesConnector.getReportDeadlines(matches(testMtdId), matches(true))(any()))
          .thenReturn(Future.successful(Right(testObligations)))

        val result: ReportDeadlinesResponseModel = await(service.getReportDeadlines(testIncomeSourceID_1, testMtdId, openObligations = true))

        result shouldBe testReportDeadlines_1
      }

      "they match the mtdid" in new Setup {
        when(reportDeadlinesConnector.getReportDeadlines(matches(testMtdId), matches(true))(any()))
          .thenReturn(Future.successful(Right(ObligationsModel(Seq(testReportDeadlines_4)))))

        val result: ReportDeadlinesResponseModel = await(service.getReportDeadlines(testIncomeSourceID_4, testMtdId, openObligations = true))

        result shouldBe testReportDeadlines_4
      }
    }

    "return an error model" when {
      "the obligations returned from the connector don't include obligations from the income source id provided" in new Setup {
        when(reportDeadlinesConnector.getReportDeadlines(matches(testMtdId), matches(true))(any()))
          .thenReturn(Future.successful(Right(testObligations)))

        val result: ReportDeadlinesResponseModel = await(service.getReportDeadlines("idNotInObligations", testMtdId, openObligations = true))

        result shouldBe testReportDeadlinesNoContentIncome
      }

      "the obligations returned from the connector don't include obligations from the nino provided" in new Setup {
        when(reportDeadlinesConnector.getReportDeadlines(matches("notfoundnino"), matches(true))(any()))
          .thenReturn(Future.successful(Right(testObligations)))

        val result: ReportDeadlinesResponseModel = await(service.getReportDeadlines("notfoundnino", "notfoundnino", openObligations = true))

        result shouldBe testReportDeadlinesNoContentMtdId
      }

      "the connector returned back an error model" in new Setup {
        when(reportDeadlinesConnector.getReportDeadlines(matches(testMtdId), matches(true))(any()))
          .thenReturn(Future.successful(Left(testReportDeadlinesError)))

        val result: ReportDeadlinesResponseModel = await(service.getReportDeadlines(testIncomeSourceID_1, testMtdId, openObligations = true))

        result shouldBe testReportDeadlinesError
      }
    }
  }

}

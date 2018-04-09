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

package services

import assets.ReportDeadlinesTestConstants._
import mocks._
import models.reportDeadlines.ReportDeadlinesResponseModel
import utils.TestSupport
import assets.ReportDeadlinesTestConstants._
import assets.BaseTestConstants.mtdRef

import scala.concurrent.Future

class ReportDeadlinesServiceSpec extends TestSupport with MockReportDeadlinesConnector {

  object TestReportDeadlinesService extends ReportDeadlinesService(mockReportDeadlinesConnector)

  "The ReportDeadlinesService" when {

    "getReportDeadlines method is called" when {

      def result: Future[ReportDeadlinesResponseModel] = TestReportDeadlinesService.getReportDeadlines(mtdRef)

      "a successful response is returned from the ReportDeadlinesConnector" should {

        "return a correctly formatted ReportDeadlinesModel" in {
          val resp: ReportDeadlinesResponseModel = testReportDeadlines
          mockReportDeadlinesResponse(resp)
          await(result) shouldBe testReportDeadlines
        }
      }

      "an Error Response is returned from the ReportDeadlinesConnector" should {

        "return a correctly formatted ReportDeadlinesError model" in {
          mockReportDeadlinesResponse(testReportDeadlinesError)
          await(result) shouldBe testReportDeadlinesError
        }
      }
    }
  }

}
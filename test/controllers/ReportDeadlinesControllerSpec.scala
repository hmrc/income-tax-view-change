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

package controllers

import assets.BaseTestConstants._
import assets.ReportDeadlinesTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockAuthorisedUser, MockReportDeadlinesService, MockUnauthorisedUser}
import play.api.http.Status
import play.api.test.FakeRequest


class ReportDeadlinesControllerSpec extends ControllerBaseSpec with MockReportDeadlinesService {

  "The ReportDeadlinesController" when {

    "called with an Authenticated user" when {

      object TestReportDeadlinesController extends ReportDeadlinesController(
        authentication = new AuthenticationPredicate(MockAuthorisedUser),
        reportDeadlinesService = mockReportDeadlinesService
      )

      "A valid ReportDeadlinesModel is returned from the ReportDeadlinesService" should {

        "return a ReportDeadlinesModel" should {

          setupMockReportDeadlinesResponse(testIncomeSourceID_1, testNino)(testReportDeadlines_1)
          lazy val result = TestReportDeadlinesController.getReportDeadlines(testIncomeSourceID_1, testNino)(FakeRequest())

          checkStatusOf(result)(Status.OK)
          checkContentTypeOf(result)("application/json")
          checkJsonBodyOf(result)(testReportDeadlines_1)

        }

      }

      "an invalid response from the ReportDeadlinesService" should {

        setupMockReportDeadlinesResponse(testIncomeSourceID_1, testNino)(testReportDeadlinesError)
        lazy val result = TestReportDeadlinesController.getReportDeadlines(testIncomeSourceID_1, testNino)(FakeRequest())

        checkStatusOf(result)(Status.INTERNAL_SERVER_ERROR)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(testReportDeadlinesError)
      }

    }

    "called with an Unauthenticated user" should {

      object TestReportDeadlinesController extends ReportDeadlinesController(
        authentication = new AuthenticationPredicate(MockUnauthorisedUser),
        reportDeadlinesService = mockReportDeadlinesService
      )

      lazy val result = TestReportDeadlinesController.getReportDeadlines(testIncomeSourceID_1, testNino)(FakeRequest())

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }

  }

}

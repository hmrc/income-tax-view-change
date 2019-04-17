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

package controllers

import assets.BaseTestConstants._
import assets.ReportDeadlinesTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockAuthorisedFunctions, MockAuthorisedUser, MockUnauthorisedUser}
import org.mockito.ArgumentMatchers.{any, eq => matches}
import org.mockito.Mockito.when
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import services.ReportDeadlinesService
import utils.TestSupport

import scala.concurrent.Future


class ReportDeadlinesControllerSpec extends TestSupport {

  class Setup(authorised: Boolean = true) {
    val cc: ControllerComponents = stubControllerComponents()
    val reportDeadlinesService: ReportDeadlinesService = mock[ReportDeadlinesService]
    val user: MockAuthorisedFunctions = if(authorised) MockAuthorisedUser else MockUnauthorisedUser
    val authenticationPredicate: AuthenticationPredicate = new AuthenticationPredicate(user, cc)

    val controller = new ReportDeadlinesController(
      authenticationPredicate,
      reportDeadlinesService,
      cc
    )
  }

  "getOpenObligations" should {
    s"return ${Status.OK} with valid report deadlines" in new Setup {
      when(reportDeadlinesService.getReportDeadlines(matches(testIncomeSourceID_1), matches(testNino), matches(true))(any()))
        .thenReturn(Future.successful(testReportDeadlines_1))

      val result: Result = await(controller.getOpenObligations(testIncomeSourceID_1, testNino)(FakeRequest()))

      status(result) shouldBe Status.OK
      result.body.contentType shouldBe Some("application/json")
      jsonBodyOf(result) shouldBe Json.toJson(testReportDeadlines_1)
    }

    "return the status of the error model when the service returns one" in new Setup {
      when(reportDeadlinesService.getReportDeadlines(matches(testIncomeSourceID_1), matches(testNino), matches(true))(any()))
        .thenReturn(Future.successful(testReportDeadlinesError))

      val result: Result = await(controller.getOpenObligations(testIncomeSourceID_1, testNino)(FakeRequest()))

      status(result) shouldBe testReportDeadlinesError.status
      result.body.contentType shouldBe Some("application/json")
      jsonBodyOf(result) shouldBe Json.toJson(testReportDeadlinesError)
    }

    s"return ${Status.UNAUTHORIZED} when called by an unauthorised user" in new Setup(authorised = false) {
      val result: Result = await(controller.getOpenObligations(testIncomeSourceID_1, testNino)(FakeRequest()))

      status(result) shouldBe Status.UNAUTHORIZED
    }
  }

  "getFulfilledObligations" should {
    s"return ${Status.OK} with valid report deadlines" in new Setup {
      when(reportDeadlinesService.getReportDeadlines(matches(testIncomeSourceID_1), matches(testNino), matches(false))(any()))
        .thenReturn(Future.successful(testReportDeadlines_1))

      val result: Result = await(controller.getFulfilledObligations(testIncomeSourceID_1, testNino)(FakeRequest()))

      status(result) shouldBe Status.OK
      result.body.contentType shouldBe Some("application/json")
      jsonBodyOf(result) shouldBe Json.toJson(testReportDeadlines_1)
    }

    "return the status of the error model when the service returns one" in new Setup {
      when(reportDeadlinesService.getReportDeadlines(matches(testIncomeSourceID_1), matches(testNino), matches(false))(any()))
        .thenReturn(Future.successful(testReportDeadlinesError))

      val result: Result = await(controller.getFulfilledObligations(testIncomeSourceID_1, testNino)(FakeRequest()))

      status(result) shouldBe testReportDeadlinesError.status
      result.body.contentType shouldBe Some("application/json")
      jsonBodyOf(result) shouldBe Json.toJson(testReportDeadlinesError)
    }

    s"return ${Status.UNAUTHORIZED} when called by an unauthorised user" in new Setup(authorised = false) {
      val result: Result = await(controller.getFulfilledObligations(testIncomeSourceID_1, testNino)(FakeRequest()))

      status(result) shouldBe Status.UNAUTHORIZED
    }
  }

}

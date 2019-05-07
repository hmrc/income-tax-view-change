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

package controllers

import assets.BaseIntegrationTestConstants._
import assets.ReportDeadlinesIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesReportDeadlinesStub
import models.reportDeadlines.{ReportDeadlinesErrorModel, ReportDeadlinesModel}
import play.api.http.Status._

class ReportDeadlinesControllerISpec extends ComponentSpecBase {

  "Calling the ReportDeadlinesController" when {
    "authorised with a valid request" should {
      "return a valid ReportDeadlinesModel with an income source id for open obligations" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesOpenReportDeadlines(testNino, testIncomeSourceId)

        When(s"I call GET /income-tax-view-change/$testNino/income-source/$testIncomeSourceId/report-deadlines")
        val res = IncomeTaxViewChange.getReportDeadlines(testIncomeSourceId, testNino)

        DesReportDeadlinesStub.verifyGetOpenDesReportDeadlines(testNino)

        Then("a successful response is returned with the correct model")

        res should have(
          httpStatus(OK),
          jsonBodyAs[ReportDeadlinesModel](reportDeadlines)
        )
      }

      "return a valid ReportDeadlinesModel with an income source id for fulfilled obligations" in {
        isAuthorised(true)

        And("I wiremock stub a successful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesFulfilledReportDeadlines(testNino, testMtditid)

        When(s"I call GET /income-tax-view-change/$testNino/income-source/$testMtditid/fulfilled-report-deadlines")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testMtditid, testNino)

        DesReportDeadlinesStub.verifyGetFulfilledDesReportDeadlines(testNino)

        Then("a successful response is returned with the correct model")

        res should have(
          httpStatus(OK),
          jsonBodyAs[ReportDeadlinesModel](reportDeadlinesMtdId)
        )
      }

      "return a valid ReportDeadlinesModel with no income source id for open obligations" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesOpenReportDeadlines(testNino, testMtditid)

        When(s"I call GET /income-tax-view-change/$testNino/income-source/$testMtditid/report-deadlines")
        val res = IncomeTaxViewChange.getReportDeadlines(testMtditid, testNino)

        DesReportDeadlinesStub.verifyGetOpenDesReportDeadlines(testNino)

        Then("a successful response is returned with the correct model")

        res should have(
          httpStatus(OK),
          jsonBodyAs[ReportDeadlinesModel](reportDeadlinesMtdId)
        )
      }

      "return a valid ReportDeadlinesModel with no income source id for fulfilled obligations" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesFulfilledReportDeadlines(testNino, testMtditid)

        When(s"I call GET /income-tax-view-change/$testNino/income-source/$testMtditid/report-deadlines")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testMtditid, testNino)

        DesReportDeadlinesStub.verifyGetFulfilledDesReportDeadlines(testNino)

        Then("a successful response is returned with the correct model")

        res should have(
          httpStatus(OK),
          jsonBodyAs[ReportDeadlinesModel](reportDeadlinesMtdId)
        )
      }
    }



    "authorised with a invalid request" should {
      "return a ReportDeadlinesErrorModel for open obligations" in {
        isAuthorised(true)

        And("I wiremock stub an unsuccessful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesOpenReportDeadlinesError(testNino)

        When(s"I call GET /income-tax-view-change/$testNino/income-source/$testIncomeSourceId/report-deadlines")
        val res = IncomeTaxViewChange.getReportDeadlines(testIncomeSourceId, testNino)

        DesReportDeadlinesStub.verifyGetOpenDesReportDeadlines(testNino)

        Then("a unsuccessful response is returned with an error model")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
      "return a ReportDeadlinesErrorModel for fulfilled obligations" in {
        isAuthorised(true)

        And("I wiremock stub an unsuccessful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesFulfilledReportDeadlinesError(testNino)

        When(s"I call GET /income-tax-view-change/$testNino/income-source/$testIncomeSourceId/report-deadlines")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testIncomeSourceId, testNino)

        DesReportDeadlinesStub.verifyGetFulfilledDesReportDeadlines(testNino)

        Then("a unsuccessful response is returned with an error model")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
    "unauthorised" should {
      "return an error for open obligations" in {

        isAuthorised(false)

        When(s"I call GET /income-tax-view-change/estimated-tax-liability/$testNino/$testYear")
        val res = IncomeTaxViewChange.getReportDeadlines(testIncomeSourceId, testNino)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }

      "return an error for fulfilled obligations" in {

        isAuthorised(false)

        When(s"I call GET /income-tax-view-change/estimated-tax-liability/$testNino/$testYear")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testIncomeSourceId, testNino)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }

}

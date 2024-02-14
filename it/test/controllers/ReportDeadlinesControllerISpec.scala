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

package test.controllers

import test.assets.BaseIntegrationTestConstants._
import test.assets.ReportDeadlinesIntegrationTestConstants._
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesErrorModel}
import play.api.http.Status._
import test.helpers.ComponentSpecBase
import test.helpers.servicemocks.DesReportDeadlinesStub

class ReportDeadlinesControllerISpec extends ComponentSpecBase {

  val from: String = "2020-04-06"
  val to: String = "2021-04-05"

  s"Calling GET ${controllers.routes.ReportDeadlinesController.getAllObligations(testNino, from, to)}" when {
    "the user is authenticated" when {
      "the request is valid" should {
        s"return $OK" when {
          "valid obligations are retrieved" in {
            isAuthorised(true)

            DesReportDeadlinesStub.stubGetDesAllObligations(testNino, from, to)

            val res = IncomeTaxViewChange.getAllObligations(testNino, from, to)

            DesReportDeadlinesStub.verifyGetDesAllObligations(testNino, from, to)

            res should have(
              httpStatus(OK),
              jsonBodyAs[ObligationsModel](obligationsModel)
            )
          }
        }
        s"return $INTERNAL_SERVER_ERROR" when {
          "the response retrieved is invalid" in {
            isAuthorised(true)

            DesReportDeadlinesStub.stubGetDesAllObligationsError(testNino, from, to)(OK, "{}")

            val res = IncomeTaxViewChange.getAllObligations(testNino, from, to)

            DesReportDeadlinesStub.verifyGetDesAllObligations(testNino, from, to)

            res should have(
              httpStatus(INTERNAL_SERVER_ERROR),
              jsonBodyAs[ReportDeadlinesErrorModel](ReportDeadlinesErrorModel(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data"))
            )
          }
        }
        s"return the status retrieved from the call to DES when not $OK" in {
          isAuthorised(true)

          DesReportDeadlinesStub.stubGetDesAllObligationsError(testNino, from, to)(NOT_FOUND, "Error, not found")

          val res = IncomeTaxViewChange.getAllObligations(testNino, from, to)

          DesReportDeadlinesStub.verifyGetDesAllObligations(testNino, from, to)

          res should have(
            httpStatus(NOT_FOUND),
            jsonBodyAs[ReportDeadlinesErrorModel](ReportDeadlinesErrorModel(NOT_FOUND, "Error, not found"))
          )
        }
      }
    }
    "the user is not authenticated" should {
      s"return $UNAUTHORIZED" in {
        isAuthorised(false)

        val res = IncomeTaxViewChange.getAllObligations(testNino, from, to)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }

  "Calling the ReportDeadlinesController" when {
    "authorised with a valid request" should {
      "return a valid ObligationsModel for open obligations" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesOpenReportDeadlines(testNino)

        When(s"I call GET /income-tax-view-change/$testNino/report-deadlines")
        val res = IncomeTaxViewChange.getReportDeadlines(testNino)

        DesReportDeadlinesStub.verifyGetOpenDesReportDeadlines(testNino)

        Then("a successful response is returned with the correct model")

        res should have(
          httpStatus(OK),
          jsonBodyAs[ObligationsModel](obligationsModel)
        )
      }

      "return a valid ObligationsModel for fulfilled obligations" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesFulfilledReportDeadlines(testNino)

        When(s"I call GET /income-tax-view-change/$testNino/report-deadlines")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testNino)

        DesReportDeadlinesStub.verifyGetFulfilledDesReportDeadlines(testNino)

        Then("a successful response is returned with the correct model")

        res should have(
          httpStatus(OK),
          jsonBodyAs[ObligationsModel](obligationsModel)
        )
      }
    }

    "authorised with a invalid request" should {
      "return a ReportDeadlinesErrorModel for open obligations" in {
        isAuthorised(true)

        And("I wiremock stub an unsuccessful Get Report Deadlines response")
        DesReportDeadlinesStub.stubGetDesOpenReportDeadlinesError(testNino)

        When(s"I call GET /income-tax-view-change/$testNino/report-deadlines")
        val res = IncomeTaxViewChange.getReportDeadlines(testNino)

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

        When(s"I call GET /income-tax-view-change/$testNino/report-deadlines")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testNino)

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

        When(s"I call GET /income-tax-view-change/$testNino/report-deadlines")
        val res = IncomeTaxViewChange.getReportDeadlines(testNino)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }

      "return an error for fulfilled obligations" in {

        isAuthorised(false)

        When(s"I call GET /income-tax-view-change/$testNino/report-deadlines")
        val res = IncomeTaxViewChange.getFulfilledReportDeadlines(testNino)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }

}

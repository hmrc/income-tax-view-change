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


import assets.BaseIntegrationTestConstants.{testNino, testTaxYearEnd, testTaxYearRange}
import assets.CalculationListIntegrationTestConstants.calculationListFull
import helpers.ComponentSpecBase
import helpers.servicemocks.DesCalculationListStub
import models.calculationList.CalculationListModel
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT, OK, UNAUTHORIZED}
import play.api.libs.ws.WSResponse

class CalculationListControllerISpec extends ComponentSpecBase {
  "CalculationListController.getCalculationList" should {
    "return 200 OK" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response")
        DesCalculationListStub.stubGetDesCalculationList(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)
        DesCalculationListStub.verifyGetCalculationList(testNino, testTaxYearEnd)

        Then("A success response is received")
        result should have(
          httpStatus(OK),
          jsonBodyAs[CalculationListModel](calculationListFull.calculations.head)
        )
      }
    }
    "return 204 NO_CONTENT" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response")
        DesCalculationListStub.stubGetDesCalculationListNoContent(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)
        DesCalculationListStub.verifyGetCalculationList(testNino, testTaxYearEnd)

        Then("A no content response is received")
        result should have(
          httpStatus(NO_CONTENT)
        )
      }
    }
    "return 500 INTERNAL SERVER ERROR" when {
      "1404 Get List Of Calculation Results (legacy API) returns an error response" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub an error response from 1404 Get List Of Calculation Results (legacy API)")
        DesCalculationListStub.stubGetCalculationListError(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)
        DesCalculationListStub.verifyGetCalculationList(testNino, testTaxYearEnd)

        Then("A 500 Internal Server Error response is received")
        result should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
    "return 401 UNAUTHORIZED" when {
      "user is unauthorised" in {
        Given("I am an unauthorised user")
        isAuthorised(false)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response")
        DesCalculationListStub.stubGetDesCalculationList(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)
        DesCalculationListStub.verifyGetCalculationList(testNino, testTaxYearEnd)

        Then("An unauthorized response is received")
        result should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }

  "CalculationListController.getCalculationListTYS" should {
    "return 200 OK" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1896 Get Calculation List response")
        DesCalculationListStub.stubGetDesCalculationListTYS(testNino, testTaxYearRange)

        When(s"I call /calculation-list/$testNino/$testTaxYearRange")
        val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, testTaxYearRange)
        DesCalculationListStub.verifyGetCalculationListTYS(testNino, testTaxYearRange)

        Then("A success response is received")
        result should have(
          httpStatus(OK),
          jsonBodyAs[CalculationListModel](calculationListFull.calculations.head)
        )
      }
    }
    "return 204 NO_CONTENT" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1896 Get Calculation List response")
        DesCalculationListStub.stubGetDesCalculationListTYSNoContent(testNino, testTaxYearRange)

        When(s"I call /calculation-list/$testNino/$testTaxYearRange")
        val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, testTaxYearRange)
        DesCalculationListStub.verifyGetCalculationListTYS(testNino, testTaxYearRange)

        Then("A no content response is received")
        result should have(
          httpStatus(NO_CONTENT)
        )
      }
    }
    "return 500 INTERNAL SERVER ERROR" when {
      "1896 Get Calculation List returns an error response" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub an error response from 1896 Get Calculation List")
        DesCalculationListStub.stubGetCalculationListTYSError(testNino, testTaxYearRange)

        When(s"I call /calculation-list/$testNino/$testTaxYearRange")
        val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, testTaxYearRange)
        DesCalculationListStub.verifyGetCalculationListTYS(testNino, testTaxYearRange)

        Then("A 500 Internal Server Error response is received")
        result should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
    "return 401 UNAUTHORIZED" when {
      "user is unauthorised" in {
        Given("I am an unauthorised user")
        isAuthorised(false)

        And("I wiremock stub a 1896 Get Calculation List response")
        DesCalculationListStub.stubGetDesCalculationListTYS(testNino, testTaxYearRange)

        When(s"I call /calculation-list/$testNino/$testTaxYearRange")
        val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, testTaxYearRange)
        DesCalculationListStub.verifyGetCalculationListTYS(testNino, testTaxYearRange)

        Then("An unauthorized response is received")
        result should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
}

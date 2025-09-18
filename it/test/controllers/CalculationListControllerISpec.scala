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

import constants.BaseIntegrationTestConstants.{taxYearRange25to26, taxYearRange26to27, testNino, testTaxYearEnd, testTaxYearRange}
import constants.CalculationListIntegrationTestConstants.{calculationListFull, calculationListResponseConverted}
import helpers.ComponentSpecBase
import helpers.servicemocks.DesCalculationListStub
import models.calculationList.CalculationListModel
import models.errors.{Error, MultiError}
import play.api.http.Status._
import play.api.libs.ws.WSResponse

class CalculationListControllerISpec extends ComponentSpecBase {

  "CalculationListController.getCalculationListTYS" should {
    "return 200 OK" when {
      "user is authorised and sends a valid request" that {
        "is before tax 25-26 tax year range" in {
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


        "is for 25-26 tax year" in {
          Given("I am an authorised user")
          isAuthorised(true)

          And("I wiremock stub a 2083 Get Calculation List response")
          DesCalculationListStub.stubGetDesCalculationList2083(testNino, taxYearRange25to26, true)

          When(s"I call /calculation-list/$testNino/$taxYearRange25to26")
          val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, taxYearRange25to26)
          DesCalculationListStub.verifyGetCalculationList8023(testNino, taxYearRange25to26)

          Then("A success response is received")
          result should have(
            httpStatus(OK),
            jsonBodyAs[CalculationListModel](calculationListResponseConverted(true).calculations.head)
          )
        }


        "is after 25-26 tax year" in {
          Given("I am an authorised user")
          isAuthorised(true)

          And("I wiremock stub a 1896 Get Calculation List response")
          DesCalculationListStub.stubGetDesCalculationList2083(testNino, taxYearRange26to27, false)

          When(s"I call /calculation-list/$testNino/$taxYearRange26to27")
          val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, taxYearRange26to27)
          DesCalculationListStub.verifyGetCalculationList8023(testNino, taxYearRange26to27)

          Then("A success response is received")
          result should have(
            httpStatus(OK),
            jsonBodyAs[CalculationListModel](calculationListResponseConverted(false).calculations.head)
          )
        }
      }
    }

    "return 404 NOT_FOUND" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1896 Get Calculation List response to have no calculations")
        DesCalculationListStub.stubGetDesCalculationListTYSNotFound(testNino, testTaxYearRange)

        When(s"I call /calculation-list/$testNino/$testTaxYearRange")
        val result: WSResponse = IncomeTaxViewChange.getCalculationListTYS(testNino, testTaxYearRange)

        Then("A no content response is received")
        result should have(
          httpStatus(NOT_FOUND),
          jsonBodyAs[Error](
            Error(
              "NOT_FOUND", "Resource not found"
            )
          )
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

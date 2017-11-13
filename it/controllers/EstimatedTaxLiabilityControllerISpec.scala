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

import helpers.ComponentSpecBase
import helpers.IntegrationTestConstants._
import helpers.servicemocks.{AuthStub, FinancialDataStub}
import models.LastTaxCalculation
import play.api.http.Status._

class EstimatedTaxLiabilityControllerISpec extends ComponentSpecBase {
  "Calling the EstimatedTaxLiabilityController" when {
    "authorised with a valid request" should {
      "return a valid simple calculation estimate" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Financial Data response")
        FinancialDataStub.stubGetFinancialData(testNino, testYear, testCalcType, lastTaxCalculation)

        When(s"I call GET /income-tax-view-change/estimated-tax-liability/$testNino/$testYear/$testCalcType")
        val res = IncomeTaxViewChange.getEstimatedTaxLiability(testNino, testYear, testCalcType)

        FinancialDataStub.verifyGetFinancialData(testNino, testYear, testCalcType)

        Then("a successful response is returned with the correct estimate")

        res should have(
          //Check for Status OK response (200)
          httpStatus(OK),
          //Check the response is a LastTaxCalculation
          jsonBodyAs[LastTaxCalculation](lastTaxCalculation)
        )
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET /income-tax-view-change/estimated-tax-liability/$testNino/$testYear")
        val res = IncomeTaxViewChange.getEstimatedTaxLiability(testNino, testYear, testCalcType)

        res should have(
          //Check for an Unauthorised response UNAUTHORIZED (401)
          httpStatus(UNAUTHORIZED),
          //Check for an empty response body
          emptyBody
        )
      }
    }
  }
}

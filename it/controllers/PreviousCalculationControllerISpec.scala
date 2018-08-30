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
import assets.PreviousCalculationIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesPreviousCalculationStub
import models.PreviousCalculation.{Error, PreviousCalculationModel}
import play.api.http.Status._
import play.api.libs.ws.WSResponse

class PreviousCalculationControllerISpec extends ComponentSpecBase {
  "Calling the PreviousCalculationController" when {
    "authorised with a valid request" should {
      "return a valid bill and estimate data" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Financial Data response")
        DesPreviousCalculationStub.stubGetDesPreviousCalculation(testNino, testYear, previousCalculation)

        When(s"I call GET /income-tax-view-change/getPreviousCalculation/$testNino/$testYear")
        val res: WSResponse = IncomeTaxViewChange.getPreviousCalculation(testNino, testYear)

        DesPreviousCalculationStub.verifyGetPreviousCalculation(testNino, testYear)

        Then("a successful response is returned with the correct data")

        res should have(
          httpStatus(OK),
          jsonBodyAs[PreviousCalculationModel](previousCalculation)
        )
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET /income-tax-view-change/getPreviousCalculation/$testNino/$testYear")
        val res: WSResponse = IncomeTaxViewChange.getPreviousCalculation(testNino, testYear)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
    "authorised with an invalid request" should {
      "return an error response" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Financial Data response")
        DesPreviousCalculationStub.stubGetPreviousCalculationError(testNino, testYear)

        When(s"I call GET /income-tax-view-change/getPreviousCalculation/$testNino/$testYear")
        val res: WSResponse = IncomeTaxViewChange.getPreviousCalculation(testNino, testYear)

        DesPreviousCalculationStub.verifyGetPreviousCalculation(testNino, testYear)

        Then("a successful response is returned with the correct data")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR),
          jsonBodyAs[Error](previousCalculationError)
        )
      }
    }
  }
}

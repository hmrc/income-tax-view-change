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
import helpers.servicemocks.DesBusinessDetailsStub
import models.NinoModel
import play.api.http.Status._

class NinoLookupControllerISpec extends ComponentSpecBase {
  "Calling the NinoLookupController" when {
    "authorised with a valid request" should {
      "return a valid NINO" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Des Business Details response")
        DesBusinessDetailsStub.stubGetDesBusinessDetails(testMtdRef, desBusinessDetails)

        When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
        val res = IncomeTaxViewChange.getNino(testMtdRef)

        DesBusinessDetailsStub.verifyGetDesBusinessDetails(testMtdRef)

        Then("a successful response is returned with the correct NINO")

        res should have(
          //Check for Status OK response (200)
          httpStatus(OK),
          //Check the response is a Nino
          jsonBodyAs[NinoModel](ninoLookup)
        )
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
        val res = IncomeTaxViewChange.getNino(testMtdRef)

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

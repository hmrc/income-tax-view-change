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
import models.{IncomeSourceDetailsError, IncomeSourceDetailsModel, NinoErrorModel, NinoModel}
import play.api.http.Status._

class IncomeSourceDetailsControllerISpec extends ComponentSpecBase {

  "Calling the IncomeSourceDetailsController.getNino method" when {
    "authorised with a valid request" when {
      "A success response is returned from DES" should {
        "return a valid NINO" in {

          isAuthorised(true)

          And("I wiremock stub a successful getIncomeSOurceDetails response")
          DesBusinessDetailsStub.stubGetDesBusinessDetails(testMtdRef, incomeSourceDetailsSuccess)

          When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
          val res = IncomeTaxViewChange.getNino(testMtdRef)

          DesBusinessDetailsStub.verifyGetDesBusinessDetails(testMtdRef)

          Then("a successful response is returned with the correct NINO")

          res should have(
            httpStatus(OK),
            jsonBodyAs[NinoModel](ninoLookup)
          )
        }
      }
      "An error response is returned from DES" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          DesBusinessDetailsStub.stubGetDesBusinessDetailsError(testMtdRef)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getNino(testMtdRef)

          DesBusinessDetailsStub.verifyGetDesBusinessDetails(testMtdRef)

          Then("an error response is returned")

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs[NinoErrorModel](ninoError)
          )
        }
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
        val res = IncomeTaxViewChange.getNino(testMtdRef)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }

  "Calling the IncomeSourceDetailsController.getIncomeSourceDetails method" when {
    "authorised with a valid request" when {
      "A successful response is returned from DES" should {
        "return a valid IncomeSourceDetails model" in {

          isAuthorised(true)

          And("I wiremock stub a successful getIncomeSOurceDetails response")
          DesBusinessDetailsStub.stubGetDesBusinessDetails(testMtdRef, incomeSourceDetailsSuccess)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getIncomeSources(testMtdRef)

          DesBusinessDetailsStub.verifyGetDesBusinessDetails(testMtdRef)

          Then("a successful response is returned with the correct NINO")

          res should have(
            httpStatus(OK),
            jsonBodyAs[IncomeSourceDetailsModel](incomeSourceDetailsSuccess)
          )
        }
      }
      "An error response is returned from DES" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          DesBusinessDetailsStub.stubGetDesBusinessDetailsError(testMtdRef)

          When(s"I call GET income-tax-view-change/income-sources/$testMtdRef")
          val res = IncomeTaxViewChange.getIncomeSources(testMtdRef)

          DesBusinessDetailsStub.verifyGetDesBusinessDetails(testMtdRef)

          Then("an error response is returned")

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs[IncomeSourceDetailsError](incomeSourceDetailsError)
          )
        }
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET income-tax-view-change/nino-lookup/$testMtdRef")
        val res = IncomeTaxViewChange.getNino(testMtdRef)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
}

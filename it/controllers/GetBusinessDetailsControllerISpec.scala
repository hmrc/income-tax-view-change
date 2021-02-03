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
import assets.BusinessDetailsIntegrationTestConstants.jsonSuccessOutput
import assets.IncomeSourceIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesBusinessDetailsCallWithNinoStub
import models.incomeSourceDetails.IncomeSourceDetailsError
import play.api.http.Status._

class GetBusinessDetailsControllerISpec extends ComponentSpecBase {


  "Calling the GetBusinessDetailsController.getBusinessDetails method" when {
    "authorised with a valid request" when {
      "A successful response is returned from DES" should {
        "return a valid IncomeSourceDetails model" in {

          isAuthorised(true)

          And("I wiremock stub a successful getIncomeSourceDetails response")
          DesBusinessDetailsCallWithNinoStub.stubGetDesBusinessDetails(testNino, incomeSourceDetailsSuccess)

          When(s"I call GET /get-business-details/nino/$testNino")
          val res = IncomeTaxViewChange.getBusinessDetails(testNino)

          DesBusinessDetailsCallWithNinoStub.verifyGetDesBusinessDetails(testNino)

          Then("a successful response is returned with the correct business details")

          res should have(
            httpStatus(OK),
            jsonBodyMatching(jsonSuccessOutput())
          )
        }
      }
      "An error response is returned from DES" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          DesBusinessDetailsCallWithNinoStub.stubGetDesBusinessDetailsError(testNino)

          When(s"I call GET /get-business-details/nino/$testNino")
          val res = IncomeTaxViewChange.getBusinessDetails(testNino)

          DesBusinessDetailsCallWithNinoStub.verifyGetDesBusinessDetails(testNino)

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

        When(s"I call GET /get-business-details/nino/$testNino")
        val res = IncomeTaxViewChange.getBusinessDetails(testNino)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
}

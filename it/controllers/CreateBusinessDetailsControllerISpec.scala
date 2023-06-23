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
import assets.CreateBusinessDetailsIntegrationTestConstants.{successResponse, testBusinessDetails, testIncomeSourceId}
import helpers.ComponentSpecBase
import helpers.servicemocks.DesCreateBusinessDetailsStub
import play.api.http.Status._
import play.api.libs.json.Json

class CreateBusinessDetailsControllerISpec extends ComponentSpecBase {


  "Calling the CreateBusinessDetailsController.createBusinessDetails method" when {
    "authorised with a valid request" when {
      "A successful response is returned from DES" should {
        "return a valid createBusinessDetails model" in {

          isAuthorised(true)

          And("I wiremock stub a successful createBusinessDetails response")
          DesCreateBusinessDetailsStub.stubPostDesBusinessDetails(testMtdbsa, OK, testBusinessDetails.toString, successResponse.toString)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, testBusinessDetails)

          DesCreateBusinessDetailsStub.verifyCreateDesBusinessDetails(testMtdbsa, testBusinessDetails.toString)

          Then("a successful response is returned containing the incomeSourceId")

          res should have(httpStatus(OK))
          res.body should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a invalid request" should {
      s"return ${BAD_REQUEST}" in {
        isAuthorised(true)

        val invalidRequest = Json.obj()

        And("I wiremock stub a FAIL createBusinessDetails response")
        DesCreateBusinessDetailsStub.stubPostDesBusinessDetails(testMtdbsa, BAD_REQUEST, invalidRequest.toString, successResponse.toString)

        When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
        val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, invalidRequest)

        Then(s"a status of ${BAD_REQUEST} is returned ")

        res should have(
          httpStatus(BAD_REQUEST))
      }
    }
  }
}
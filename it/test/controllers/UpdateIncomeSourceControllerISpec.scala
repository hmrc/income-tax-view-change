/*
 * Copyright 2024 HM Revenue & Customs
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

import  assets.UpdateIncomeSourceIntegrationTestConstants._
import models.updateIncomeSource.UpdateIncomeSourceResponseError
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, UNAUTHORIZED}
import play.api.libs.json.Json
import  helpers.ComponentSpecBase
import  helpers.servicemocks.IfUpdateIncomeSourceStub


class UpdateIncomeSourceControllerISpec extends ComponentSpecBase {
  "Calling the UpdateIncomeSourceController.updateIncomeSource method" when {
    "authorised with a valid request" when {
      "a success response is returned from IF" should {
        "return a timestamp of update" in {

          isAuthorised(true)

          And("I wiremock stub a successful UpdateCessationDate response")
          IfUpdateIncomeSourceStub.stubPutIfUpdateIncomeSource(requestJson.toString(), successResponseJson.toString())

          When(s"I call PUT /update-income-source")
          val res = IncomeTaxViewChange.putUpdateIncomeSource(requestJson)

          IfUpdateIncomeSourceStub.verifyPutIfUpdateIncomeSource(requestJson.toString())

          Then("a successful response is returned with a timestamp")

          res should have(
            httpStatus(OK),
            jsonBodyAs(Json.toJson(successResponse))
          )
        }
      }
      "authorised with a invalid request" should {
        s"return ${BAD_REQUEST}" in {
          isAuthorised(true)

          When(s"I call PUT /update-income-source with invalid request")
          val res = IncomeTaxViewChange.putUpdateIncomeSource(invalidRequestJson)

          Then(s"a status of ${BAD_REQUEST} is returned ")

          res should have(
            httpStatus(BAD_REQUEST))
        }
      }
      "An error response is returned from IF" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          IfUpdateIncomeSourceStub.stubPutIfUpdateIncomeSourceError()

          When(s"I call PUT /update-income-source")
          val res = IncomeTaxViewChange.putUpdateIncomeSource(requestJson)

          IfUpdateIncomeSourceStub.verifyPutIfUpdateIncomeSource(requestJson.toString())

          Then("an error response is returned")

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs[UpdateIncomeSourceResponseError](failureResponse)
          )
        }
      }

      "unauthorised" should {
        "return an error" in {

          isAuthorised(false)

          When(s"I call PUT /update-income-source")
          val res = IncomeTaxViewChange.putUpdateIncomeSource(requestJson)

          res should have(
            httpStatus(UNAUTHORIZED),
            emptyBody
          )
        }
      }
    }

  }
}
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

import constants.CreateBusinessDetailsHipIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.HipCreateBusinessDetailsStub
import models.hip.incomeSourceDetails.CreateBusinessDetailsHipErrorResponse
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyReadables.readableAsJson


class CreateBusinessDetailsControllerISpec extends ComponentSpecBase {

  "Calling CreateBusinessDetailsController.createBusinessDetails method" when {
    "authorised with a CreateBusinessIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK response with an incomeSourceId" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(CREATED, testCreateSelfEmploymentHipIncomeSourceRequest(),
              testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testCreateSelfEmploymentIncomeSourceRequest())

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateSelfEmploymentHipIncomeSourceRequest())

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a CreateUKPropertyIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK response with an incomeSourceId" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(OK, testCreateUKPropertyHipRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testCreateUKPropertyRequest)

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateUKPropertyHipRequest)

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a CreateForeignPropertyIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK with an incomeSourceId" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(OK, testCreateForeignPropertyHipRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testCreateForeignPropertyRequest)

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateForeignPropertyHipRequest)

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }

        s"return $OK with an incomeSourceId with no flag" in {
          isAuthorised(true)

          HipCreateBusinessDetailsStub
            .stubPostHipBusinessDetails(OK, testCreateHipForeignPropertyRequestNoFlag, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testCreateForeignPropertyRequestNoFlag)

          HipCreateBusinessDetailsStub.verifyCreateHipBusinessDetails(testCreateHipForeignPropertyRequestNoFlag)

          res should have(httpStatus(OK))
          res.body.toString should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a invalid request" should {
      s"return $BAD_REQUEST" in {

        isAuthorised(true)

        val invalidRequest = Json.obj()

        When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
        val res = IncomeTaxViewChange.createBusinessDetails(invalidRequest)

        Then(s"a status of $BAD_REQUEST is returned ")

        res should have(httpStatus(BAD_REQUEST))
      }
    }
    "authorised with a valid request" when {
      "API returns an error" should {
        s"return $INTERNAL_SERVER_ERROR" in {

          isAuthorised(true)

          HipCreateBusinessDetailsStub.stubPostHipBusinessDetails(
            INTERNAL_SERVER_ERROR,
            testCreateSelfEmploymentHipIncomeSourceRequest(),
            Json.toJson(CreateBusinessDetailsHipErrorResponse(INTERNAL_SERVER_ERROR, "failed to create details"))
          )

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testCreateSelfEmploymentIncomeSourceRequest())

          res should have(httpStatus(INTERNAL_SERVER_ERROR))
        }
      }
    }
  }
}

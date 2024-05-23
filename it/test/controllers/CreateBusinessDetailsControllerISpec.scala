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

import  assets.BaseIntegrationTestConstants._
import  assets.CreateBusinessDetailsIntegrationTestConstants._
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.CreateBusinessDetailsErrorResponse
import play.api.http.Status._
import play.api.libs.json.Json
import  helpers.ComponentSpecBase
import  helpers.servicemocks.DesCreateBusinessDetailsStub

class CreateBusinessDetailsControllerISpec extends ComponentSpecBase {


  "Calling CreateBusinessDetailsController.createBusinessDetails method" when {
    "authorised with a CreateBusinessIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK response with an incomeSourceId" in {

          isAuthorised(true)

          DesCreateBusinessDetailsStub
            .stubPostDesBusinessDetails(testMtdbsa, OK, testCreateSelfEmploymentIncomeSourceRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, testCreateSelfEmploymentIncomeSourceRequest)

          DesCreateBusinessDetailsStub.verifyCreateDesBusinessDetails(testMtdbsa, testCreateSelfEmploymentIncomeSourceRequest)

          res should have(httpStatus(OK))
          res.body should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a CreateUKPropertyIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK response with an incomeSourceId" in {

          isAuthorised(true)

          DesCreateBusinessDetailsStub
            .stubPostDesBusinessDetails(testMtdbsa, OK, testCreateUKPropertyRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, testCreateUKPropertyRequest)

          DesCreateBusinessDetailsStub.verifyCreateDesBusinessDetails(testMtdbsa, testCreateUKPropertyRequest)

          res should have(httpStatus(OK))
          res.body should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a CreateForeignPropertyIncomeSourceRequest model" when {
      "A successful response is returned from the API" should {
        s"return $OK with an incomeSourceId" in {

          isAuthorised(true)

          DesCreateBusinessDetailsStub
            .stubPostDesBusinessDetails(testMtdbsa, OK, testCreateForeignPropertyRequest, testCreateBusinessDetailsSuccessResponse)

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, testCreateForeignPropertyRequest)

          DesCreateBusinessDetailsStub.verifyCreateDesBusinessDetails(testMtdbsa, testCreateForeignPropertyRequest)

          res should have(httpStatus(OK))
          res.body should include(testIncomeSourceId)
        }
      }
    }
    "authorised with a invalid request" should {
      s"return $BAD_REQUEST" in {

        isAuthorised(true)

        val invalidRequest = Json.obj()

        When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
        val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, invalidRequest)

        Then(s"a status of $BAD_REQUEST is returned ")

        res should have(httpStatus(BAD_REQUEST))
      }
    }
    "authorised with a valid request" when {
      "API returns an error" should {
        s"return $INTERNAL_SERVER_ERROR" in {

          isAuthorised(true)

          DesCreateBusinessDetailsStub.stubPostDesBusinessDetails(
            testMtdbsa,
            INTERNAL_SERVER_ERROR,
            testCreateSelfEmploymentIncomeSourceRequest,
            Json.toJson(CreateBusinessDetailsErrorResponse(INTERNAL_SERVER_ERROR, "failed to create details"))
          )

          When(s"I call POST /income-tax/income-sources/mtdbsa/$testMtdbsa/ITSA/business")
          val res = IncomeTaxViewChange.createBusinessDetails(testMtdbsa, testCreateSelfEmploymentIncomeSourceRequest)

          res should have(httpStatus(INTERNAL_SERVER_ERROR))
        }
      }
    }
  }
}

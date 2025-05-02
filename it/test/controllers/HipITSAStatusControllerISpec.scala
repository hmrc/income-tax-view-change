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

import constants.ITSAStatusIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.HipITSAStatusStub
import models.hip.ITSAStatusHipApi
import models.itsaStatus.{ITSAStatusResponseError, ITSAStatusResponseModel}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, UNAUTHORIZED}


class HipITSAStatusControllerISpec extends ComponentSpecBase {
  override def config: Map[String, String] =
    super.config + (s"microservice.services.hip.${ITSAStatusHipApi()}.feature-switch" -> "true")


  "Calling the ITSAStatusController.getITSAStatus method" when {
    "authorised with a valid request" when {
      "a success response is returned from IF" should {
        "return a List of status details" in {

          isAuthorised(true)

          And("I wiremock stub a successful ITSAStatusDetails response")
          HipITSAStatusStub.stubGetHipITSAStatusDetails(successITSAStatusListHIPResponseJson("00", "00").toString())

          When(s"I call GET /itsa-status/status/$taxableEntityId/$taxYear")
          val res = IncomeTaxViewChange.getITSAStatus(taxableEntityId, taxYear)

          HipITSAStatusStub.verifyGetHipITSAStatusDetails()

          Then("a successful response is returned with status details")

          res should have(
            httpStatus(OK),
            jsonBodyAs[List[ITSAStatusResponseModel]](List(successITSAStatusResponseModel))
          )
        }
      }

      "authorised with a invalid request" should {
        s"return ${BAD_REQUEST}" in {
          isAuthorised(true)

          And("I wiremock stub a badRequest ITSAStatusDetails response")
          HipITSAStatusStub.stubGetHipITSAStatusDetailsBadRequest()


          When(s"I call GET /itsa-status/status/$taxableEntityId/$taxYear")
          val res = IncomeTaxViewChange.getITSAStatus(taxableEntityId, taxYear)

          Then(s"a status of ${BAD_REQUEST} is returned ")

          res should have(
            httpStatus(BAD_REQUEST))
        }
      }

      "An error response is returned from IF" should {
        "return an Error Response model" in {
          isAuthorised(true)

          And("I wiremock stub an error response")
          HipITSAStatusStub.stubGetHipITSAStatusDetailsError()

          When(s"I call GET /itsa-status/status/$taxableEntityId/$taxYear")
          val res = IncomeTaxViewChange.getITSAStatus(taxableEntityId, taxYear)

          HipITSAStatusStub.verifyGetHipITSAStatusDetails()

          Then("an error response is returned")

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR),
            jsonBodyAs[ITSAStatusResponseError](failedFutureITSAStatusError)
          )
        }
      }

      "unauthorised" should {
        "return an error" in {

          isAuthorised(false)

          When(s"I call GET /itsa-status/status/$taxableEntityId/$taxYear")
          val res = IncomeTaxViewChange.getITSAStatus(taxableEntityId, taxYear)

          res should have(
            httpStatus(UNAUTHORIZED),
            emptyBody
          )
        }
      }
    }
  }
}

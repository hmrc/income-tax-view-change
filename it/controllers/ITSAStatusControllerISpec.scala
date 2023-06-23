package controllers

import assets.ITSAStatusIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.IfITSAStatusStub
import models.itsaStatus.{ITSAStatusResponseError, ITSAStatusResponseModel}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, UNAUTHORIZED}


class ITSAStatusControllerISpec extends ComponentSpecBase {
  "Calling the ITSAStatusController.getITSAStatus method" when {
    "authorised with a valid request" when {
      "a success response is returned from IF" should {
        "return a List of status details" in {

          isAuthorised(true)

          And("I wiremock stub a successful ITSAStatusDetails response")
          IfITSAStatusStub.stubGetIfITSAStatusDetails(successITSAStatusListResponseJson.toString())

          When(s"I call GET /itsa-status/status/$taxableEntityId/$taxYear")
          val res = IncomeTaxViewChange.getITSAStatus(taxableEntityId, taxYear)

          IfITSAStatusStub.verifyGetIfITSAStatusDetails

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
          IfITSAStatusStub.stubGetIfITSAStatusDetailsBadRequest


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
          IfITSAStatusStub.stubGetIfITSAStatusDetailsError()

          When(s"I call GET /itsa-status/status/$taxableEntityId/$taxYear")
          val res = IncomeTaxViewChange.getITSAStatus(taxableEntityId, taxYear)

          IfITSAStatusStub.verifyGetIfITSAStatusDetails

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
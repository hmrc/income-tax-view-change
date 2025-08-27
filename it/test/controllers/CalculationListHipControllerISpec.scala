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

import constants.BaseIntegrationTestConstants.{testNino, testTaxYearEnd}
import constants.CalculationListIntegrationTestConstants.calculationListHipFull
import helpers.ComponentSpecBase
import helpers.servicemocks.HipLegacyCalculationListStub
import models.calculationList.CalculationListModel
import models.hip.{FailureResponse, Failures, GetLegacyCalcListHipApi, OriginFailuresResponse, OriginWithErrorCodeAndResponse, Response}
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.{Application, Environment, Mode}

class CalculationListHipControllerISpec extends ComponentSpecBase {

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(Map(
      "microservice.services.auth.host" -> mockHost,
      "microservice.services.auth.port" -> mockPort,
      "microservice.services.if.url" -> mockUrl,
      "microservice.services.des.url" -> mockUrl,
      "microservice.services.hip.host" -> mockHost,
      "microservice.services.hip.port" -> mockPort,
      "useBusinessDetailsIFPlatform" -> "true"
    ))
    .build()

  "CalculationListController.getCalculationList" should {
    "return 200 OK" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response")
        HipLegacyCalculationListStub.stubGetHipCalculationList(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)
        HipLegacyCalculationListStub.verifyGetCalculationList(testNino, testTaxYearEnd)

        Then("A success response is received")
        result should have(
          httpStatus(OK),
          jsonBodyAs[CalculationListModel](calculationListHipFull.calculations.head)
        )
      }
    }

    "return 401 UNAUTHORIZED" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response to have no calculations")
        HipLegacyCalculationListStub.stubGetHipCalculationListUnAuthorized(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)

        Then("A no content response is received")
        result should have(
          httpStatus(UNAUTHORIZED),
          jsonBodyAs[Seq[FailureResponse]](
            Seq(FailureResponse(
              "5009", "Unsuccessful authorisation"
            ))
          )
        )
      }
    }

    "return 400 BAD_REQUEST" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response to have no calculations")
        HipLegacyCalculationListStub.stubGetHipCalculationListBadRequest(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)

        Then("A BAD_REQUEST response is received")
        result should have(
          httpStatus(BAD_REQUEST),
          jsonBodyAs[OriginWithErrorCodeAndResponse](
            OriginWithErrorCodeAndResponse(
              "HIP",
              Seq(FailureResponse("1117", "The tax year provided is invalid"))
            )
          )
        )
      }
    }

    "return 404 NOT_FOUND" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response to have no calculations")
        HipLegacyCalculationListStub.stubGetHipCalculationListNotFound(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)

        Then("A NOT_FOUND response is received")
        result should have(
          httpStatus(NOT_FOUND),
          jsonBodyAs[Seq[FailureResponse]](
            Seq(FailureResponse(
              "5010", "The Requested resource could not be found."
            )
          )
        )
        )
      }
    }

    "return 502 BAD_GATEWAY" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response to have no calculations")
        HipLegacyCalculationListStub.stubGetHipCalculationListBadGateway(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)

        Then("A 502 BAD_GATEWAY is received")
        result should have(
          httpStatus(BAD_GATEWAY),
          jsonBodyMatching(Json.obj("message" -> "BAD_GATEWAY response"))
        )
      }
    }

    "return 500 INTERNAL_SERVER_ERROR" when {
      "user is authorised and sends a valid request" in {
        Given("I am an authorised user")
        isAuthorised(true)

        And("I wiremock stub a 1404 Get List Of Calculation Results (legacy API) response to have no calculations")
        HipLegacyCalculationListStub.stubGetHipCalculationListInternalServerError(testNino, testTaxYearEnd)

        When(s"I call /income-tax-view-change/list-of-calculation-results/$testNino/$testTaxYearEnd")
        val result: WSResponse = IncomeTaxViewChange.getCalculationList(testNino, testTaxYearEnd)

        Then("A 500 INTERNAL_SERVER_ERROR is received")
        result should have(
          httpStatus(INTERNAL_SERVER_ERROR),
          jsonBodyAs[OriginFailuresResponse](
            OriginFailuresResponse(
              "HIP",
              Failures(Seq(Response("9999", "Random error")))
            )
          )
        )
      }
    }
  }
}

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

package connectors

import assets.ITSAStatusIntegrationTestConstants._
import connectors.itsastatus.ITSAStatusConnector
import connectors.itsastatus.OptOutUpdateRequestModel.{OptOutUpdateRequest, OptOutUpdateResponseFailure, OptOutUpdateResponseSuccess}
import helpers.{ComponentSpecBase, WiremockHelper}
import models.itsaStatus.{ITSAStatusResponseError, ITSAStatusResponseNotFound}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.Json

class ITSAStatusConnectorISpec extends ComponentSpecBase {

  val connector: ITSAStatusConnector = app.injector.instanceOf[ITSAStatusConnector]
  val taxableEntityId: String = "AB123456A"
  val taxYear = "19-20"
  val correlationId = "123-456-789"

  val getITSAStatusUrl = s"/income-tax/$taxableEntityId/person-itd/itsa-status/$taxYear?futureYears=true&history=true"
  val updateRequestUrl = s"/income-tax/itsa-status/update/$taxableEntityId"

  val request: OptOutUpdateRequest = OptOutUpdateRequest(taxYear = "19-20", updateReason = "ITSA status update reason")

  "ITSAStatusConnector" when {

    ".getITSAStatus() is called" when {

      "the response is a 200 - OK" should {

        "return an ITSAStatusResponseModel list when successfully retrieved" in {
          val responseBody = successITSAStatusListResponseJson.toString()
          WiremockHelper.stubGet(getITSAStatusUrl, OK, responseBody)
          val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

          result shouldBe Right(listSuccessITSAStatusResponseModel)
        }

        "return an ITSAStatusResponseError when there has been an error attempting to parse the response" in {
          WiremockHelper.stubGet(getITSAStatusUrl, OK, failureInvalidRequestResponse)
          val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

          result shouldBe Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing ITSA Status Response"))
        }
      }

      "the response is a 404 - NotFound" should {

        "return an ITSAStatusResponseNotFound model when no ITSA status was able to be retrieved" in {
          val jsonError = Json.obj("code" -> NOT_FOUND, "reason" -> "The remote endpoint has indicated that no match found for the reference provided.")
          WiremockHelper.stubGet(getITSAStatusUrl, NOT_FOUND,  jsonError.toString())
          val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

          result shouldBe Left(ITSAStatusResponseNotFound(NOT_FOUND, jsonError.toString()))
        }
      }

      "the response is a 500 - InternalServerError" should {

        "return an ITSAStatusResponseError when an unexpected error has occurred while trying to retrieve the ITSA status" in {
          val jsonError = Json.obj("code" -> INTERNAL_SERVER_ERROR, "reason" -> "Server Error, unexpected error has occurred")
          WiremockHelper.stubGet(getITSAStatusUrl, INTERNAL_SERVER_ERROR,  jsonError.toString())
          val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

          result shouldBe Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, jsonError.toString()))
        }
      }
    }

    ".requestOptOutForTaxYear() is called" when {

      "the response is a 204 - NoContent" should {

        "return a OptOutUpdateResponseSuccess response with a correlationId when successful" in {
          val response = OptOutUpdateResponseSuccess(correlationId)
          WiremockHelper.stubPutWithHeaders(updateRequestUrl, NO_CONTENT, Json.toJson(response).toString, Map("correlationId" -> correlationId))
          val result = connector.requestOptOutForTaxYear(taxableEntityId, request).futureValue

          result shouldBe response
        }
      }

      "the response is a 500 - InternalServerError" should {

        "return an OptOutUpdateResponseFailure when there has been an error with the request" in {
          val expectedResponse = Json.toJson(OptOutUpdateResponseFailure.defaultFailure(correlationId)).toString()
          val headers = Map("correlationId" -> correlationId)
          WiremockHelper.stubPutWithHeaders(updateRequestUrl, INTERNAL_SERVER_ERROR, expectedResponse, headers)
          val result = connector.requestOptOutForTaxYear(taxableEntityId, request).futureValue

          result shouldBe OptOutUpdateResponseFailure.defaultFailure(correlationId)
        }
      }
    }
  }
}

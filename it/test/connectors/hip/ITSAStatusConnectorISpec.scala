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

package connectors.hip

import connectors.itsastatus.OptOutUpdateRequestModel._
import constants.ITSAStatusIntegrationTestConstants._
import helpers._
import models.itsaStatus._
import play.api.http.Status._
import play.api.libs.json.Json

class ITSAStatusConnectorISpec extends ComponentSpecBase {

  val connector: ITSAStatusConnector = app.injector.instanceOf[ITSAStatusConnector]
  val taxableEntityId: String = "AB123456A"
  val taxYear = "19-20"
  val correlationId = "123-456-789"

  val getITSAStatusUrl = s"/itsd/person-itd/itsa-status/$taxableEntityId?taxYear=$taxYear&futureYears=true&history=true"
  val updateRequestUrl = s"/itsd/itsa-status/update/$taxableEntityId"


  val request: OptOutUpdateRequest = OptOutUpdateRequest(taxYear = "19-20", updateReason = "ITSA status update reason")

  "ITSAStatusConnector" when {

    ".getITSAStatus() is called" should {
      StatusDetail.statusMapping.foreach { case (statusKey, status) =>
        StatusDetail.statusReasonMapping.foreach { case (statusReasonKey, statusReason) =>
          "return an ITSAStatusResponseModel list" that {
            s"has a status of $status and statusReason of $statusReason" when {
              s"a 200 OK response with status $statusKey and statusReason $statusReasonKey is recieved" in {
                val responseBody = successITSAStatusListHIPResponseJson(statusKey, statusReasonKey).toString()
                WiremockHelper.stubGet(getITSAStatusUrl, OK, responseBody)
                val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

                result shouldBe Right(getSuccessITSAStatusResponseModel(status, statusReason))
              }
            }
          }

        }
      }

      "return an ITSAStatusResponseError when there has been an error attempting to parse the response" in {
        WiremockHelper.stubGet(getITSAStatusUrl, OK, failureInvalidRequestResponse)
        val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

        result shouldBe Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing ITSA Status Response"))
      }

      "return an ITSAStatusResponseNotFound model when no ITSA status was able to be retrieved" when {
        "the response is a 404 - NotFound" in {
          val jsonError = Json.obj("code" -> NOT_FOUND, "reason" -> "The remote endpoint has indicated that no match found for the reference provided.")
          WiremockHelper.stubGet(getITSAStatusUrl, NOT_FOUND, jsonError.toString())
          val result = connector.getITSAStatus(taxableEntityId, taxYear, futureYears = true, history = true).futureValue

          result shouldBe Left(ITSAStatusResponseNotFound(NOT_FOUND, jsonError.toString()))
        }
      }


      "return an ITSAStatusResponseError when an unexpected error has occurred while trying to retrieve the ITSA status" when {
        "the response is a 500 - InternalServerError" in {

          val jsonError = Json.obj("code" -> INTERNAL_SERVER_ERROR, "reason" -> "Server Error, unexpected error has occurred")
          WiremockHelper.stubGet(getITSAStatusUrl, INTERNAL_SERVER_ERROR, jsonError.toString())
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
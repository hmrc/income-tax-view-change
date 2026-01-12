/*
 * Copyright 2023 HM Revenue & Customs
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

import connectors.hip.ITSAStatusConnector
import connectors.itsastatus.OptOutUpdateRequestModel.{ErrorItem, OptOutUpdateRequest, OptOutUpdateResponse, OptOutUpdateResponseFailure, OptOutUpdateResponseSuccess}
import constants.ITSAStatusTestConstants.*
import mocks.MockHttpV2
import models.itsaStatus.{ITSAStatusResponse, ITSAStatusResponseModel}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

import scala.concurrent.Future

class ITSAStatusConnectorSpec extends TestSupport with MockHttpV2 {
  object TestITSAStatusConnector extends ITSAStatusConnector(mockHttpClientV2, microserviceAppConfig)

  "ITSAStatusConnector.getITSAStatus" should {

    import TestITSAStatusConnector._

    lazy val mock = setupMockHttpGetWithHeaderCarrier[HttpResponse](getITSAStatusUrl("", "", futureYears = "true", history = "true"), hipHeaders)(_)

    def getITSAStatusCall: Future[Either[ITSAStatusResponse, List[ITSAStatusResponseModel]]] = getITSAStatus("", "", true, true)

    "return Status (OK) and a JSON body when successful" in {
      mock(successHttpResponse)
      getITSAStatusCall.futureValue shouldBe Right(List(successITSAStatusResponseModel))
    }

    "return ITSAStatusResponseError model in case of failure" in {
      mock(errorHttpResponse)
      getITSAStatusCall.futureValue shouldBe Left(errorITSAStatusError)
    }

    "return ITSAStatusResponseNotFound model in case of failure" in {
      mock(notFoundHttpResponse)
      getITSAStatusCall.futureValue shouldBe Left(errorITSAStatusNotFoundError)
    }

    "return ITSAStatusResponseError model in case of bad JSON" in {
      mock(badJsonHttpResponse)
      getITSAStatusCall.futureValue shouldBe Left(badJsonErrorITSAStatusError)
    }

    "return ITSAStatusResponseError model in case of failed future" in {
      setupMockFailedHttpV2Get(getITSAStatusUrl("", "", futureYears = "true", history = "true"))
      getITSAStatusCall.futureValue shouldBe Left(failedFutureITSAStatusError)
    }
  }

  "ITSAStatusConnector.requestOptOutForTaxYear" should {
    import TestITSAStatusConnector._

    lazy val putMock = setupMockHttpV2PutWithHeaderCarrier[HttpResponse](updateItsaStatusUrl(""))(_)
    def requestOptOutForTaxYearCall: Future[OptOutUpdateResponse] = requestOptOutForTaxYear("", OptOutUpdateRequest("", ""))

    "return Status (NO_CONTENT) and a JSON body when successful" in {
      putMock(putSuccessHttpResponse)
      requestOptOutForTaxYearCall.futureValue shouldBe OptOutUpdateResponseSuccess("test-correlation-id")
    }

    "return OptOutUpdateResponseFailure when the downstream returns a 400" in {
      putMock(putBadRequestHttpResponse)
      requestOptOutForTaxYearCall.futureValue shouldBe OptOutUpdateResponseFailure("test-correlation-id", 400, List(ErrorItem("Type of Failure", "Reason for Failure")))
    }

    "return OptOutUpdateResponseFailure when the downstream returns a 422" in {
      putMock(putUnprocessableEntityHttpResponse)
      requestOptOutForTaxYearCall.futureValue shouldBe OptOutUpdateResponseFailure("test-correlation-id", 422, List(ErrorItem("6001", "string")))
    }

    "return OptOutUpdateResponseFailure when the downstream returns a 500" in {
      putMock(putInternalServerErrorHttpResponse)
      requestOptOutForTaxYearCall.futureValue shouldBe OptOutUpdateResponseFailure("test-correlation-id", 500, List(ErrorItem("string", "string")))
    }

    "return OptOutUpdateResponseFailure when the downstream returns a 503" in {
      putMock(putServiceUnavailableHttpResponse)
      requestOptOutForTaxYearCall.futureValue shouldBe OptOutUpdateResponseFailure("test-correlation-id", 503, List(ErrorItem("string", "string")))
    }

    "return OptOutUpdateResponseFailure for other error scenarios" in {
      val httpResponse = HttpResponse(NOT_FOUND, "", Map("CorrelationId" -> Seq("test-correlation-id")))
      putMock(httpResponse)
      requestOptOutForTaxYearCall.futureValue shouldBe OptOutUpdateResponseFailure("test-correlation-id", INTERNAL_SERVER_ERROR, List(ErrorItem("INTERNAL_SERVER_ERROR", "Unexpected response status: 404")))
    }
  }
}

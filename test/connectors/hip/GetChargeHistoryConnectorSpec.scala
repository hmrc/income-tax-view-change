/*
 * Copyright 2025 HM Revenue & Customs
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

import constants.BaseTestConstants.testNino
import constants.hip.ChargeHistoryTestConstants._
import mocks.MockHttpV2
import models.hip.GetChargeHistoryHipApi
import models.hip.chargeHistory.{ChargeHistoryError, ChargeHistoryNotFound}
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

import scala.concurrent.Future

class GetChargeHistoryConnectorSpec extends TestSupport with MockHttpV2 {

  object TestGetChargeHistoryConnector extends GetChargeHistoryConnector(mockHttpClientV2, microserviceAppConfig)

  import TestGetChargeHistoryConnector._

  val hipPlatform: String = microserviceAppConfig.hipUrl
  val chargeReference: String = "XD000024425799"
  val url5705: String = s"$hipPlatform/etmp/RESTAdapter/ITSA/TaxPayer/GetChargeHistory?idType=NINO&idValue=$testNino&chargeReference=$chargeReference"

  lazy val mock5705: HttpResponse => OngoingStubbing[Future[HttpResponse]] = setupMockHttpGetWithHeaderCarrier(url5705,
    microserviceAppConfig.getHIPHeaders(GetChargeHistoryHipApi))(_)

  "GetChargeHistoryConnector" should {

    "format API URLs correctly" when {
      "getChargeHistoryDetailsUrl is called" in {
        getChargeHistoryDetailsUrl("NINO", testNino, chargeReference) shouldBe url5705
      }
    }

    "return the correct success model when the correct headers are passed" in {
      mock5705(successResponse)
      getHeaders.exists(_._1 == "Authorization") shouldBe true
      getHeaders.exists(_._1 == "correlationId") shouldBe true
      getHeaders.exists(_._1 == "X-Message-Type") shouldBe true
      getHeaders.exists(_._1 == "X-Originating-System") shouldBe true
      getHeaders.exists(_._1 == "X-Receipt-Date") shouldBe true
      getHeaders.exists(_._1 == "X-Regime-Type") shouldBe true
      getHeaders.exists(_._1 == "X-Transmitting-System") shouldBe true
      getChargeHistory(testNino, chargeReference).futureValue shouldBe Right(chargeHistorySuccessWrapperModel)

    }

    "return a ChargeHistorySuccess response model when able to successfully retrieve parse the data" in {
      mock5705(successResponse)
      getChargeHistory(testNino, chargeReference).futureValue shouldBe Right(chargeHistorySuccessWrapperModel)
    }

    "return a ChargeHistoryError response model when data is successfully returned but an error occurred parsing the data" in {
      mock5705(badJsonResponse)
      getChargeHistory(testNino, chargeReference).futureValue shouldBe
        Left(ChargeHistoryError(Status.INTERNAL_SERVER_ERROR, "{}"))
    }

    "return a ChargeHistoryError response model when an error has been returned by the API" in {
      mock5705(badResponse)
      getChargeHistory(testNino, chargeReference).futureValue shouldBe
        Left(ChargeHistoryError(Status.INTERNAL_SERVER_ERROR, "Error message"))
    }

    "return a ChargeHistoryNotFound response when no data was found and returned by the API" in {
      mock5705(notFoundResponse)
      getChargeHistory(testNino, chargeReference).futureValue shouldBe
        Left(ChargeHistoryNotFound(Status.NOT_FOUND, "Error message"))
    }
  }
}

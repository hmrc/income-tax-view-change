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

import constants.ITSAStatusTestConstants._
import connectors.itsastatus.ITSAStatusConnector
import mocks.MockHttpV2
import models.itsaStatus.{ITSAStatusResponse, ITSAStatusResponseModel}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

import scala.concurrent.Future

class ITSAStatusConnectorSpec extends TestSupport with MockHttpV2 {
  object TestITSAStatusConnector extends ITSAStatusConnector(mockHttpClientV2, microserviceAppConfig)

  "ITSAStatusConnector.getITSAStatus" should {

    import TestITSAStatusConnector._

    lazy val mock = setupMockHttpGetWithHeaderCarrier[HttpResponse](getITSAStatusUrl("", "", futureYears = "true", history = "true"), microserviceAppConfig.getIFHeaders("1878"))(_)

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

}

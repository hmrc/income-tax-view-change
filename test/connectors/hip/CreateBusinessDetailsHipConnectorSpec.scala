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

package connectors.hip

import constants.CreateHipBusinessDetailsTestConstants._
import mocks.MockHttpV2
import models.hip.createIncomeSource.CreateIncomeSourceHipRequest
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CreateBusinessDetailsHipConnectorSpec extends TestSupport with MockHttpV2 {

  object TestCreateBusinessDetailsHipConnector extends CreateBusinessDetailsHipConnector(mockHttpClientV2, microserviceAppConfig)

  import TestCreateBusinessDetailsHipConnector._
  "CreateBusinessDetailsConnector.create" should {

    lazy val mock: (CreateIncomeSourceHipRequest, HttpResponse) => Unit = (_, response) =>
      setupMockHttpV2PostWithHeaderCarrier(getUrl)(response)

    "return an IncomeSource model if status (OK) is returned by API with valid json body" in {
      mock(validCreateSelfEmploymentRequest, successHttpResponse)
      getHeaders.exists(_._1 == "Authorization") shouldBe true
      getHeaders.exists(_._1 == "correlationId") shouldBe true
      getHeaders.exists(_._1 == "X-Message-Type") shouldBe true
      getHeaders.exists(_._1 == "X-Originating-System") shouldBe true
      getHeaders.exists(_._1 == "X-Receipt-Date") shouldBe true
      getHeaders.exists(_._1 == "X-Regime") shouldBe true
      getHeaders.exists(_._1 == "X-Transmitting-System") shouldBe true
      create(validCreateSelfEmploymentRequest).futureValue shouldBe Right(successResponse)
    }

    "return CreateBusinessDetailsErrorResponse model if status (OK) is not returned by API" in {
      mock(validCreateSelfEmploymentRequest, failureHttpResponse)
      create(validCreateSelfEmploymentRequest).futureValue shouldBe Left(badRequestFailureResponse)
    }

    "return CreateBusinessDetailsErrorResponse model if future fails to complete" in {
      setupMockHttpV2PostFailed[CreateIncomeSourceHipRequest](getUrl)(validCreateSelfEmploymentRequest)
      create(validCreateSelfEmploymentRequest).futureValue shouldBe Left(internalServerErrorFailureResponse)
    }
  }
}

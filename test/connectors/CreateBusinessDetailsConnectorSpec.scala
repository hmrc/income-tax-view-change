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

import assets.CreateBusinessDetailsTestConstants._
import mocks.MockHttp
import models.createIncomeSource.CreateIncomeSourceRequest
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class CreateBusinessDetailsConnectorSpec extends TestSupport with MockHttp {

  object TestCreateBusinessDetailsConnector extends CreateBusinessDetailsConnector(mockHttpGet, microserviceAppConfig)

  import TestCreateBusinessDetailsConnector._


  "CreateBusinessDetailsConnector.create" should {

    lazy val mock: (CreateIncomeSourceRequest, HttpResponse) => Unit =
      setupMockHttpPostWithHeaderCarrier[CreateIncomeSourceRequest](
        url(testMtdRef), microserviceAppConfig.desAuthHeaders
      )

    "return Status (OK) and a JSON body when successful" in {

      mock(validCreateSelfEmploymentRequest, successHttpResponse)

      create(testMtdRef, validCreateSelfEmploymentRequest)
        .futureValue shouldBe Right(successResponse)
    }

    "return CreateBusinessDetailsErrorResponse model in case of failure" in {
      mock(invalidRequest, failureHttpResponse)
      create(testMtdRef, invalidRequest).futureValue shouldBe Left(failureResponse)
    }

  }
}

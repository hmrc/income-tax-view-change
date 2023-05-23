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

import assets.UpdateIncomeSourceTestConstants._
import mocks.MockHttp
import models.updateIncomeSource.request.UpdateIncomeSourceRequestModel
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class UpdateIncomeSourceConnectorSpec extends TestSupport with MockHttp {

  object TestUpdateIncomeSourceConnector extends UpdateIncomeSourceConnector(mockHttpGet, microserviceAppConfig)

  "UpdateIncomeSourceConnector.updateCessationDate" should {

    import TestUpdateIncomeSourceConnector._

    lazy val mock: (UpdateIncomeSourceRequestModel,HttpResponse) => Unit = setupMockHttpPutWithHeaderCarrier[UpdateIncomeSourceRequestModel](updateIncomeSourceUrl, microserviceAppConfig.ifAuthHeaders)

    "return Status (OK) and a JSON body when successful" in {
      mock(request,successHttpResponse)
      updateIncomeSource(request).futureValue shouldBe successResponse
    }

    "return UpdateIncomeSourceResponseError model in case of failure" in {
      mock(badRequest,badHttpResponse)
      updateIncomeSource(badRequest).futureValue shouldBe errorBadResponse
    }

    "return UpdateIncomeSourceResponseError model in case of bad JSON" in {
      mock(badRequest,successInvalidJsonResponse)
      updateIncomeSource(badRequest).futureValue shouldBe badJsonResponse
    }

    "return UpdateIncomeSourceResponseError model in case of failed future" in {
      setupMockHttpPutFailed(updateIncomeSourceUrl,microserviceAppConfig.ifAuthHeaders)(badRequest)
      updateIncomeSource(badRequest).futureValue shouldBe failureResponse
    }
  }
}

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

package connectors

import mocks.MockHttpV2
import models.claimToAdjustPoa.ClaimToAdjustPoaApiResponse.SuccessResponse
import models.claimToAdjustPoa.ClaimToAdjustPoaResponse.{ClaimToAdjustPoaResponse, ErrorResponse}
import models.claimToAdjustPoa.{ClaimToAdjustPoaRequest, MainIncomeLower}
import play.api.http.Status
import utils.TestSupport

class ClaimToAdjustPoaConnectorSpec extends TestSupport with MockHttpV2 {

  object TestClaimToAdjustPoaConnector extends ClaimToAdjustPoaConnector(microserviceAppConfig, mockHttpClientV2)

  import TestClaimToAdjustPoaConnector._

  "ClaimToAdjustPoaConnector.postClaimToAdjustPoa" should {

    lazy val mock: (ClaimToAdjustPoaRequest, ClaimToAdjustPoaResponse) => Unit = (_, response) =>
      setupMockHttpV2PostWithHeaderCarrier(microserviceAppConfig.ifUrl + "/income-tax/calculations/POA/ClaimToAdjust")(response)

    val request = ClaimToAdjustPoaRequest(
      nino = "AA1111111A",taxYear = "2025", amount = 12.00, poaAdjustmentReason = MainIncomeLower
    )

    "return an ClaimToAdjustPoaResponse model if status (OK) is returned by API with valid json body" in {

      val expectedResult = ClaimToAdjustPoaResponse(status= Status.OK, Right(SuccessResponse("2024-01-31T09:27:17Z"))  )
      mock(request,expectedResult)

       val result: ClaimToAdjustPoaResponse = postClaimToAdjustPoa(request).futureValue
      result shouldBe expectedResult
    }

    "return an Error  model if status (500) is returned by API" in {

      val expectedResult = ClaimToAdjustPoaResponse(status= Status.INTERNAL_SERVER_ERROR, Left(ErrorResponse("error"))  )
      setupMockHttpV2PostFailed(microserviceAppConfig.ifUrl + "/income-tax/calculations/POA/ClaimToAdjust")(request)

      val result: ClaimToAdjustPoaResponse = postClaimToAdjustPoa(request).futureValue
      result shouldBe expectedResult
    }
  }
}
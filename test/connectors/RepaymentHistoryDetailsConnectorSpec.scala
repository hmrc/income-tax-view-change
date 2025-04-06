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

import constants.RepaymentHistoryTestConstants._
import connectors.httpParsers.RepaymentHistoryHttpParser._
import mocks.MockHttpV2
import models.repaymentHistory.{RepaymentHistory, RepaymentHistorySuccessResponse}
import play.api.http.Status.OK
import play.api.libs.json.Json
import utils.TestSupport

class RepaymentHistoryDetailsConnectorSpec extends TestSupport with MockHttpV2 {

  val idType: String = "MTDBSA"
  val idNumber: String = "1234567890"
  val regimeType: String = "ITSA"
  val docNumber: String = "XM0026100121"

  object TestRepaymentHistoryConnector extends RepaymentHistoryDetailsConnector(mockHttpClientV2, microserviceAppConfig)

  val ifPlatform: String = microserviceAppConfig.ifUrl
  val url = s"$ifPlatform/income-tax/self-assessment/repayments-viewer/$nino"
  val urlWithRepaymentId = s"$ifPlatform/income-tax/self-assessment/repayments-viewer/$nino?repaymentRequestNumber=$repaymentId"

  val headers = microserviceAppConfig.getIFHeaders("1771")

  val mock = setupMockHttpGetWithHeaderCarrier[Either[RepaymentHistoryError, RepaymentHistorySuccessResponse]](url, headers)(_)
  val mockWithRepaymentId = setupMockHttpGetWithHeaderCarrier[Either[RepaymentHistoryError, RepaymentHistorySuccessResponse]](urlWithRepaymentId, headers)(_)


  "The repaymentHistoryDetails connector get by Id method" should {
    "return a list of repayments" when {
      s"$OK is received with repayment history " in {

        val repaymentHistoryDetails: List[RepaymentHistory] = List(repaymentHistoryDetail)
        val response = Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails)
        )

        mockWithRepaymentId(response)

        val result = TestRepaymentHistoryConnector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

        result shouldBe Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails))
      }
    }


    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString()))

        mockWithRepaymentId(response)

        val result = TestRepaymentHistoryConnector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

        result shouldBe Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        val response = Left(RepaymentHistoryErrorResponse)

        mockWithRepaymentId(response)

        val result = TestRepaymentHistoryConnector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

        result shouldBe Left(RepaymentHistoryErrorResponse)
      }
    }
  }

  "The repaymentHistoryDetails connector get ALL method" should {
    "return a list of repayments" when {
      s"$OK is received with repayment history " in {

        val repaymentHistoryDetails: List[RepaymentHistory] = List(repaymentHistoryDetail)
        val response = Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails)
        )

        mock(response)

        val result = TestRepaymentHistoryConnector.getAllRepaymentHistoryDetails(nino).futureValue

        result shouldBe Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails))
      }
    }


    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString()))

        mock(response)

        val result = TestRepaymentHistoryConnector.getAllRepaymentHistoryDetails(nino).futureValue

        result shouldBe Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        val response = Left(RepaymentHistoryErrorResponse)

        mock(response)
        val result = TestRepaymentHistoryConnector.getAllRepaymentHistoryDetails(nino).futureValue

        result shouldBe Left(RepaymentHistoryErrorResponse)
      }
    }
  }
}

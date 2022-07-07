/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.RepaymentHistoryTestConstants._
import connectors.httpParsers.RepaymentHistoryHttpParser._
import mocks.MockHttp
import models.repaymentHistory.{RepaymentHistory, RepaymentHistorySuccessResponse}
import play.api.http.Status.OK
import play.api.libs.json.Json
import utils.TestSupport

class RepaymentHistoryDetailsConnectorSpec extends TestSupport with MockHttp {

  val idType: String = "MTDBSA"
  val idNumber: String = "1234567890"
  val regimeType: String = "ITSA"
  val docNumber: String = "XM0026100121"

  object TestRepaymentHistoryConnector extends RepaymentHistoryDetailsConnector(mockHttpGet, microserviceAppConfig)

  "The repaymentHistoryDetails connector get by Id method" should {
    "return a list of repayments" when {
      s"$OK is received with repayment history " in {

        val repaymentHistoryDetails: List[RepaymentHistory] = List(repaymentHistoryDetail)

        mockDesGet(
          url = TestRepaymentHistoryConnector.listRepaymentHistoryDetailsUrl(nino),
          queryParameters = TestRepaymentHistoryConnector.IdQueryParameters(repaymentId = repaymentId),
          headers = microserviceAppConfig.desAuthHeaders
        )(Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails)
        ))

        val result = TestRepaymentHistoryConnector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

        result shouldBe Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails))
      }
    }


    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        mockDesGet[RepaymentHistoryError, RepaymentHistory](
          url = TestRepaymentHistoryConnector.listRepaymentHistoryDetailsUrl(nino),
          queryParameters = TestRepaymentHistoryConnector.IdQueryParameters(repaymentId = repaymentId),
          headers = microserviceAppConfig.desAuthHeaders
        )(Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString())))

        val result = TestRepaymentHistoryConnector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

        result shouldBe Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        mockDesGet[RepaymentHistoryError, RepaymentHistory](
          url = TestRepaymentHistoryConnector.listRepaymentHistoryDetailsUrl(nino),
          queryParameters = TestRepaymentHistoryConnector.IdQueryParameters(repaymentId = repaymentId),
          headers = microserviceAppConfig.desAuthHeaders
        )(Left(RepaymentHistoryErrorResponse))

        val result = TestRepaymentHistoryConnector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

        result shouldBe Left(RepaymentHistoryErrorResponse)
      }
    }
  }

  "The repaymentHistoryDetails connector get by date method" should {
    "return a list of repayments" when {
      s"$OK is received with repayment history " in {

        val repaymentHistoryDetails: List[RepaymentHistory] = List(repaymentHistoryDetail)

        mockDesGet(
          url = TestRepaymentHistoryConnector.listRepaymentHistoryDetailsUrl(nino),
          queryParameters = TestRepaymentHistoryConnector.dateQueryParameters(fromDate = fromDate, toDate = toDate),
          headers = microserviceAppConfig.desAuthHeaders
        )(Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails)
        ))

        val result = TestRepaymentHistoryConnector.getAllRepaymentHistoryDetails(nino, fromDate, toDate).futureValue

        result shouldBe Right(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = repaymentHistoryDetails))
      }
    }


    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        mockDesGet[RepaymentHistoryError, RepaymentHistory](
          url = TestRepaymentHistoryConnector.listRepaymentHistoryDetailsUrl(nino),
          queryParameters = TestRepaymentHistoryConnector.dateQueryParameters(fromDate = fromDate, toDate = toDate),
          headers = microserviceAppConfig.desAuthHeaders
        )(Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString())))

        val result = TestRepaymentHistoryConnector.getAllRepaymentHistoryDetails(nino, fromDate, toDate).futureValue

        result shouldBe Left(UnexpectedRepaymentHistoryResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        mockDesGet[RepaymentHistoryError, RepaymentHistory](
          url = TestRepaymentHistoryConnector.listRepaymentHistoryDetailsUrl(nino),
          queryParameters = TestRepaymentHistoryConnector.dateQueryParameters(fromDate = fromDate, toDate = toDate),
          headers = microserviceAppConfig.desAuthHeaders
        )(Left(RepaymentHistoryErrorResponse))

        val result = TestRepaymentHistoryConnector.getAllRepaymentHistoryDetails(nino, fromDate, toDate).futureValue

        result shouldBe Left(RepaymentHistoryErrorResponse)
      }
    }
  }

}

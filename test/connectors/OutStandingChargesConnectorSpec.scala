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


import constants.OutStandingChargesConstant._
import connectors.httpParsers.OutStandingChargesHttpParser.{OutStandingChargeErrorResponse, UnexpectedOutStandingChargeResponse}
import mocks.MockHttpV2
import models.outStandingCharges.OutstandingChargesSuccessResponse
import play.api.http.Status._
import play.api.libs.json.Json
import utils.TestSupport

class OutStandingChargesConnectorSpec extends TestSupport with MockHttpV2 {

  object TestOutStandingChargesConnector extends OutStandingChargesConnector(mockHttpClientV2, microserviceAppConfig)


  "listOutStandingCharges" should {
    val idType: String = "utr"
    val idNumber = "1234567890"
    val taxYearEndDate: String = "2020-04-05"

    import TestOutStandingChargesConnector.{listOutStandingCharges, listOutStandingChargesUrl}

    val url =  s"${microserviceAppConfig.desUrl}/income-tax/charges/outstanding/$idType/$idNumber/$taxYearEndDate"

    val header = microserviceAppConfig.desAuthHeaders

    val mock = setupMockHttpGetWithHeaderCarrier[Either[Nothing, OutstandingChargesSuccessResponse]](url, header)(_)


    "return a list of out standing charges" when {

      s"$OK is received from ETMP with outstanding charges" in {

        mock(Right(OutstandingChargesSuccessResponse(List(outStandingChargeModelOne, outStandingChargeModelTwo))))

        val result = listOutStandingCharges(idType, idNumber, taxYearEndDate).futureValue

        result shouldBe Right(OutstandingChargesSuccessResponse(List(outStandingChargeModelOne, outStandingChargeModelTwo)))
      }
    }
    "return an error" when {
      s"when $NOT_FOUND is returned" in {
        val responseBody = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.").toString()

        setupMockHttpV2Get(listOutStandingChargesUrl(idType, idNumber, taxYearEndDate))(Left(UnexpectedOutStandingChargeResponse(NOT_FOUND, responseBody)))

        val result = listOutStandingCharges(idType, idNumber, taxYearEndDate).futureValue

        result shouldBe Left(UnexpectedOutStandingChargeResponse(NOT_FOUND, responseBody))
      }
      s"when $INTERNAL_SERVER_ERROR is returned" in {
        setupMockHttpV2Get(listOutStandingChargesUrl(idType, idNumber, taxYearEndDate))(Left(OutStandingChargeErrorResponse))

        val result = listOutStandingCharges(idType, idNumber, taxYearEndDate).futureValue

        result shouldBe Left(OutStandingChargeErrorResponse)
      }
    }
  }
}

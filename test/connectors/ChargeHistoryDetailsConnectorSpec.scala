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

import constants.ChargeHistoryTestConstants.chargeHistoryDetail
import connectors.httpParsers.ChargeHistoryHttpParser._
import mocks.MockHttpV2
import models.chargeHistoryDetail.{ChargeHistoryDetailModel, ChargeHistorySuccessResponse}
import play.api.http.Status.OK
import play.api.libs.json.Json
import utils.TestSupport

class ChargeHistoryDetailsConnectorSpec extends TestSupport with MockHttpV2 {

  val idType: String = "NINO"
  val idNumber: String = "1234567890"
  val regimeType: String = "ITSA"
  val docNumber: String = "XM0026100121"

  val ifPlatform: String = microserviceAppConfig.ifUrl
  val url = s"$ifPlatform/cross-regime/charges/$idType/$idNumber/$regimeType?chargeReference=$docNumber"
  val header: Seq[(String, String)] = microserviceAppConfig.getIFHeaders("1554")

  val mock = setupMockHttpGetWithHeaderCarrier[Either[Nothing, ChargeHistorySuccessResponse]](url, header)(_)

  object TestChargeHistoryConnector extends ChargeHistoryDetailsConnector(mockHttpClientV2, microserviceAppConfig)

  "The chargeHistoryDetails connector" should {
    "return a list of charges" when {
      s"$OK is received with charge history " in {

        val chargeHistoryDetails: List[ChargeHistoryDetailModel] = List(chargeHistoryDetail)
        val response = Right(ChargeHistorySuccessResponse(
          idType = "MTDBSA",
          idValue = "XAIT000000000000",
          regimeType = "ITSA",
          chargeHistoryDetails = Some(chargeHistoryDetails)))

        mock(response)

        val result = TestChargeHistoryConnector.getChargeHistoryDetails(idNumber, docNumber).futureValue

        result shouldBe Right(ChargeHistorySuccessResponse(
          idType = "MTDBSA",
          idValue = "XAIT000000000000",
          regimeType = "ITSA",
          chargeHistoryDetails = Some(chargeHistoryDetails)))
      }
    }


    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        setupMockHttpGetWithHeaderCarrier(url, header)(Left(UnexpectedChargeHistoryResponse(404, errorJson.toString())))
        val result = TestChargeHistoryConnector.getChargeHistoryDetails(idNumber, docNumber).futureValue

        result shouldBe Left(UnexpectedChargeHistoryResponse(404, errorJson.toString()))
      }

      "something went wrong" in {
        setupMockHttpGetWithHeaderCarrier(url, header)(Left(ChargeHistoryErrorResponse))
        val result = TestChargeHistoryConnector.getChargeHistoryDetails(idNumber, docNumber).futureValue

        result shouldBe Left(ChargeHistoryErrorResponse)
      }
    }
  }
}

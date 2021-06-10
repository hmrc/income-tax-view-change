/*
 * Copyright 2021 HM Revenue & Customs
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

import assets.ChargeHistoryTestConstants.chargeHistoryDetail
import connectors.httpParsers.ChargeHistoryHttpParser.{ChargeHistoryError, ChargeHistoryErrorResponse, UnexpectedChargeHistoryResponse}
import mocks.MockHttp
import models.chargeHistoryDetail.{ChargeHistoryDetailModel, ChargeHistorySuccessResponse}
import play.api.http.Status.OK
import play.api.libs.json.Json
import utils.TestSupport

class ChargeHistoryDetailsConnectorSpec extends TestSupport with MockHttp {

	val idType: String = "MTDBSA"
	val idNumber: String = "1234567890"
	val regimeType: String = "ITSA"
	val docNumber: String = "XM0026100121"

	object TestChargeHistoryConnector extends ChargeHistoryDetailsConnector(mockHttpGet, microserviceAppConfig)

	"The chargeHistoryDetails connector" should {
		"return a list of charges" when {
			s"$OK is received with charge history " in {

				val chargeHistoryDetails: List[ChargeHistoryDetailModel] = List(chargeHistoryDetail)

				mockDesGet(
					url = TestChargeHistoryConnector.listChargeHistoryDetailsUrl(idType, idNumber, regimeType),
					queryParameters = TestChargeHistoryConnector.queryParameters(docNumber),
					headers = microserviceAppConfig.desAuthHeaders
				)(Right(ChargeHistorySuccessResponse(
					idType = "MTDBSA",
					idValue = "XAIT000000000000",
					regimeType = "ITSA",
					chargeHistoryDetails = Some(chargeHistoryDetails))))

				val result = await(TestChargeHistoryConnector.getChargeHistoryDetails(idNumber, docNumber))

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
				mockDesGet[ChargeHistoryError, ChargeHistoryDetailModel](
					url = TestChargeHistoryConnector.listChargeHistoryDetailsUrl(idType, idNumber, regimeType),
					queryParameters = TestChargeHistoryConnector.queryParameters(docNumber),
					headers = microserviceAppConfig.desAuthHeaders
				)(Left(UnexpectedChargeHistoryResponse(404, errorJson.toString())))

				val result = await(TestChargeHistoryConnector.getChargeHistoryDetails(idNumber, docNumber))

				result shouldBe Left(UnexpectedChargeHistoryResponse(404, errorJson.toString()))
			}
			"something went wrong" in {
				mockDesGet[ChargeHistoryError, ChargeHistoryDetailModel](
					url = TestChargeHistoryConnector.listChargeHistoryDetailsUrl(idType, idNumber, regimeType),
					queryParameters = TestChargeHistoryConnector.queryParameters(docNumber),
					headers = microserviceAppConfig.desAuthHeaders
				)(Left(ChargeHistoryErrorResponse))

				val result = await(TestChargeHistoryConnector.getChargeHistoryDetails(idNumber, docNumber))

				result shouldBe Left(ChargeHistoryErrorResponse)
			}
		}
	}

}

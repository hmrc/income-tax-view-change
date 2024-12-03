/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.httpParsers.ChargeHistoryHttpParser.{ChargeHistoryErrorResponse, UnexpectedChargeHistoryResponse}
import helpers.{ComponentSpecBase, WiremockHelper}
import models.chargeHistoryDetail.{ChargeHistoryDetailModel, ChargeHistorySuccessResponse}
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

class ChargeHistoryDetailsConnectorISpec extends ComponentSpecBase {

  val connector: ChargeHistoryDetailsConnector = app.injector.instanceOf[ChargeHistoryDetailsConnector]

  val idType: String = "NINO"
  val idNumber: String = "AA123456A"
  val regimeType: String = "ITSA"
  val docNumber: String = "XM0026100121"
  val nino = "AA123456A"
  val url: String = s"/cross-regime/charges/$idType/$idNumber/$regimeType?chargeReference=$docNumber"

  val chargeHistoryDetail: ChargeHistoryDetailModel = ChargeHistoryDetailModel(
    taxYear = "2019",
    documentId = "123456789",
    documentDate = LocalDate.parse("2020-01-29"),
    documentDescription = "Balancing Charge",
    totalAmount = 12345678912.12,
    reversalDate = "2020-02-24",
    reversalReason = "amended return",
    Some("001")
  )

  val chargeHistoryDetails: List[ChargeHistoryDetailModel] = List(chargeHistoryDetail)

  val chargeHistorySuccessResponse: ChargeHistorySuccessResponse = ChargeHistorySuccessResponse(
    idType = "MTDBSA",
    idValue = "XAIT000000000000",
    regimeType = "ITSA",
    chargeHistoryDetails = Some(chargeHistoryDetails)
    )

  val chargeHistorySuccessResponseJson: JsValue = Json.parse(
    """
			|{
			|  "idType" : "MTDBSA",
			|	 "idValue" : "XAIT000000000000",
			|	 "regimeType" : "ITSA",
			|  "chargeHistoryDetails": [
			|  	{
			|  		"taxYear": "2019",
			|  		"documentId": "123456789",
			|  		"documentDate": "2020-01-29",
			|  		"documentDescription": "Balancing Charge",
			|  		"totalAmount": 12345678912.12,
			|     "reversalDate": "2020-02-24",
			|     "reversalReason": "amended return",
			|     "poaAdjustmentReason": "001"
			|   }
			|  ]
			|}
			|""".stripMargin)

  "ChargeHistoryDetailsConnector" when {

    ".getHistoryDetails() is called" when {

      "the response is a 200 - OK" should {

        "return a ChargeHistorySuccessResponse when successful" in {

          WiremockHelper.stubGet(url, OK, chargeHistorySuccessResponseJson.toString())
          val result = connector.getChargeHistoryDetails(nino, docNumber).futureValue

          result shouldBe Right(chargeHistorySuccessResponse)
        }
      }

      "the response is an error" when {

        "the response is a 500 - InternalServerError" should {

          "return a ChargeHistoryErrorResponse when there has been an unexpected error" in {
            WiremockHelper.stubGet(url, INTERNAL_SERVER_ERROR, "{}")
            val result = connector.getChargeHistoryDetails(nino, docNumber).futureValue

            result shouldBe Left(ChargeHistoryErrorResponse)
          }
        }

        "the response is a 404 - NotFound" should {

          "return an UnexpectedChargeHistoryResponse when there has been an error returned" in {
            val jsonError = Json.obj("code" -> 404, "reason" -> "The remote endpoint has indicated that no match found for the reference provided.")

            WiremockHelper.stubGet(url, NOT_FOUND, jsonError.toString())
            val result = connector.getChargeHistoryDetails(nino, docNumber).futureValue

            result shouldBe Left(UnexpectedChargeHistoryResponse(NOT_FOUND, jsonError.toString()))
          }
        }
      }
    }
  }
}

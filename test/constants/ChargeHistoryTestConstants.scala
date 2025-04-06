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

package constants

import models.chargeHistoryDetail.{ChargeHistoryDetailModel, ChargeHistorySuccessResponse}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object ChargeHistoryTestConstants {


  val testValidChargeHistorySuccessResponseJson: JsValue = Json.parse(
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

  val testEmptyValidChargeHistorySuccessResponseJson: JsValue = Json.parse(
    """
			|{
			|  "idType" : "MTDBSA",
			|	 "idValue" : "XAIT000000000000",
			|	 "regimeType" : "ITSA"
			|}
			|""".stripMargin)

  val testInValidChargeHistoryModelJson: JsValue = Json.parse(
    """
			|   {
			|  "chargeHistoryDetails": [ {
			|  "taxYear": "2019",
			|  "documentId": "123456789",
			|  "documentDate": "2020-01-29",
			|  "documentDescription": "Balancing Charge",
			|  "totalAmount": 12345678912.12,
			|    "reversalDate": "2020-02-24"
			|     }
			|    ]
			|  }
			|""".stripMargin)


  val testValidChargeHistoryModelJson: JsValue = Json.parse(
    """
			|{
			|  "taxYear": "2019",
			|  "documentId": "123456789",
			|  "documentDate": "2020-01-29",
			|  "documentDescription": "Balancing Charge",
			|  "totalAmount": 12345678912.12,
			|    "reversalDate": "2020-02-24",
			|    "reversalReason": "amended return",
			|    "poaAdjustmentReason": "001"
			|}
			|
			|""".stripMargin)

  val testValidMultipleChargeHistoryModelJson: JsValue = Json.parse(
    """
			|{
			|	 "idType" : "MTDBSA",
			|	 "idValue" : "XAIT000000000000",
			|	 "regimeType" : "ITSA",
			|  "chargeHistoryDetails": [ {
			|			"taxYear": "2019",
			|			"documentId": "123456789",
			|			"documentDate": "2020-01-29",
			|			"documentDescription": "Balancing Charge",
			|			"totalAmount": 12345678912.12,
			|			"reversalDate": "2020-02-24",
			|			"reversalReason": "amended return",
			|			"poaAdjustmentReason": "001"
			|		},
			|		{
			|			"taxYear": "2018",
			|			"documentId": "123456789",
			|			"documentDate": "2020-01-29",
			|			"documentDescription": "POA1",
			|			"totalAmount": 12345678912.12,
			|			"reversalDate": "2020-02-24",
			|			"reversalReason": "Customer Request"
			|		}]
			|}
			|""".stripMargin)


  val testValidChargeHistoryModel: ChargeHistorySuccessResponse = ChargeHistorySuccessResponse(
    idType = "MTDBSA",
    idValue = "XAIT000000000000",
    regimeType = "ITSA",
    chargeHistoryDetails = Some(List(
      ChargeHistoryDetailModel("  2019 ", "123456789", LocalDate.parse("2020-01-29"), "Balancing Charge", 12345678912.12, "2020-02-24", "amended return", None)
    )))

  val testValidChargeHistoryModel2: ChargeHistorySuccessResponse = ChargeHistorySuccessResponse(
    idType = "MTDBSA",
    idValue = "XAIT000000000000",
    regimeType = "ITSA",
    chargeHistoryDetails = Some(List(
      ChargeHistoryDetailModel("  2021 ", "123456789", LocalDate.parse("2020-01-29"), "Balancing Charge", 123456789012345.67, "2020-02-24", "amended return", None)
    )))


  val singleChargeHistoryDetail: ChargeHistoryDetailModel = ChargeHistoryDetailModel(
    taxYear = "2019",
    documentId = "123456789",
    documentDate = LocalDate.parse("2020-01-29"),
    documentDescription = "Balancing Charge",
    totalAmount = 12345678912.12,
    reversalDate = "2020-02-24",
    reversalReason = "amended return",
    Some("001")
  )

  val singleChargeHistoryDetailTwo: ChargeHistoryDetailModel = ChargeHistoryDetailModel(
    taxYear = "2018",
    documentId = "123456789",
    documentDate = LocalDate.parse("2020-01-29"),
    documentDescription = "POA1",
    totalAmount = 12345678912.12,
    reversalDate = "2020-02-24",
    reversalReason = "Customer Request",
    None
  )

  val SingleChargeHistoryResponseModel: ChargeHistorySuccessResponse = ChargeHistorySuccessResponse(
    idType = "MTDBSA",
    idValue = "XAIT000000000000",
    regimeType = "ITSA",
    chargeHistoryDetails = Some(List(singleChargeHistoryDetail)))

  val MultipleChargeHistoryResponseModel: ChargeHistorySuccessResponse = ChargeHistorySuccessResponse(
    idType = "MTDBSA",
    idValue = "XAIT000000000000",
    regimeType = "ITSA",
    chargeHistoryDetails = Some(List(singleChargeHistoryDetail, singleChargeHistoryDetailTwo)))


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


  val testValidChargeHistoryDetailsModelJson: JsValue = Json.obj(
    "chargesHistory" -> Json.arr(
      Json.obj(
        "documentId" -> "123456789",
        "documentDate" -> "2020-01-29",
        "documentDescription" -> "Balancing Charge",
        "totalAmount" -> 123456789012345.67,
        "reversalDate" -> "2020-02-24",
        "reversalReason" -> "amended return"
      )))

  val testInvalidChargeHistoryDetailsModelJson: JsValue = Json.obj(
    "chargeHistoryDetails" -> Json.arr(
      Json.obj(
        "taxYear" -> "2019",
        "documentId" -> "123456789",
        "documentDate" -> "2020-01-29",
        "documentDescription" -> "Balancing Charge",
        "totalAmount" -> 10.33,
        "reversalDate" -> "2020-02-24"
      )
    )
  )

}

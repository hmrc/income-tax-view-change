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

package connectors.hip

import constants.HipRepaymentHistoryDetailsIntegrationTestConstants.*
import helpers.{ComponentSpecBase, WiremockHelper}
import models.errors.{Error, InvalidJsonResponse, UnexpectedJsonFormat, UnexpectedResponse}
import models.hip.{CustomResponse, ErrorResponse}
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}

class HipRepaymentHistoryDetailsConnectorISpec extends ComponentSpecBase {

  val connector: HipRepaymentHistoryDetailsConnector = app.injector.instanceOf[HipRepaymentHistoryDetailsConnector]
  val idValue = "12345678"

  val urlRepaymentsViewer = s"/etmp/RESTAdapter/ITSA/RepaymentsViewer/$idValue"




  "HipRepaymentHistoryDetailsConnector" when {

    ".getRepaymentHistoryDetailsList() is called" when {

      "the response is a 200 - OK" should {

        "return the Repayments History List list response when successful for a particular ID value" in {

          val requestBody: JsValue = Json.parse(
            """
              |{
              |  "etmp_transaction_header": {
              |    "status": "OK",
              |    "processingDate": "2025-12-17T09:30:17Z"
              |  },
              |  "etmp_Response_Details": {
              |    "repaymentsViewerDetails": [
              |      {
              |        "repaymentRequestNumber": "000000003135",
              |        "actor": "Taxpayer",
              |        "channel": "CESA Return",
              |        "status": "Approved",
              |        "amountRequested": 705.2,
              |        "amountApprovedforRepayment": 705.2,
              |        "totalAmountforRepaymentSupplement": 100.2,
              |        "totalRepaymentAmount": 12345,
              |        "repaymentMethod": "BACS",
              |        "creationDate": "2021-07-21",
              |        "estimatedRepaymentDate": "2021-07-23",
              |        "repaymentItems": [
              |          {
              |            "creditReasons": [
              |              {
              |                "creditReference": "002420002231000",
              |                "creditReason": "Credit",
              |                "receivedDate": "2021-07-23",
              |                "edp": "2021-07-23",
              |                "amount": 700,
              |                "originalChargeReduced": "Original Charge Reduced ",
              |                "amendmentDate": "2021-07-23",
              |                "taxYear": "2020"
              |              }
              |            ],
              |            "repaymentSupplementItem": [
              |              {
              |                "creditReference": "002420002231",
              |                "parentCreditReference": "002420002231",
              |                "amount": 700,
              |                "fromDate": "2021-07-23",
              |                "toDate": "2021-08-23",
              |                "rate": 12.12
              |              }
              |            ]
              |          }
              |        ]
              |      }
              |    ]
              |  }
              |}
              |""".stripMargin)

          WiremockHelper.stubGet(urlRepaymentsViewer, OK, requestBody.toString())

          val result = connector.getRepaymentHistoryDetailsList(idValue).futureValue

          result shouldBe Right(hipRepaymentHistoryList)
        }
        
      }

      "the response is a 500 - InternalServerError" should {

        "return an error when the downstream response was unexpected" in {

          WiremockHelper.stubGet(urlRepaymentsViewer, INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getRepaymentHistoryDetailsList(idValue).futureValue

          result shouldBe Left(ErrorResponse.UnexpectedJsonResponse)
        }
      }

      "the response is a 404 - NOT_FOUND" should {

        "return an error when the downstream response was NOT FOUND" in {

          val json = Json.parse(
            """
              |{
              | "origin":"HoD",
              | "response": {
              |   "failures":[
              |     {
              |       "type": "NOT_FOUND",
              |       "reason":"The remote endpoint has indicated that the requested resource could not be found."
              |     }
              |   ]
              | }
              |}
              |""".stripMargin)

          WiremockHelper.stubGet(urlRepaymentsViewer, NOT_FOUND, json.toString())

          val result = connector.getRepaymentHistoryDetailsList(idValue).futureValue

          result shouldBe Left(ErrorResponse.GenericError(NOT_FOUND, Json.toJson(CustomResponse("Unexpected Unauthorized or Not found error"))))
        }
      }
      "return an error when the downstream response was UNPROCESSABLE_ENTITY" in {

          val json = Json.parse(
            """
              |{
              |  "etmp_transaction_header": {
              |    "status": "NOT_OK",
              |    "statusText": "No Data Found",
              |    "processingDate": "2001-12-17T09:30:47.02Z",
              |    "returnParameters": [
              |      {
              |        "paramName": "ERRORCODE",
              |        "paramValue": "001"
              |      },
              |      {
              |        "paramName": "ERRORTEXT",
              |        "paramValue": "No Data Found "
              |      }
              |    ]
              |  }
              |}
              |""".stripMargin)

          WiremockHelper.stubGet(urlRepaymentsViewer, UNPROCESSABLE_ENTITY, json.toString())

          val result = connector.getRepaymentHistoryDetailsList(idValue).futureValue

          result shouldBe Left(ErrorResponse.GenericError(NOT_FOUND, Json.toJson("")))
        }
      }
    }
}


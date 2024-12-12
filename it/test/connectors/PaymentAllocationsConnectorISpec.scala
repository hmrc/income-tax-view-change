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

import assets.BaseIntegrationTestConstants.testNino
import assets.ReportDeadlinesIntegrationTestConstants._
import helpers.{ComponentSpecBase, WiremockHelper}
import play.api.http.Status._
import play.api.libs.json.Json

class PaymentAllocationsConnectorISpec extends ComponentSpecBase {

  val connector: PaymentAllocationsConnector = app.injector.instanceOf[PaymentAllocationsConnector]

  val paymentAllocationsUrl = s"/cross-regime/payment-allocation/NINO/$testNino/ITSA"

  "PaymentAllocationsConnector" when {

    ".getPaymentAllocations() is called" when {

      "the response is a 200 - OK" should {

        "return a valid PaymentAllocationsResponse model when successfully retrieved" in {

          val responseBody = Json.obj(
            "amount" -> 500,
            "method" -> "method",
            "reference" -> "reference",
            "transactionDate" -> "2022-06-23",
            "allocations" -> Json.arr(
              Json.obj(
                "transactionId" -> "transactionId",
                "from" -> "2022-06-23",
                "to" -> "2022-06-23",
                "chargeType" -> "type",
                "mainType" -> "mainType",
                "amount" -> 1000,
                "clearedAmount" -> 500,
                "chargeReference" -> "chargeReference"
              )
            )
          ).toString()


          WiremockHelper.stubGet(paymentAllocationsUrl, OK, responseBody)
          val result = connector.getPaymentAllocations(testNino, "paymentLot", "paymentLotItem").futureValue

          result shouldBe obligationsModel
        }

//        "return an ObligationsErrorModel when unable to parse the json returned" in {
//
//          val responseBody = testDeadlineFromJson().toString()
//          WiremockHelper.stubGet(getOpenObligationsUrl, OK, responseBody)
//          val result = connector.getOpenObligations(testNino).futureValue
//
//          result shouldBe ObligationsErrorModel(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data")
//        }
//      }
//
//      "the response is a 404 - NotFound" should {
//
//        "return an ObligationsErrorModel when there was no data to be retrieved" in {
//
//          val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
//          WiremockHelper.stubGet(getOpenObligationsUrl, NOT_FOUND, errorJson.toString())
//          val result = connector.getOpenObligations(testNino).futureValue
//
//          result shouldBe ObligationsErrorModel(NOT_FOUND, errorJson.toString())
//        }
//      }
//
//      "the response is a 500 - InternalServerError" should {
//
//        "return an ObligationsErrorModel when there has been an unexpected error" in {
//          val errorJson = Json.obj("code" -> "SERVER_ERROR", "reason" -> "An unexpected error has occurred.")
//          WiremockHelper.stubGet(getOpenObligationsUrl, INTERNAL_SERVER_ERROR, errorJson.toString())
//          val result = connector.getOpenObligations(testNino).futureValue
//
//          result shouldBe ObligationsErrorModel(INTERNAL_SERVER_ERROR, errorJson.toString())
//        }
//      }
//    }
//
//    ".getAllObligationsWithinDateRange() is called" when {
//
//      "the response is a 200 - OK" should {
//
//        "return a valid obligations model when successfully retrieved" in {
//          val responseBody = successResponse(testNino).toString()
//          WiremockHelper.stubGet(getAllObligationsDateRangeUrl, OK, responseBody)
//          val result = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue
//
//          result shouldBe obligationsModel
//        }
//
//        "return an ObligationsErrorModel when unable to parse the json returned" in {
//
//          val responseBody = testDeadlineFromJson().toString()
//          WiremockHelper.stubGet(getAllObligationsDateRangeUrl, OK, responseBody)
//          val result = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue
//
//          result shouldBe ObligationsErrorModel(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data")
//        }
//      }
//
//      "the response is a 404 - NotFound" should {
//
//        "return an ObligationsErrorModel when there was no data to be retrieved" in {
//
//          val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
//          WiremockHelper.stubGet(getAllObligationsDateRangeUrl, NOT_FOUND, errorJson.toString())
//          val result = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue
//
//          result shouldBe ObligationsErrorModel(NOT_FOUND, errorJson.toString())
//        }
//
//        "the response is a 500 - InternalServerError" should {
//
//          "return an ObligationsErrorModel when there has been an unexpected error" in {
//            val errorJson = Json.obj("code" -> "SERVER_ERROR", "reason" -> "An unexpected error has occurred.")
//            WiremockHelper.stubGet(getAllObligationsDateRangeUrl, INTERNAL_SERVER_ERROR, errorJson.toString())
//            val result = connector.getAllObligationsWithinDateRange(testNino, dateFrom, dateTo).futureValue
//
//            result shouldBe ObligationsErrorModel(INTERNAL_SERVER_ERROR, errorJson.toString())
//          }
//        }
      }
    }
  }

}

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

import connectors.httpParsers.ChargeHttpParser.{UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import constants.FinancialDetailHipIntegrationTestConstants._
import helpers.{ComponentSpecBase, WiremockHelper}
import models.financialDetails.hip.model.ChargesHipResponse
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}

class FinancialDetailsHipConnectorISpec extends ComponentSpecBase {

  val connector: FinancialDetailsHipConnector = app.injector.instanceOf[FinancialDetailsHipConnector]
  val baseUrl = "/RESTAdapter/itsa/taxpayer/financial-details"

  val nino = "AA123456A"
  val dateTo = "2019-12-12"
  val dateFrom = "2018-12-12"
  val documentId = "123456789"

  val queryParamsChargeDetails: Seq[(String, String)] = Seq(
    "dateFrom" -> dateFrom,
    "dateTo" -> dateTo,
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "false",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false"
  )

  val queryParamsPaymentAllocation: Seq[(String, String)] = Seq(
    "docNumber" -> documentId,
    "onlyOpenItems" -> "false",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePOA" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false"
  )

  val queryParamsGetOnlyOpenItems: Seq[(String, String)] = Seq(
    "onlyOpenItems" -> "true",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePOA" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false"
  )

  val chargesResponse: ChargesHipResponse = ChargesHipResponse(
    taxpayerDetails = testTaxPayerHipDetails,
    balanceDetails = testBalanceHipDetails,
    documentDetails = List(documentDetailsHip),
    financialDetails = List(financialDetailsHip),
    codingDetails = codingOutList
  )

  val chargesResponseJsonInvalid: JsValue = Json.obj("" -> "")


  val urlGetChargeDetails: String = baseUrl + connector.buildQueryString(queryParamsChargeDetails)
  val urlGetPaymentAllocationDetails: String = baseUrl + connector.buildQueryString(queryParamsPaymentAllocation)
  val urlGetOnlyOpenItems: String = baseUrl + connector.buildQueryString(queryParamsGetOnlyOpenItems)

  "FinancialDetailsConnector" when {

    ".getChargeDetails() is called" when {

      "the response is an Ok - 200" should {

        "return a ChargesResponse when successful" in {

          val responseBody = chargeHipJson
          WiremockHelper.stubGet(urlGetChargeDetails, OK, responseBody.toString())
          val result = connector.getChargeDetails(nino, dateFrom, dateTo).futureValue

          result shouldBe Right(chargesResponse)
        }

        "return an UnexpectedChargeErrorResponse when unable to parse the data into a ChargesResponse" in {

          val responseBody = chargesResponseJsonInvalid
          WiremockHelper.stubGet(urlGetChargeDetails, OK, responseBody.toString())
          val result = connector.getChargeDetails(nino, dateFrom, dateTo).futureValue

          result shouldBe Left(UnexpectedChargeErrorResponse)

        }
      }

      "the response is a 404 - NotFound when no data can be found" should {

        "return an UnexpectedChargeResponse error response when no data is found" in {
          val jsonError = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
          WiremockHelper.stubGet(urlGetChargeDetails, NOT_FOUND, jsonError.toString())
          val result = connector.getChargeDetails(nino, dateFrom, dateTo).futureValue

          result shouldBe Left(UnexpectedChargeResponse(NOT_FOUND, jsonError.toString()))
        }
      }

      "return a 500 - InternalServerError when there has been an issue retrieving the data" should {

        "return an UnexpectedChargeResponse when there's been an unexpected error" in {

          WiremockHelper.stubGet(urlGetChargeDetails, INTERNAL_SERVER_ERROR, "{}")
          val result = connector.getChargeDetails(nino, dateFrom, dateTo).futureValue

          result shouldBe Left(UnexpectedChargeErrorResponse)
        }
      }
    }

//    ".getPaymentAllocation() is called" when {
//
//      "the response is a 200 - OK" should {
//
//        "return a ChargeResponse when successful" in {
//
//          val responseBody = chargeJson
//          WiremockHelper.stubGet(urlGetPaymentAllocationDetails, OK, responseBody.toString())
//          val result = connector.getPaymentAllocationDetails(nino, documentId).futureValue
//
//          result shouldBe Right(chargesResponse)
//        }
//
//        "return an UnexpectedChargeErrorResponse when unable to parse the date into a ChargesResponse" in {
//
//          val responseBody = chargesResponseJsonInvalid
//          WiremockHelper.stubGet(urlGetPaymentAllocationDetails, OK, responseBody.toString())
//          val result = connector.getPaymentAllocationDetails(nino, documentId).futureValue
//
//          result shouldBe Left(UnexpectedChargeErrorResponse)
//        }
//      }
//
//      "the response is a 404 - NotFound" should {
//
//        "return an UnexpectedChargeResponse error response when no data is found" in {
//          val jsonError = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
//          WiremockHelper.stubGet(urlGetPaymentAllocationDetails, NOT_FOUND, jsonError.toString())
//          val result = connector.getPaymentAllocationDetails(nino, documentId).futureValue
//
//          result shouldBe Left(UnexpectedChargeResponse(NOT_FOUND, jsonError.toString()))
//        }
//      }
//
//      "the response is a 500 - InternalServerError when there has been an issue retrieving the data" should {
//
//        "return an UnexpectedChargeResponse when there's been an unexpected error" in {
//
//          WiremockHelper.stubGet(urlGetPaymentAllocationDetails, INTERNAL_SERVER_ERROR, "{}")
//          val result = connector.getPaymentAllocationDetails(nino, documentId).futureValue
//
//          result shouldBe Left(UnexpectedChargeErrorResponse)
//        }
//      }
//    }
//
//    ".getOnlyOpenItems() is called" when {
//
//      "the response is an Ok - 200" should {
//
//        "return a ChargesResponse when successful" in {
//
//          val responseBody = chargeJson
//          WiremockHelper.stubGet(urlGetOnlyOpenItems, OK, responseBody.toString())
//          val result = connector.getOnlyOpenItems(nino).futureValue
//
//          result shouldBe Right(chargesResponse)
//        }
//
//        "return an UnexpectedChargeErrorResponse when unable to parse the data into a ChargesResponse" in {
//
//          val responseBody = chargesResponseJsonInvalid
//          WiremockHelper.stubGet(urlGetOnlyOpenItems, OK, responseBody.toString())
//          val result = connector.getOnlyOpenItems(nino).futureValue
//
//          result shouldBe Left(UnexpectedChargeErrorResponse)
//
//        }
//      }
//
//      "the response is a 404 - NotFound when no data can be found" should {
//
//        "return an UnexpectedChargeResponse error response when no data is found" in {
//          val jsonError = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
//          WiremockHelper.stubGet(urlGetOnlyOpenItems, NOT_FOUND, jsonError.toString())
//          val result = connector.getOnlyOpenItems(nino).futureValue
//
//          result shouldBe Left(UnexpectedChargeResponse(NOT_FOUND, jsonError.toString()))
//        }
//      }
//
//      "return a 500 - InternalServerError when there has been an issue retrieving the data" should {
//
//        "return an UnexpectedChargeResponse when there's been an unexpected error" in {
//
//          WiremockHelper.stubGet(urlGetOnlyOpenItems, INTERNAL_SERVER_ERROR, "{}")
//          val result = connector.getOnlyOpenItems(nino).futureValue
//
//          result shouldBe Left(UnexpectedChargeErrorResponse)
//        }
//      }
//    }
  }
}

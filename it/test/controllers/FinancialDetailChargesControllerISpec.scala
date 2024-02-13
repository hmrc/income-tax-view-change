/*
 * Copyright 2017 HM Revenue & Customs
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

package test.controllers

import test.assets.BaseIntegrationTestConstants._
import test.assets.FinancialDetailIntegrationTestConstants._
import models.financialDetails.responses.ChargesResponse
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import test.helpers.ComponentSpecBase
import test.helpers.servicemocks.DesChargesStub._

class FinancialDetailChargesControllerISpec extends ComponentSpecBase {

  val from: String = "from"
  val to: String = "to"
  val documentId: String = "123456789"

  s"GET ${controllers.routes.FinancialDetailChargesController.getChargeDetails(testNino, from, to)}" should {
    s"return $OK" when {
      "charge details are successfully retrieved" in {

        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = OK,
          response = chargeJson)

        val res: WSResponse = IncomeTaxViewChange.getChargeDetails(testNino, from, to)

        val expectedResponseBody: JsValue = Json.toJson(ChargesResponse(
          balanceDetails = balanceDetails,
          documentDetails = List(documentDetail, documentDetail2),
          financialDetails = List(financialDetail, financialDetail2)
        ))

        res should have(
          httpStatus(OK),
          jsonBodyMatching(expectedResponseBody)
        )
      }
    }

    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving charge details" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubGetChargeDetails(testNino, from, to)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getChargeDetails(testNino, from, to)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "API responds with status OK but a non-json body" in {
        isAuthorised(true)
        stubGetChargeDetails(testNino, from, to,
          status = OK,
          responseBody = "")

        val res: WSResponse = IncomeTaxViewChange.getChargeDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }

      "an unexpected status was returned when retrieving charge details" in {

        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getChargeDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

  s"GET ${controllers.routes.FinancialDetailChargesController.getPaymentAllocationDetails(testNino, documentId)}" should {
    s"return $OK" when {
      "new charge details are successfully retrieved" in {

        isAuthorised(true)

        stubGetSingleDocumentDetails(testNino, documentId)(
          status = OK,
          response = chargeJson)

        val res: WSResponse = IncomeTaxViewChange.getPaymentAllocationDetails(testNino, documentId)

        val expectedResponseBody: JsValue = Json.toJson(ChargesResponse(
          balanceDetails = balanceDetails,
          documentDetails = List(documentDetail, documentDetail2),
          financialDetails = List(financialDetail, financialDetail2)
        ))

        res should have(
          httpStatus(OK),
          jsonBodyMatching(expectedResponseBody)
        )
      }
    }

    s"return $NOT_FOUND" when {
      "new an unexpected status with NOT_FOUND was returned when retrieving charge details" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubGetSingleDocumentDetails(testNino, documentId)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getPaymentAllocationDetails(testNino, documentId)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "API responds with status OK but a non-json body" in {
        isAuthorised(true)
        stubGetChargeDetails(testNino, from, to,
          status = OK,
          responseBody = "rubbish")

        val res: WSResponse = IncomeTaxViewChange.getChargeDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }

      "an unexpected status was returned when retrieving charge details" in {

        isAuthorised(true)

        stubGetSingleDocumentDetails(testNino, documentId)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getPaymentAllocationDetails(testNino, documentId)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }
}

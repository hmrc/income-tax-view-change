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

package controllers

import assets.BaseIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesChargesStub._
import models.credits.CreditsModel
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import utils.AChargesResponse

import java.time.LocalDate

class FinancialDetailCreditsControllerISpec extends ComponentSpecBase {

  val apiResponseJson = """{
               |  "balanceDetails" : {
               |    "balanceDueWithin30Days" : 100,
               |    "overDueAmount" : 0,
               |    "totalBalance" : 0,
               |    "availableCredit" : 200,
               |    "allocatedCredit" : 100,
               |    "unallocatedCredit" : 400,
               |    "firstPendingAmountRequested" : 200,
               |    "secondPendingAmountRequested" : 100
               |  },
               |  "documentDetails" : [ {
               |    "taxYear" : "2024",
               |    "documentId" : "CUTOVER01",
               |    "totalAmount" : -1000,
               |    "documentOutstandingAmount" : -100,
               |    "documentDate" : "2024-06-20",
               |    "documentDueDate" : "2024-06-20"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "BALANCING01",
               |    "totalAmount" : -1000,
               |    "documentOutstandingAmount" : -200,
               |    "documentDate" : "2024-06-19",
               |    "documentDueDate" : "2024-06-19"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "MFA01",
               |    "totalAmount" : -1000,
               |    "documentOutstandingAmount" : -300,
               |    "documentDate" : "2024-06-18",
               |    "documentDueDate" : "2024-06-18"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "PAYMENT01",
               |    "totalAmount" : -1000,
               |    "documentOutstandingAmount" : -400,
               |    "documentDate" : "2024-06-17",
               |    "documentDueDate" : "2024-06-17"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "INTEREST01",
               |    "totalAmount" : -1000,
               |    "documentOutstandingAmount" : -500,
               |    "documentDate" : "2024-06-16",
               |    "documentDueDate" : "2024-06-16"
               |  } ],
               |  "financialDetails" : [ {
               |    "taxYear" : "2024",
               |    "documentId" : "CUTOVER01",
               |    "documentDate" : "2024-06-20",
               |    "originalAmount" : -1000,
               |    "documentOutstandingAmount" : -100,
               |    "mainType" : "ITSA Cutover Credits",
               |    "mainTransaction" : "6110"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "BALANCING01",
               |    "documentDate" : "2024-06-19",
               |    "originalAmount" : -1000,
               |    "documentOutstandingAmount" : -200,
               |    "mainType" : "SA Balancing Charge Credit",
               |    "mainTransaction" : "4905"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "MFA01",
               |    "documentDate" : "2024-06-18",
               |    "originalAmount" : -1000,
               |    "documentOutstandingAmount" : -300,
               |    "mainType" : "ITSA Overpayment Relief",
               |    "mainTransaction" : "4004"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "PAYMENT01",
               |    "documentDate" : "2024-06-17",
               |    "originalAmount" : -1000,
               |    "documentOutstandingAmount" : -400,
               |    "mainType" : "Payment",
               |    "mainTransaction" : "0060"
               |  }, {
               |    "taxYear" : "2024",
               |    "documentId" : "INTEREST01",
               |    "documentDate" : "2024-06-16",
               |    "originalAmount" : -1000,
               |    "documentOutstandingAmount" : -500,
               |    "mainType" : "SA Repayment Supplement Credit",
               |    "mainTransaction" : "6020"
               |  } ]
               |}""".stripMargin

  val from: String = "from"
  val to: String = "to"
  val documentId: String = "123456789"

  s"GET ${controllers.routes.FinancialDetailCreditsController.getCredits(testNino, from, to)}" should {
    s"return $OK" when {
      "charge details are successfully retrieved" in {

        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = OK,
          response = Json.parse(apiResponseJson))

        val res: WSResponse = IncomeTaxViewChange.getCreditDetails(testNino, from, to)

        val expectedResponse = CreditsModel.fromChargesResponse(AChargesResponse()
          .withAvailableCredit(200.0)
          .withUnallocatedCredit(400.0)
          .withBalanceDueWithin30Days(100.0)
          .withFirstRefundRequest(200.0)
          .withSecondRefundRequest(100.0)
          .withCutoverCredit("CUTOVER01", LocalDate.of(2024, 6, 20), -100.0)
          .withBalancingChargeCredit("BALANCING01", LocalDate.of(2024, 6, 19), -200)
          .withMfaCredit("MFA01", LocalDate.of(2024, 6, 18), -300)
          .withPayment("PAYMENT01", LocalDate.of(2024, 6, 17), -400)
          .withRepaymentInterest("INTEREST01", LocalDate.of(2024, 6, 16), -500)
          .get())

        val expectedResponseBody: JsValue = Json.toJson(expectedResponse)

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

        val res: WSResponse = IncomeTaxViewChange.getCreditDetails(testNino, from, to)

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

        val res: WSResponse = IncomeTaxViewChange.getCreditDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }

      "an unexpected status was returned when retrieving charge details" in {

        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getCreditDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }
}

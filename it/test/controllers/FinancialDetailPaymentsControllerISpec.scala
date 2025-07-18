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

package controllers

import constants.BaseIntegrationTestConstants._
import models.financialDetails.Payment
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSResponse
import helpers.ComponentSpecBase
import helpers.servicemocks.DesChargesStub.stubGetChargeDetails


import java.time.LocalDate

class FinancialDetailPaymentsControllerISpec extends ComponentSpecBase {

  val from: String = "from"
  val to: String = "to"

  val payments1: Payment = Payment(
    reference = Some("paymentReference"),
    amount = BigDecimal("-1000.00"),
    outstandingAmount = BigDecimal("100"),
    documentDescription = Some("documentDescription"),
    method = Some("paymentMethod"),
    lot = None,
    lotItem = None,
    dueDate = Some(LocalDate.parse("2018-03-29")),
    documentDate = LocalDate.parse("2018-03-29"),
    transactionId = "id",
    mainType = None,
    mainTransaction = None
  )

  val payments2: Payment = Payment(
    reference = Some("paymentReference2"),
    amount = BigDecimal("-1000.00"),
    outstandingAmount = BigDecimal("100"),
    method = Some("paymentMethod2"),
    documentDescription =  Some("documentDescription2"),
    lot = None,
    lotItem = None,
    dueDate = Some(LocalDate.parse("2018-03-29")),
    documentDate = LocalDate.parse("2018-03-29"),
    transactionId = "id2",
    mainType = None,
    mainTransaction = None
  )

  val chargeJson: JsObject = Json.obj(
    "success" -> Json.obj(
      "taxpayerDetails" -> Json.obj(
        "idType" -> "NINO",
        "idNumber" -> "BB123456A",
        "regimeType" -> "ITSA"
      ),
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30days" -> 100.00,
      "balanceNotDuein30Days" -> 0,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00
    ),
      "codingDetails" -> Json.arr(
        Json.obj(
          "totalLiabilityAmount" -> 2300.00,
          "taxYearReturn" -> "2020"
        )
      ),
    "documentDetails" -> Json.arr(
      Json.obj(
        "taxYear" -> "2018",
        "documentID" -> "id",
        "documentDescription" -> "documentDescription",
        "documentText" -> "documentText",
        "totalAmount" -> -1000.00,
        "documentOutstandingAmount" -> 100.00,
        "documentDate" -> "2018-03-29",
        "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29")
      ),
      Json.obj(
        "taxYear" -> "2019",
        "documentID" -> "id2",
        "documentDescription" -> "documentDescription2",
        "documentText" -> "documentText2",
        "totalAmount" -> -1000.00,
        "documentOutstandingAmount" -> 100.00,
        "documentDate" -> "2018-03-29",
        "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29")
      )
    ),
    "financialDetailsItem" -> Json.arr(
      Json.obj(
        "taxYear" -> "2018",
        "documentID" -> "id",
        "documentDate" -> "2022-06-23",
        "documentDescription" -> "type",
        "originalAmount" -> -1000.00,
        "totalAmount" -> -1000.00,
        "originalAmount" -> -1000.00,
        "outstandingAmount" -> 0.00,
        "items" -> Json.arr(
          Json.obj(
            "subItem" -> "1",
            "amount" -> -1000.00,
            "clearingDate" -> "2022-06-23",
            "clearingReason" -> "clearingReason",
            "outgoingPaymentMethod" -> "outgoingPaymentMethod",
            "paymentReference" -> "paymentReference",
            "paymentAmount" -> -1000.00,
            "dueDate" -> "2022-06-23",
            "paymentMethod" -> "paymentMethod"
          )
        )
      ),
      Json.obj(
        "taxYear" -> "2019",
        "documentID" -> "id2",
        "documentDate" -> "2022-06-23",
        "documentDescription" -> "type2",
        "totalAmount" -> -1000.00,
        "originalAmount" -> -1000.00,
        "outstandingAmount" -> 0.00,
        "items" -> Json.arr(
          Json.obj(
            "subItem" -> "2",
            "amount" -> -1000.00,
            "clearingDate" -> "2022-06-23",
            "clearingReason" -> "clearingReason2",
            "outgoingPaymentMethod" -> "outgoingPaymentMethod2",
            "paymentReference" -> "paymentReference2",
            "paymentAmount" -> -1000.00,
            "dueDate" -> "2022-06-23",
            "paymentMethod" -> "paymentMethod2"
          )
        )
      )
    )
  )
  )

  s"GET ${controllers.routes.FinancialDetailPaymentsController.getPaymentDetails(testNino, from, to)}" should {
    s"return $OK" when {
      "payment details are successfully retrieved" in {
        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = OK,
          response = chargeJson)

        val res: WSResponse = IncomeTaxViewChange.getPaymentDetails(testNino, from, to)

        res should have(
          httpStatus(OK),
          jsonBodyMatching(Json.toJson(List(payments1, payments2)))
        )
      }
    }

    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving payment details" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubGetChargeDetails(testNino, from, to)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getPaymentDetails(testNino, from, to)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving payment details" in {

        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getPaymentDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

}

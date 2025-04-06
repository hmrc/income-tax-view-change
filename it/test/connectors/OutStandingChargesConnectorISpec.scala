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

import connectors.httpParsers.OutStandingChargesHttpParser.{OutStandingChargeErrorResponse, UnexpectedOutStandingChargeResponse}
import helpers.{ComponentSpecBase, WiremockHelper}
import models.outStandingCharges.{OutStandingCharge, OutstandingChargesSuccessResponse}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}


class OutStandingChargesConnectorISpec extends ComponentSpecBase {

  lazy val connector: OutStandingChargesConnector = app.injector.instanceOf[OutStandingChargesConnector]
  val idType = "idType"
  val idNumber = "idNumber"
  val taxYearEndDate = "taxYearEndDate"
  val url = s"/income-tax/charges/outstanding/$idType/$idNumber/$taxYearEndDate"

  "OutstandingChargesConnector" when {

    ".listOutStandingCharges() is called" when {

      s"the response is a $OK" should {

        "return the outstanding charges for a given idType, idNumber and taxYearEndDate" in {

          lazy val requestBody: JsValue = Json.parse(
            s"""
               |[
               | {
               |   "chargeName": "LATE",
               |   "relevantDueDate": "2021-01-31",
               |   "chargeAmount": 123456.78,
               |   "tieBreaker": 1234
               | }
               |]
               |""".stripMargin)

          WiremockHelper.stubGet(url, OK, requestBody.toString())

          val result = connector.listOutStandingCharges(idType, idNumber, taxYearEndDate).futureValue

          val expected = Right(OutstandingChargesSuccessResponse(List(OutStandingCharge("LATE", Some("2021-01-31"), 123456.78, 1234))))

          result shouldBe expected
        }
      }

      s"the response is a $NOT_FOUND" should {

        "return an Error Response Model" in {

          val responseError = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.").toString()

          WiremockHelper.stubGet(url, NOT_FOUND, responseError)

          val result = connector.listOutStandingCharges(idType, idNumber, taxYearEndDate).futureValue

          val expected = Left(UnexpectedOutStandingChargeResponse(NOT_FOUND, responseError))

          result shouldBe expected
        }
      }

      s"when the response is $INTERNAL_SERVER_ERROR" should {

        "return an Error Response Model" in {

          val responseError = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.").toString()

          WiremockHelper.stubGet(url, INTERNAL_SERVER_ERROR, responseError)

          val result = connector.listOutStandingCharges(idType, idNumber, taxYearEndDate).futureValue

          val expected = Left(OutStandingChargeErrorResponse)

          result shouldBe expected
        }
      }
    }
  }
}

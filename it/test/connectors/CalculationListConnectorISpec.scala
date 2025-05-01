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

import helpers.{ComponentSpecBase, WiremockHelper}
import models.calculationList.{CalculationListModel, CalculationListResponseModel}
import models.errors.{InvalidJsonResponse, UnexpectedJsonFormat, UnexpectedResponse}
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

class CalculationListConnectorISpec extends ComponentSpecBase {

  val connector: CalculationListConnector = app.injector.instanceOf[CalculationListConnector]
  val nino = "AA123456A"
  val taxYear = "2023"
  val taxYearRange = "23-24"
  val urlCalculation = s"/income-tax/list-of-calculation-results/$nino?taxYear=$taxYear"
  val urlCalculationTYS = s"/income-tax/view/calculations/liability/$taxYearRange/$nino"
  val urlCalculation2083 = s"/income-tax/$taxYearRange/view/$nino/calculations-summary"

  val calculationListResponse: CalculationListResponseModel = CalculationListResponseModel(Seq(CalculationListModel(
    calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
    calculationTimestamp = "2023-10-31T12:55:51.159Z",
    calculationType = "finalDeclaration",
    crystallised = Some(false)
  )))



  "CalculationListConnector" when {

    ".getCalculationList() is called" when {

      "the response is a 200 - Ok" should {

        "return the correct calculation list for a given NINO and tax year" in {

          val requestBody: JsValue = Json.parse(
            """
              |[
              | {
              |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
              |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
              |   "calculationType":"finalDeclaration",
              |   "crystallised": false
              | }
              |]
              |""".stripMargin)

          WiremockHelper.stubGet(urlCalculation, OK, requestBody.toString())

          val result = connector.getCalculationList(nino, taxYear).futureValue

          result shouldBe Right(calculationListResponse)
        }

        "return an UnexpectedJsonFormat error when the json returned is in an unexpected format" in {

          WiremockHelper.stubGet(urlCalculation, OK, "{}")

          val result = connector.getCalculationList(nino, taxYear).futureValue

          result shouldBe Left(UnexpectedJsonFormat)
        }
      }

      "the response is a 500 - InternalServerError" should {

        "return an error response when an unexpected error was received" in {

          WiremockHelper.stubGet(urlCalculation, INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getCalculationList(nino, taxYear).futureValue

          result shouldBe Left(UnexpectedResponse)
        }
      }
    }

    ".getCalculationListTYS() is called" when {

      "the response is a 200 - OK" should {

        "return the calculation list response when successful for a given NINO and tax year" in {

          val requestBody: JsValue = Json.parse(
            """
              |[
              | {
              |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
              |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
              |   "calculationType":"finalDeclaration",
              |   "crystallised": false
              | }
              |]
              |""".stripMargin)

          WiremockHelper.stubGet(urlCalculationTYS, OK, requestBody.toString())

          val result = connector.getCalculationListTYS(nino, taxYearRange).futureValue

          result shouldBe Right(calculationListResponse)
        }

        "return an error when the request returned has errors" in {
          val requestBody: JsValue = Json.parse(
            """
              |[
              | {
              |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
              |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
              |   "crystallised": false
              | }
              |]
              |""".stripMargin)

          WiremockHelper.stubGet(urlCalculationTYS, OK, requestBody.toString())

          val result = connector.getCalculationListTYS(nino, taxYearRange).futureValue

          result shouldBe Left(UnexpectedJsonFormat)
        }
      }

      "the response is a 500 - InternalServerError" should {

        "return an error when the downstream response was unexpected" in {

          WiremockHelper.stubGet(urlCalculationTYS, INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getCalculationListTYS(nino, taxYearRange).futureValue

          result shouldBe Left(UnexpectedResponse)
        }

        "return an error when the json returned was in an incorrect state" in {

          val requestBody: JsValue = Json.parse(
            """
              |[
              | {
              |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
              |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
              |   "crystallised": false
              | }
              |]
              |""".stripMargin)

          WiremockHelper.stubGet(urlCalculationTYS, INTERNAL_SERVER_ERROR, requestBody.toString())

          val result = connector.getCalculationListTYS("1234", taxYearRange).futureValue

          result shouldBe Left(InvalidJsonResponse)
        }
      }
    }
  }

  ".getCalculationList2083() is called" when {

    "the response is a 200 - OK" should {

      "return the converted calculation list response" that {
        "has calculationType crystallisation and crystallised true" when {
          "successful for a given NINO and tax year and original calculation type is DF" in {

            val requestBody: JsValue = Json.obj(
              "calculationsSummary" -> Json.parse(
                """
                  |[
                  | {
                  |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
                  |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
                  |   "calculationType":"DF"
                  | }
                  |]
                  |""".stripMargin)
            )

            val expectedResponse: CalculationListResponseModel = CalculationListResponseModel(Seq(CalculationListModel(
              calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
              calculationTimestamp = "2023-10-31T12:55:51.159Z",
              calculationType = "crystallisation",
              crystallised = Some(true)
            )))

            WiremockHelper.stubGet(urlCalculation2083, OK, requestBody.toString())

            val result = connector.getCalculationList2083(nino, taxYearRange).futureValue

            result shouldBe Right(expectedResponse)
          }
        }

        "has calculationType inYear and crystallised None" when {
          "successful for a given NINO and tax year and original calculation type is IY" in {

            val requestBody: JsValue = Json.obj(
              "calculationsSummary" -> Json.parse(
                """
                  |[
                  | {
                  |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
                  |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
                  |   "calculationType":"IY"
                  | }
                  |]
                  |""".stripMargin)
            )

            val expectedResponse: CalculationListResponseModel = CalculationListResponseModel(Seq(CalculationListModel(
              calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
              calculationTimestamp = "2023-10-31T12:55:51.159Z",
              calculationType = "inYear",
              crystallised = None
            )))

            WiremockHelper.stubGet(urlCalculation2083, OK, requestBody.toString())

            val result = connector.getCalculationList2083(nino, taxYearRange).futureValue

            result shouldBe Right(expectedResponse)
          }
        }
      }

      "return an error when the request returned has errors" in {
        val requestBody: JsValue = Json.parse(
          """
            |[
            | {
            |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
            |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
            |   "crystallised": false
            | }
            |]
            |""".stripMargin)

        WiremockHelper.stubGet(urlCalculation2083, OK, requestBody.toString())

        val result = connector.getCalculationList2083(nino, taxYearRange).futureValue

        result shouldBe Left(UnexpectedJsonFormat)
      }
    }

    "the response is a 500 - InternalServerError" should {

      "return an error when the downstream response was unexpected" in {

        WiremockHelper.stubGet(urlCalculation2083, INTERNAL_SERVER_ERROR, "{}")

        val result = connector.getCalculationList2083(nino, taxYearRange).futureValue

        result shouldBe Left(UnexpectedResponse)
      }

      "return an error when the json returned was in an incorrect state" in {

        val requestBody: JsValue = Json.parse(
          """
            |[
            | {
            |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
            |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
            |   "crystallised": false
            | }
            |]
            |""".stripMargin)

        WiremockHelper.stubGet(urlCalculation2083, INTERNAL_SERVER_ERROR, requestBody.toString())

        val result = connector.getCalculationList2083("1234", taxYearRange).futureValue

        result shouldBe Left(InvalidJsonResponse)
      }
    }
  }
}


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

import helpers.{ComponentSpecBase, WiremockHelper}
import models.calculationList.{CalculationListModel, CalculationListResponseModel}
import models.errors.{InvalidJsonResponse, UnexpectedJsonFormat, UnexpectedResponse}
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

class CalculationListHipConnectorISpec extends ComponentSpecBase {

  val connector: CalculationListHipConnector = app.injector.instanceOf[CalculationListHipConnector]
  val nino = "AA123456A"
  val taxYearRange = "23-24"
  val urlCalculationHipTYS = s"/itsa/income-tax/v1/$taxYearRange/view/calculations/liability/$nino"

  val calculationListResponse: CalculationListResponseModel = CalculationListResponseModel(Seq(CalculationListModel(
    calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
    calculationTimestamp = "2023-10-31T12:55:51.159Z",
    calculationType = "finalDeclaration",
    crystallised = Some(false)
  )))



  "CalculationListConnector" when {

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

          WiremockHelper.stubGet(urlCalculationHipTYS, OK, requestBody.toString())

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

          WiremockHelper.stubGet(urlCalculationHipTYS, OK, requestBody.toString())

          val result = connector.getCalculationListTYS(nino, taxYearRange).futureValue

          result shouldBe Left(UnexpectedJsonFormat)
        }
      }

      "the response is a 500 - InternalServerError" should {

        "return an error when the downstream response was unexpected" in {

          WiremockHelper.stubGet(urlCalculationHipTYS, INTERNAL_SERVER_ERROR, "{}")

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

          WiremockHelper.stubGet(urlCalculationHipTYS, INTERNAL_SERVER_ERROR, requestBody.toString())

          val result = connector.getCalculationListTYS("1234", taxYearRange).futureValue

          result shouldBe Left(InvalidJsonResponse)
        }
      }
    }
  }
}


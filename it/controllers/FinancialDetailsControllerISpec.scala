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
import assets.FinancialDetailIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesChargesStub._
import models.financialDetails.responses.ChargesResponse
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse

class FinancialDetailsControllerDESISpec extends FinancialDetailsControllerISpec(enableIF = false)

class FinancialDetailsControllerIFISpec extends FinancialDetailsControllerISpec(enableIF = true)


abstract class FinancialDetailsControllerISpec(enableIF: Boolean) extends ComponentSpecBase {

  override def config: Map[String, String] = super.config + ("microservice.services.if.enabled" -> enableIF.toString)

  s"GET ${controllers.routes.FinancialDetailsController.getOnlyOpenItems(testNino)}" should {
    s"return $OK" when {
      "charge details are successfully retrieved" in {
        isAuthorised(true)
        stubGetOnlyOpenItems(testNino)(
          status = OK,
          response = chargeJson)

        val res: WSResponse = IncomeTaxViewChange.getOnlyOpenItems(testNino)

        val expectedResponseBody: JsValue = Json.toJson(ChargesResponse(
          balanceDetails = balanceDetails,
          codingDetails = Some(List(codingDetails)),
          documentDetails = Some(List(documentDetail, documentDetail2)),
          financialDetails = Some(List(financialDetail, financialDetail2))
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
        stubGetOnlyOpenItems(testNino)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getOnlyOpenItems(testNino)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "API responds with status OK but a non-json body" in {
        isAuthorised(true)
        stubGetOnlyOpenItems(testNino,
          status = OK,
          responseBody = "")

        val res: WSResponse = IncomeTaxViewChange.getOnlyOpenItems(testNino)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }

      "an unexpected status was returned when retrieving charge details" in {
        isAuthorised(true)
        stubGetOnlyOpenItems(testNino)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getOnlyOpenItems(testNino)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

}

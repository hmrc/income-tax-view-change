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

import helpers.ComponentSpecBase
import helpers.servicemocks.HipGetChargeHistoryStub.stubGetChargeHistory
import models.hip.chargeHistory._
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse

import java.time.{LocalDate, LocalDateTime, LocalTime}

class ChargeHistoryControllerISpec extends ComponentSpecBase {

  val chargeReference = "XAIT000000000000"
  val nino: String = "AA123456A"

  val chargeHistoryJson: JsValue = {
    Json.parse(
      s"""{
        |  "success" : {
        |    "processingDate" : "2001-12-17T09:30:17Z",
        |    "chargeHistoryDetails" : {
        |      "idType" : "NINO",
        |      "idNumber" : "$nino",
        |      "regimeType" : "ITSA",
        |      "chargeHistory" : [ {
        |        "taxYear" : "2023",
        |        "documentId" : "2740002892",
        |        "documentDate" : "2023-03-13",
        |        "documentDescription" : "Balancing Charge",
        |        "totalAmount" : 25678.99,
        |        "reversalDate" : "2022-03-14T09:30:45Z",
        |        "reversalReason" : "Manual amendment",
        |        "poaAdjustmentReason" : "005"
        |      } ]
        |    }
        |  }
        |}
        |""".stripMargin
    )
  }

  s"GET ${controllers.routes.ChargeHistoryController.getChargeHistoryDetails(nino, chargeReference)}" should {
    s"return $OK" when {
      "charge details are successfully retrieved" in {

        isAuthorised(true)

        stubGetChargeHistory(nino, chargeReference, OK, chargeHistoryJson)

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(nino, chargeReference)

        val expectedResponseBody: JsValue = Json.toJson(ChargeHistoryDetails(
          idType = "NINO",
          idValue = nino,
          regimeType = "ITSA",
          chargeHistoryDetails = Some(List(
            ChargeHistory(
              taxYear = "2023",
              documentId = "2740002892",
              documentDate = LocalDate.of(2023, 3, 13),
              documentDescription = "Balancing Charge",
              totalAmount = 25678.99,
              reversalDate = LocalDateTime.of(LocalDate.of(2022, 3, 14), LocalTime.of(9, 30, 45)),
              reversalReason = "Manual amendment",
              poaAdjustmentReason = Some("005")
            )
          ))))

        res should have(
          httpStatus(OK),
          jsonBodyMatching(expectedResponseBody)
        )
      }
    }

    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving charge details" in {
        val chargeHistoryNotFound = ChargeHistoryNotFound(404, "No charge history data was found")

        isAuthorised(true)

        stubGetChargeHistory(nino, chargeReference, NOT_FOUND, Json.toJson(chargeHistoryNotFound))

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(nino, chargeReference)

        res should have(
          httpStatus(NOT_FOUND)
        )
      }

      "a UnprocessableEntity - 422 response has been returned by the connector" in {
        val errorJson422NotFoundError: JsValue = {
          Json.obj("errors" ->
            Json.obj(
              "code" -> "005",
              "text" -> "Subscription data not found"
            ))
        }

        isAuthorised(true)

        stubGetChargeHistory(nino, chargeReference, UNPROCESSABLE_ENTITY, Json.toJson(errorJson422NotFoundError))

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(nino, chargeReference)

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving charge details" in {
        val chargeHistoryError = ChargeHistoryError(500, "An error occurred while trying to retrieve charge history data")

        isAuthorised(true)

        stubGetChargeHistory(nino, chargeReference, INTERNAL_SERVER_ERROR, Json.toJson(chargeHistoryError))

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(nino, chargeReference)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }
}

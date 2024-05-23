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

import models.chargeHistoryDetail.{ChargeHistoryDetailModel, ChargeHistorySuccessResponse}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import  helpers.ComponentSpecBase
import  helpers.servicemocks.DesChargesStub.stubChargeHistory

import java.time.LocalDate

class ChargeHistoryControllerISpec extends ComponentSpecBase {

  val mtdBsa = "XAIT000000000000"
  val documentId = "DOCID01"

  val chargeHistoryJson: JsValue = {
    Json.parse(
      s"""|{
   			 	|	"idType" : "MTDBSA",
   			 	|	"idValue" : "${mtdBsa}",
   			 	|	"regimeType" : "ITSA",
   			 	|	"chargeHistoryDetails" : [{
					|		"taxYear" : "2017",
					|		"documentId" : "DOCID01",
					|		"documentDate" : "2017-08-07",
					|		"documentDescription" : "desc",
					|		"totalAmount" : 10000.00,
					|		"reversalDate" : "2018-10-09",
					|		"reversalReason" : "reason"
					|	}]
					|}""".stripMargin)
  }

  s"GET ${controllers.routes.ChargeHistoryController.getChargeHistoryDetails(mtdBsa, documentId)}" should {
    s"return $OK" when {
      "charge details are successfully retrieved" in {

        isAuthorised(true)

        stubChargeHistory(mtdBsa, documentId)(
          status = OK,
          response = chargeHistoryJson)

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(mtdBsa, documentId)

        val expectedResponseBody: JsValue = Json.toJson(ChargeHistorySuccessResponse(
          idType = "MTDBSA",
          idValue = mtdBsa,
          regimeType = "ITSA",
          chargeHistoryDetails = Some(List(
            ChargeHistoryDetailModel(
              taxYear = "2017",
              documentId = "DOCID01",
              documentDate = LocalDate.parse("2017-08-07"),
              documentDescription = "desc",
              totalAmount = 10000.00,
              reversalDate = "2018-10-09",
              reversalReason = "reason",
              poaAdjustmentReason = None
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

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubChargeHistory(mtdBsa, documentId)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(mtdBsa, documentId)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving charge details" in {

        isAuthorised(true)

        stubChargeHistory(mtdBsa, documentId)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getChargeHistory(mtdBsa, documentId)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }
}

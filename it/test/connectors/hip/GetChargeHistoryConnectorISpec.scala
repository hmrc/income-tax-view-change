/*
 * Copyright 2025 HM Revenue & Customs
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
import models.hip.chargeHistory._
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime, LocalTime}

class GetChargeHistoryConnectorISpec extends ComponentSpecBase {

  val connector: GetChargeHistoryConnector = app.injector.instanceOf[GetChargeHistoryConnector]
  val chargeReference: String = "XD000024425799"
  val nino: String = "AA123456A"
  val chargeHistoryUrl: String = s"/etmp/RESTAdapter/ITSA/TaxPayer/GetChargeHistory?idType=NINO&idValue=$nino&chargeReference=$chargeReference"

  val chargeHistory: ChargeHistory = ChargeHistory(
    taxYear = "2023",
    documentId = "2740002892",
    documentDate = LocalDate.of(2023, 3, 13),
    documentDescription = "Balancing Charge",
    totalAmount = 25678.99,
    reversalDate = LocalDateTime.of(LocalDate.of(2022, 3, 14), LocalTime.of(9, 30, 45)),
    reversalReason = "Manual amendment",
    poaAdjustmentReason = Some("005")
  )

  val chargeHistoryDetails: ChargeHistoryDetails = ChargeHistoryDetails(
    idType = "MTDBSA",
    idValue = "XQIT00000000001",
    regimeType = "ITSA",
    chargeHistoryDetails = Some(List(chargeHistory))
  )

  val chargeHistorySuccessModel: ChargeHistorySuccess = ChargeHistorySuccess(
    processingDate = LocalDateTime.of(LocalDate.of(2001, 12, 17), LocalTime.of(9, 30, 17)),
    chargeHistoryDetails = chargeHistoryDetails
  )

  val chargeHistorySuccessWrapper: ChargeHistorySuccessWrapper =
    ChargeHistorySuccessWrapper(chargeHistorySuccessModel)

  val toJsonModel = Json.prettyPrint(Json.toJson(chargeHistorySuccessWrapper))
  println(toJsonModel)

  val chargeHistorySuccessJsonReads: JsValue = Json.parse(
    """{
      |  "success" : {
      |    "processingDate" : "2001-12-17T09:30:17Z",
      |    "chargeHistoryDetails" : {
      |      "idType" : "MTDBSA",
      |      "idNumber" : "XQIT00000000001",
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

  "GetChargeHistoryConnector" when {

    "getChargeHistory() is called" when {

      "the response status is a 200 - Ok" should {
        "return a valid GetChargeHistorySuccess response model, when parsed successfully" in {
          WiremockHelper.stubGet(chargeHistoryUrl, OK, chargeHistorySuccessJsonReads.toString())
          val result = connector.getChargeHistory(nino, chargeReference).futureValue

          result shouldBe Right(chargeHistorySuccessWrapper)
        }

        "return a ChargeHistoryError response when the API has returned data, but it hasn't been parsed successfully" in {
          WiremockHelper.stubGet(chargeHistoryUrl, OK, "{}")
          val result = connector.getChargeHistory(nino, chargeReference).futureValue

          result shouldBe Left(ChargeHistoryError(INTERNAL_SERVER_ERROR, "Json validation error parsing ChargeHistorySuccess model"))
        }
      }

      "the response is a 404 - NotFound" should {
        "return a ChargeHistoryNotFound response when the API returned no data" in {
          val jsonError = Json.obj("code" -> NOT_FOUND, "reason" -> "The remote endpoint has indicated that no match found for the reference provided.")
          WiremockHelper.stubGet(chargeHistoryUrl, NOT_FOUND, jsonError.toString())
          val result = connector.getChargeHistory(nino, chargeReference).futureValue

          result shouldBe Left(ChargeHistoryNotFound(NOT_FOUND, jsonError.toString()))
        }
      }

      "the response is a 500 - InternalServerError" should {
        "return a ChargeHistoryError response when an error has occurred unexpectedly" in {
          val jsonError = Json.obj("code" -> INTERNAL_SERVER_ERROR, "reason" -> "An unexpected error has occurred")
          WiremockHelper.stubGet(chargeHistoryUrl, INTERNAL_SERVER_ERROR, jsonError.toString())
          val result = connector.getChargeHistory(nino, chargeReference).futureValue

          result shouldBe Left(ChargeHistoryError(INTERNAL_SERVER_ERROR, jsonError.toString()))
        }
      }

      "the response is a 422 - UnprocessableEntity" should {
        "return a response which is converted to a 404 - NotFound as no data was able to be retrieved by the API" in {
          val jsonError = """{
                            |  "errors" : {
                            |    "code" : "005",
                            |    "text" : "Unable to find any charge history data"
                            |  }
                            |}
                            |""".stripMargin

          WiremockHelper.stubGet(chargeHistoryUrl, UNPROCESSABLE_ENTITY, jsonError)
          val result = connector.getChargeHistory(nino, chargeReference).futureValue
          result shouldBe Left(ChargeHistoryNotFound(NOT_FOUND, jsonError))
        }
      }
    }
  }
}

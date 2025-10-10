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

package constants.hip

import models.hip.chargeHistory.{ChargeHistory, ChargeHistoryDetails, ChargeHistorySuccess, ChargeHistorySuccessWrapper}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.{LocalDate, LocalDateTime, LocalTime}

object ChargeHistoryTestConstants {

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

  val chargeHistorySuccess: ChargeHistorySuccess = ChargeHistorySuccess(
    processingDate = LocalDateTime.of(LocalDate.of(2001, 12, 17), LocalTime.of(9, 30, 17)),
    chargeHistoryDetails = chargeHistoryDetails
  )

  val chargeHistorySuccessWrapperModel: ChargeHistorySuccessWrapper =
    ChargeHistorySuccessWrapper(success = chargeHistorySuccess)

  val chargeHistoryJson: JsValue = Json.parse(
    """{
      |  "taxYear" : "2023",
      |  "documentId" : "2740002892",
      |  "documentDate" : "2023-03-13",
      |  "documentDescription" : "Balancing Charge",
      |  "totalAmount" : 25678.99,
      |  "reversalDate" : "2022-03-14T09:30:45Z",
      |  "reversalReason" : "Manual amendment",
      |  "poaAdjustmentReason" : "005"
      |}
      |""".stripMargin)

  val chargeHistoryDetailsJsonReads: JsValue = Json.parse(
    """{
      |  "idType" : "MTDBSA",
      |  "idNumber" : "XQIT00000000001",
      |  "regimeType" : "ITSA",
      |  "chargeHistory" : [ {
      |    "taxYear" : "2023",
      |    "documentId" : "2740002892",
      |    "documentDate" : "2023-03-13",
      |    "documentDescription" : "Balancing Charge",
      |    "totalAmount" : 25678.99,
      |    "reversalDate" : "2022-03-14T09:30:45Z",
      |    "reversalReason" : "Manual amendment",
      |    "poaAdjustmentReason" : "005"
      |  } ]
      |}
      |""".stripMargin)

  val chargeHistoryDetailsJsonWrites: JsValue = Json.parse(
    """{
      |  "idType" : "MTDBSA",
      |  "idValue" : "XQIT00000000001",
      |  "regimeType" : "ITSA",
      |  "chargeHistoryDetails" : [ {
      |    "taxYear" : "2023",
      |    "documentId" : "2740002892",
      |    "documentDate" : "2023-03-13",
      |    "documentDescription" : "Balancing Charge",
      |    "totalAmount" : 25678.99,
      |    "reversalDate" : "2022-03-14T09:30:45Z",
      |    "reversalReason" : "Manual amendment",
      |    "poaAdjustmentReason" : "005"
      |  } ]
      |}
      |""".stripMargin
  )

  val chargeHistorySuccessJsonWrites: JsValue = Json.parse(
    """{
      |  "processingDate" : "2001-12-17T09:30:17Z",
      |  "chargeHistoryDetails" : {
      |    "idType" : "MTDBSA",
      |    "idValue" : "XQIT00000000001",
      |    "regimeType" : "ITSA",
      |    "chargeHistoryDetails" : [ {
      |      "taxYear" : "2023",
      |      "documentId" : "2740002892",
      |      "documentDate" : "2023-03-13",
      |      "documentDescription" : "Balancing Charge",
      |      "totalAmount" : 25678.99,
      |      "reversalDate" : "2022-03-14T09:30:45Z",
      |      "reversalReason" : "Manual amendment",
      |      "poaAdjustmentReason" : "005"
      |    } ]
      |  }
      |}
      |""".stripMargin
  )

  val chargeHistorySuccessJsonReads: JsValue = Json.parse(
    """{
      |  "processingDate" : "2001-12-17T09:30:17Z",
      |  "chargeHistoryDetails" : {
      |    "idType" : "MTDBSA",
      |    "idNumber" : "XQIT00000000001",
      |    "regimeType" : "ITSA",
      |    "chargeHistory" : [ {
      |      "taxYear" : "2023",
      |      "documentId" : "2740002892",
      |      "documentDate" : "2023-03-13",
      |      "documentDescription" : "Balancing Charge",
      |      "totalAmount" : 25678.99,
      |      "reversalDate" : "2022-03-14T09:30:45Z",
      |      "reversalReason" : "Manual amendment",
      |      "poaAdjustmentReason" : "005"
      |    } ]
      |  }
      |}
      |""".stripMargin
  )

  val chargeHistorySuccessWrapperJsonReads: JsValue = Json.parse(
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

  val chargeHistorySuccessWrapperJsonWrites: JsValue = Json.parse(
    """{
      |  "success" : {
      |    "processingDate" : "2001-12-17T09:30:17Z",
      |    "chargeHistoryDetails" : {
      |      "idType" : "MTDBSA",
      |      "idValue" : "XQIT00000000001",
      |      "regimeType" : "ITSA",
      |      "chargeHistoryDetails" : [ {
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

  val successResponse: HttpResponse = HttpResponse(Status.OK, chargeHistorySuccessWrapperJsonReads, Map.empty)
  val badJsonResponse: HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "{}")
  val notFoundResponse: HttpResponse = HttpResponse(Status.NOT_FOUND, "Error message", Map.empty)
  val badResponse: HttpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "Error message")
}

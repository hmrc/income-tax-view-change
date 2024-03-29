/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors.httpParsers

import connectors.httpParsers.PaymentAllocationsHttpParser.{NotFoundResponse, PaymentAllocationsReads, PaymentAllocationsResponse, UnexpectedResponse}
import models.paymentAllocations.{AllocationDetail, PaymentAllocations}
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

import java.time.LocalDate

class PaymentAllocationsHttpParserSpec extends TestSupport {

  val paymentAllocations: PaymentAllocations = PaymentAllocations(
    amount = Some(1000.00),
    method = Some("method"),
    reference = Some("reference"),
    transactionDate = Some(LocalDate.parse("2022-06-23")),
    allocations = Seq(
      AllocationDetail(
        transactionId = Some("transactionId"),
        from = Some(LocalDate.parse("2022-06-23")),
        to = Some(LocalDate.parse("2022-06-23")),
        chargeType = Some("type"),
        mainType = Some("mainType"),
        amount = Some(1500.00),
        clearedAmount = Some(500.00),
        chargeReference = Some("chargeReference")
      )
    )
  )

  val paymentAllocationsJson: JsObject = Json.obj(
    "paymentDetails" -> Json.arr(
      Json.obj(
        "paymentAmount" -> 1000.00,
        "paymentMethod" -> "method",
        "paymentReference" -> "reference",
        "valueDate" -> LocalDate.parse("2022-06-23"),
        "sapClearingDocsDetails" -> Json.arr(
          Json.obj(
            "sapDocNumber" -> "transactionId",
            "taxPeriodStartDate" -> LocalDate.parse("2022-06-23"),
            "taxPeriodEndDate" -> LocalDate.parse("2022-06-23"),
            "chargeType" -> "type",
            "mainType" -> "mainType",
            "amount" -> 1500.00,
            "clearedAmount" -> 500.00,
            "chargeReference" -> "chargeReference"
          )
        )
      )
    )
  )

  val multiplePaymentAllocationJson: JsObject = Json.obj(
    "paymentDetails" -> Json.arr(
      Json.obj(
        "paymentAmount" -> 1000.00,
        "paymentMethod" -> "method",
        "paymentReference" -> "reference",
        "valueDate" -> LocalDate.parse("2022-06-23"),
        "sapClearingDocsDetails" -> Json.arr(
          Json.obj(
            "sapDocNumber" -> "transactionId",
            "taxPeriodStartDate" -> LocalDate.parse("2022-06-23"),
            "taxPeriodEndDate" -> LocalDate.parse("2022-06-23"),
            "chargeType" -> "type",
            "mainType" -> "mainType",
            "amount" -> 1500.00,
            "clearedAmount" -> 500.00,
            "chargeReference" -> "chargeReference"
          )
        )
      ),
      Json.obj(
        "paymentAmount" -> 1000.00,
        "paymentMethod" -> "method",
        "paymentReference" -> "reference2",
        "valueDate" -> LocalDate.parse("2022-06-23"),
        "sapClearingDocsDetails" -> Json.arr(
          Json.obj(
            "sapDocNumber" -> "transactionId",
            "taxPeriodStartDate" -> LocalDate.parse("2022-06-23"),
            "taxPeriodEndDate" -> LocalDate.parse("2022-06-23"),
            "chargeType" -> "type",
            "mainType" -> "mainType",
            "amount" -> 1500.00,
            "clearedAmount" -> 500.00,
            "chargeReference" -> "chargeReference"
          )
        )
      )
    )
  )

  "PaymentAllocationsHttpParser" should {
    "return a payment allocations" when {
      s"$OK is returned with valid json and a single payment allocations" in {
        val httpResponse: HttpResponse = HttpResponse(OK, paymentAllocationsJson, Map.empty)

        val expectedResult: PaymentAllocationsResponse = Right(paymentAllocations)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      s"$OK is returned with valid json and multiple payment allocations" in {
        val httpResponse: HttpResponse = HttpResponse(OK, multiplePaymentAllocationJson, Map.empty)

        val expectedResponse: PaymentAllocationsResponse = Right(paymentAllocations)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResponse
      }
    }
    "return a Not Found response" when {
      "the status of the response is 404" in {
        val httpResponse: HttpResponse = HttpResponse(NOT_FOUND, "")

        PaymentAllocationsReads.read("", "", httpResponse) shouldBe Left(NotFoundResponse)
      }
    }
    s"return $UnexpectedResponse" when {
      "no payment details are returned in the json" in {
        val httpResponse: HttpResponse = HttpResponse(
          OK,
          Json.obj(
            "paymentDetails" -> Json.arr()
          ), Map.empty
        )

        val expectedResult: PaymentAllocationsResponse = Left(UnexpectedResponse)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST, "")

        val expectedResult: PaymentAllocationsResponse = Left(UnexpectedResponse)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "any other status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(INTERNAL_SERVER_ERROR, "")

        val expectedResult: PaymentAllocationsResponse = Left(UnexpectedResponse)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }
}

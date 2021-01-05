/*
 * Copyright 2021 HM Revenue & Customs
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

import connectors.httpParsers.PaymentAllocationsHttpParser.{PaymentAllocationsReads, PaymentAllocationsResponse, UnexpectedResponse}
import models.paymentAllocations.{AllocationDetail, PaymentAllocations}
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class PaymentAllocationsHttpParserSpec extends TestSupport {

  val paymentAllocations: PaymentAllocations = PaymentAllocations(
    amount = Some(1000.00),
    method = Some("method"),
    transactionDate = Some("transactionDate"),
    allocations = Seq(
      AllocationDetail(
        transactionId = Some("transactionId"),
        from = Some("from"),
        to = Some("to"),
        `type` = Some("type"),
        amount = Some(1500.00),
        clearedAmount = Some(500.00)
      )
    )
  )

  val paymentAllocationsJson: JsObject = Json.obj(
    "paymentDetails" -> Json.obj(
      "paymentAmount" -> 1000.00,
      "paymentMethod" -> "method",
      "valueDate" -> "transactionDate",
      "sapClearingDocsDetails" -> Json.arr(
        Json.obj(
          "sapDocNumber" -> "transactionId",
          "taxPeriodStartDate" -> "from",
          "taxPeriodEndDate" -> "to",
          "chargeType" -> "type",
          "amount" -> 1500.00,
          "clearedAmount" -> 500.00
        )
      )
    )
  )

  "PaymentAllocationsHttpParser" should {
    "return a payment allocations" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = OK,
          responseJson = Some(paymentAllocationsJson)
        )

        val expectedResult: PaymentAllocationsResponse = Right(paymentAllocations)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedResponse" when {
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = BAD_REQUEST
        )

        val expectedResult: PaymentAllocationsResponse = Left(UnexpectedResponse)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "any other status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = INTERNAL_SERVER_ERROR
        )

        val expectedResult: PaymentAllocationsResponse = Left(UnexpectedResponse)
        val actualResult: PaymentAllocationsResponse = PaymentAllocationsReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }
}

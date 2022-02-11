/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.FinancialDataTestConstants.{testChargesResponse, validChargesJson}
import connectors.httpParsers.ChargeHttpParser.{ChargeReads, ChargeResponse, UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class ChargesHttpParserSpec extends TestSupport {

  "ChargesHttpParser" should {
    "return a charge" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(OK, json = validChargesJson, headers = Map.empty)
        val expectedResult: ChargeResponse = Right(testChargesResponse)
        val actualResult: ChargeResponse = ChargeReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedChargeResponse" when {
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST, body = "Bad request")
        val expectedResult: ChargeResponse = Left(UnexpectedChargeResponse(BAD_REQUEST, "Bad request"))
        val actualResult: ChargeResponse = ChargeReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedChargeErrorResponse" when {
      s"$OK is returned without mandatory fields in json" in {
        val httpResponse: HttpResponse = HttpResponse(OK, body = "{}")

        val expectedResponse: ChargeResponse = Left(UnexpectedChargeErrorResponse)
        val actualResponse: ChargeResponse = ChargeReads.read("", "", httpResponse)

        actualResponse shouldBe expectedResponse
      }
      "a non 200 or 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = INTERNAL_SERVER_ERROR
        )

        val expectedResult: ChargeResponse = Left(UnexpectedChargeErrorResponse)
        val actualResult: ChargeResponse = ChargeReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }
}

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

import assets.FinancialDataTestConstants.{charges1, charges2, validChargesJson}
import connectors.httpParsers.ChargeHttpParser.{ChargeReads, ChargeResponse, UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import models.financialDetails.responses.ChargesResponse
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class ChargesHttpParserSpec extends TestSupport {

  "ChargesHttpParser" should {
    "return a charge" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = OK,
          responseJson = Some(validChargesJson)
        )

        val expectedResult: ChargeResponse = Right(ChargesResponse(List(charges1, charges2)))
        val actualResult: ChargeResponse = ChargeReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedChargeResponse" when {
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = BAD_REQUEST, responseString = Some("Bad request")
        )

        val expectedResult: ChargeResponse = Left(UnexpectedChargeResponse(BAD_REQUEST, "Bad request"))
        val actualResult: ChargeResponse = ChargeReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "any other status is returned" in {
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

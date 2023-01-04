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

import assets.ChargeHistoryTestConstants._
import connectors.httpParsers.ChargeHistoryHttpParser._
import models.chargeHistoryDetail.ChargeHistorySuccessResponse
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class ChargeHistoryHttpParserSpec extends TestSupport {
  "ChargeHistoryHttpParser" should {
    "return charge history" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(OK, testValidChargeHistorySuccessResponseJson, Map.empty)

        val expectedResult: ChargeHistoryResponse = Right(ChargeHistorySuccessResponse(
          idType = "MTDBSA",
          idValue = "XAIT000000000000",
          regimeType = "ITSA",
          chargeHistoryDetails = Some(List(chargeHistoryDetail))))
        val actualResult: ChargeHistoryResponse = ChargeHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }

      s"$OK is returned with no charge history" in {
        val httpResponse: HttpResponse = HttpResponse(OK, testEmptyValidChargeHistorySuccessResponseJson, Map.empty)

        val expectedResult: ChargeHistoryResponse = Right(ChargeHistorySuccessResponse(
          idType = "MTDBSA",
          idValue = "XAIT000000000000",
          regimeType = "ITSA",
          chargeHistoryDetails = None))
        val actualResult: ChargeHistoryResponse = ChargeHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedChargeHistoryResponse" when {
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST, "Bad request", Map.empty)

        val expectedResult: ChargeHistoryResponse = Left(UnexpectedChargeHistoryResponse(BAD_REQUEST, "Bad request"))
        val actualResult: ChargeHistoryResponse = ChargeHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "any other status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(INTERNAL_SERVER_ERROR, "")

        val expectedResult: ChargeHistoryResponse = Left(ChargeHistoryErrorResponse)
        val actualResult: ChargeHistoryResponse = ChargeHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }

}

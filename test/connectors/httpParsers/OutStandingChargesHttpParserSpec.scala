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

import assets.OutStandingChargesConstant._
import connectors.httpParsers.OutStandingChargesHttpParser._
import models.outStandingCharges.OutstandingChargesSuccessResponse
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class OutStandingChargesHttpParserSpec extends TestSupport {
  "OutStandingChargesHttpParser" should {
    "return a out standing charge" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = OK,
          responseJson = Some(validMultipleOutStandingChargeDesResponseJson)
        )

        val expectedResult: OutStandingChargeResponse = Right(OutstandingChargesSuccessResponse(List(outStandingChargeModelOne, outStandingChargeModelTwo)))
        val actualResult: OutStandingChargeResponse = OutStandingChargesReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
    s"return $UnexpectedOutStandingChargeResponse" when {
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = BAD_REQUEST, responseString = Some("Bad request")
        )

        val expectedResult: OutStandingChargeResponse = Left(UnexpectedOutStandingChargeResponse(BAD_REQUEST, "Bad request"))
        val actualResult: OutStandingChargeResponse = OutStandingChargesReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "any other status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(
          responseStatus = INTERNAL_SERVER_ERROR
        )

        val expectedResult: OutStandingChargeResponse = Left(OutStandingChargeErrorResponse)
        val actualResult: OutStandingChargeResponse = OutStandingChargesReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }

}

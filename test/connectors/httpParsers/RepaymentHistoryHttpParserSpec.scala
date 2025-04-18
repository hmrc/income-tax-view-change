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

import constants.RepaymentHistoryTestConstants._
import connectors.httpParsers.RepaymentHistoryHttpParser._
import models.repaymentHistory.{RepaymentHistory, RepaymentHistorySuccessResponse, RepaymentItem, RepaymentSupplementItem}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

import java.time.LocalDate

class RepaymentHistoryHttpParserSpec extends TestSupport {
  "RepaymentHistoryHttpParser" should {

    "return Repayment history" when {
      s"$OK is returned with valid json" in {
        val httpResponse: HttpResponse = HttpResponse(OK, repaymentHistoryFullJson, Map.empty)

        val expectedResult: RepaymentHistoryResponse = Right(
          RepaymentHistorySuccessResponse(
            List(
              RepaymentHistory(
                amountApprovedforRepayment = Some(100.0),
                amountRequested = 200.0,
                repaymentMethod = Some("BACD"),
                totalRepaymentAmount = Some(300.0),
                repaymentItems = Some(Seq[RepaymentItem](
                  RepaymentItem(
                    repaymentSupplementItem =
                      Some(Seq(
                        RepaymentSupplementItem(
                          parentCreditReference = Some("002420002231"),
                          amount = Some(400.0),
                          fromDate = Some(LocalDate.parse("2021-07-23")),
                          toDate = Some(LocalDate.parse("2021-08-23")),
                          rate = Some(500.0)
                        )
                      )
                      )))
                ),
                estimatedRepaymentDate = Some(LocalDate.parse("2021-08-11")),
                creationDate = Some(LocalDate.parse("2020-12-06")),
                repaymentRequestNumber = "000000003135",
                status = "A"
              )
            )
          )
        )
        val actualResult: RepaymentHistoryResponse = RepaymentHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }

    s"return $UnexpectedRepaymentHistoryResponse" when {
      "a 4xx status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(BAD_REQUEST, "Bad request", Map.empty)

        val expectedResult: RepaymentHistoryResponse = Left(UnexpectedRepaymentHistoryResponse(BAD_REQUEST, "Bad request"))
        val actualResult: RepaymentHistoryResponse = RepaymentHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
      "any other status is returned" in {
        val httpResponse: HttpResponse = HttpResponse(INTERNAL_SERVER_ERROR, "")

        val expectedResult: RepaymentHistoryResponse = Left(RepaymentHistoryErrorResponse)
        val actualResult: RepaymentHistoryResponse = RepaymentHistoryReads.read("", "", httpResponse)

        actualResult shouldBe expectedResult
      }
    }
  }
}

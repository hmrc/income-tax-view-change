/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import connectors.httpParsers.RepaymentHistoryHttpParser.{RepaymentHistoryErrorResponse, UnexpectedRepaymentHistoryResponse}
import helpers.{ComponentSpecBase, WiremockHelper}
import models.repaymentHistory.{RepaymentHistory, RepaymentHistorySuccessResponse, RepaymentItem, RepaymentSupplementItem}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

class RepaymentHistoryDetailsConnectorISpec extends ComponentSpecBase {

  val connector: RepaymentHistoryDetailsConnector = app.injector.instanceOf[RepaymentHistoryDetailsConnector]

  val nino = "AA123456A"
  val repaymentId = "012345678912"

  val url = s"/income-tax/self-assessment/repayments-viewer/$nino"
  val urlWithRepaymentId = s"/income-tax/self-assessment/repayments-viewer/$nino?repaymentRequestNumber=$repaymentId"

  val repaymentHistoryResponseJson: JsValue =
    Json.obj(
      "repaymentsViewerDetails" ->
        Json.arr(
          Json.obj(
            "repaymentRequestNumber" -> "000000003135",
            "amountApprovedforRepayment" -> Some(100.0),
            "amountRequested" -> 200.0,
            "repaymentMethod" -> "BACD",
            "totalRepaymentAmount" -> 300.0,
            "repaymentItems" -> Json.arr(
              Json.obj(
                "repaymentSupplementItem" -> Json.arr(
                  Json.obj(
                    "parentCreditReference" -> Some("002420002231"),
                    "amount" -> Some(400.0),
                    "fromDate" -> Some(LocalDate.parse("2021-07-23")),
                    "toDate" -> Some(LocalDate.parse("2021-08-23")),
                    "rate" -> Some(500.0)
                  )
                )
              )
            ),
            "estimatedRepaymentDate" -> LocalDate.parse("2021-08-11"),
            "creationDate" -> LocalDate.parse("2020-12-06"),
            "status" -> "A"
          )
        )
    )

  val repaymentHistoryDetail: RepaymentHistory = RepaymentHistory(
    repaymentRequestNumber = "000000003135",
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
    status = "A"
  )

  "RepaymentHistoryDetailsConnector" when {

    ".listRepaymentHistoryDetailsUrl() is called" when {

      "the response is an OK - 200" should {

        "return a RepaymentHistorySuccessResponse model when successful" in {
          WiremockHelper.stubGet(url, OK, repaymentHistoryResponseJson.toString())
          val result = connector.getAllRepaymentHistoryDetails(nino).futureValue

          result shouldBe Right(RepaymentHistorySuccessResponse(List(repaymentHistoryDetail)))
        }
      }

      "the response is a NotFound - 404" should {

        "return a UnexpectedRepaymentHistoryResponse when no data was found" in {
          val jsonError = Json.obj("code" -> NOT_FOUND, "reason" -> "The remote endpoint has indicated that no match found for the nino provided.")
          WiremockHelper.stubGet(url, NOT_FOUND, jsonError.toString())
          val result = connector.getAllRepaymentHistoryDetails(nino).futureValue

          result shouldBe Left(UnexpectedRepaymentHistoryResponse(NOT_FOUND, jsonError.toString()))
        }
      }

      "the response is an InternalServerError - 500" should {

        "return an RepaymentHistoryErrorResponse when an unexpected error has occured" in {
          WiremockHelper.stubGet(url, INTERNAL_SERVER_ERROR, "{}")
          val result = connector.getAllRepaymentHistoryDetails(nino).futureValue

          result shouldBe Left(RepaymentHistoryErrorResponse)
        }
      }
    }

    ".getRepaymentHistoryDetailsById() is called" when {

      "the response is an OK - 200" should {

        "return a RepaymentHistorySuccessResponse model when successful" in {
          WiremockHelper.stubGet(urlWithRepaymentId, OK, repaymentHistoryResponseJson.toString())
          val result = connector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

          result shouldBe Right(RepaymentHistorySuccessResponse(List(repaymentHistoryDetail)))
        }
      }

      "the response is a NotFound - 404" should {

        "return a UnexpectedRepaymentHistoryResponse when no data was found" in {
          val jsonError = Json.obj("code" -> NOT_FOUND, "reason" -> "The remote endpoint has indicated that no match found for the nino provided.")
          WiremockHelper.stubGet(urlWithRepaymentId, NOT_FOUND, jsonError.toString())
          val result = connector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

          result shouldBe Left(UnexpectedRepaymentHistoryResponse(NOT_FOUND, jsonError.toString()))
        }
      }

      "the response is an InternalServerError - 500" should {

        "return an RepaymentHistoryErrorResponse when an unexpected error has occured" in {
          WiremockHelper.stubGet(urlWithRepaymentId, INTERNAL_SERVER_ERROR, "{}")
          val result = connector.getRepaymentHistoryDetailsById(nino, repaymentId).futureValue

          result shouldBe Left(RepaymentHistoryErrorResponse)
        }
      }
    }
  }
}

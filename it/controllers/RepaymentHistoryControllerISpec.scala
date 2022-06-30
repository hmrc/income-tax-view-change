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

package controllers

import helpers.ComponentSpecBase
import helpers.servicemocks.DesChargesStub.{stubRepaymentHistoryByDate, stubRepaymentHistoryById}
import models.repaymentHistory.{RepaymentHistory, RepaymentHistorySuccessResponse, RepaymentSupplementItem}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse

class RepaymentHistoryControllerISpec extends ComponentSpecBase {

  val repaymentId = "12789971"
  val nino = "AA0000AA"
  val fromDate = "2021-07-23"
  val toDate = "2021-08-23"


  val validRepaymentHistoryJson: JsValue = Json.obj(
    "repaymentsViewerDetails" ->
      Json.arr(
        Json.obj(
          "repaymentRequestNumber" -> "000000003135",
          "amountApprovedforRepayment" -> Some(100.0),
          "amountRequested" -> 200.0,
          "repaymentMethod" -> "BACD",
          "totalRepaymentAmount" -> 300.0,
          "items" -> Json.arr(
            Json.obj(
              "parentCreditReference" -> Some("002420002231"),
              "amount" -> Some(400.0),
              "fromDate" -> Some("2021-07-23"),
              "toDate" -> Some("2021-08-23"),
              "rate" -> Some(500.0)
            )
          ),
          "estimatedRepaymentDate" -> "2021-01-21",
          "creationDate" -> "2020-12-25"
        )
      )
  )


  s"GET ${controllers.routes.RepaymentHistoryController.getRepaymentHistoryById(nino, repaymentId)}" should {
    s"return $OK" when {
      "repayment history are successfully retrieved by Id" in {

        isAuthorised(true)

        stubRepaymentHistoryById(nino, repaymentId)(
          status = OK,
          response = validRepaymentHistoryJson)

        val res: WSResponse = IncomeTaxViewChange.getRepaymentHistoryById(nino, repaymentId)

        val expectedResponseBody: JsValue = Json.toJson(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = List(
            RepaymentHistory(
              amountApprovedforRepayment = Some(100.0),
              amountRequested = 200.0,
              repaymentMethod = "BACD",
              totalRepaymentAmount = 300.0,
              items = Some(Seq(
                RepaymentSupplementItem(
                  parentCreditReference = Some("002420002231"),
                  amount = Some(400.0),
                  fromDate = Some("2021-07-23"),
                  toDate = Some("2021-08-23"),
                  rate = Some(500.0)
                )
              )),
              estimatedRepaymentDate = "2021-01-21",
              creationDate = "2020-12-25",
              repaymentRequestNumber = "000000003135"
            ))))

        res should have(
          httpStatus(OK),
          jsonBodyMatching(expectedResponseBody)
        )
      }
    }


    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving repayment history by ID" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubRepaymentHistoryById(nino, repaymentId)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getRepaymentHistoryById(nino, repaymentId)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving repayment history by ID" in {

        isAuthorised(true)

        stubRepaymentHistoryById(nino, repaymentId)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getRepaymentHistoryById(nino, repaymentId)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }

  }

  s"GET ${controllers.routes.RepaymentHistoryController.getRepaymentHistoryByDate(nino, fromDate, toDate)}" should {
    s"return $OK" when {
      "repayment history is successfully retrieved by date range" in {

        isAuthorised(true)

        stubRepaymentHistoryByDate(nino, fromDate, toDate)(
          status = OK,
          response = validRepaymentHistoryJson)

        val res: WSResponse = IncomeTaxViewChange.getRepaymentHistoryByDate(nino, fromDate, toDate)

        val expectedResponseBody: JsValue = Json.toJson(RepaymentHistorySuccessResponse(
          repaymentsViewerDetails = List(
            RepaymentHistory(
              amountApprovedforRepayment = Some(100.0),
              amountRequested = 200.0,
              repaymentMethod = "BACD",
              totalRepaymentAmount = 300.0,
              items = Some(Seq(
                RepaymentSupplementItem(
                  parentCreditReference = Some("002420002231"),
                  amount = Some(400.0),
                  fromDate = Some("2021-07-23"),
                  toDate = Some("2021-08-23"),
                  rate = Some(500.0)
                )
              )),
              estimatedRepaymentDate = "2021-01-21",
              creationDate = "2020-12-25",
              repaymentRequestNumber = "000000003135"
            ))))

        res should have(
          httpStatus(OK),
          jsonBodyMatching(expectedResponseBody)
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving repayment history by date range" in {

        isAuthorised(true)

        stubRepaymentHistoryByDate(nino, fromDate, toDate)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getRepaymentHistoryByDate(nino, fromDate, toDate)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }

    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving repayment history by date range" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubRepaymentHistoryByDate(nino, fromDate, toDate)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getRepaymentHistoryByDate(nino, fromDate, toDate)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }
  }
}
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

package constants

import models.hip.createIncomeSource.{AddressDetails, BusinessDetails, CreateBusinessIncomeSourceHipRequest}
import models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, IncomeSource}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDate

object CreateHipBusinessDetailsTestConstants {

  val testDate = LocalDate.of(2022, 5, 1).toString

  val testMtdRef = "XIAT00000000000"

  val validCreateSelfEmploymentRequest =
    CreateBusinessIncomeSourceHipRequest(
      testMtdRef,
      List(
        BusinessDetails(
          accountingPeriodStartDate = testDate,
          accountingPeriodEndDate = testDate,
          tradingName = "Big Business",
          address = AddressDetails("10 FooBar Street", None, None, None, "GB", None),
          typeOfBusiness = "test business type",
          tradingStartDate = testDate,
          cashAccrualsFlag = Some("C"),
          cessationDate = None,
          cessationReason = None
        )
      )
    )

  val successResponse = List(IncomeSource("AAIS12345678901"))
  val successResponseJson =
    """
      |{
      |    "success": {
      |      "processingDate": "2023-05-19T19:34:22Z",
      |      "incomeSourceIdDetails": [
      |        {
      |          "incomeSourceId": "AAIS12345678901"
      |        }
      |      ]
      |    }
      |  }
      |""".stripMargin

  val successHttpResponse =
    HttpResponse(
      Status.CREATED,
      Json.parse(successResponseJson),
      Map.empty
    )

  val errorResponseBody: JsValue = Json.parse(
    """
      |  {
      |    "origin": "HIP",
      |    "response": {
      |      "error": {
      |        "code": "400",
      |        "message": "Duplicate Submission",
      |        "logID": "C0000AB8190C8E1F000000C700006836"
      |      }
      |    }
      |  }
      |""".stripMargin)

  val badRequestFailureResponse = CreateBusinessDetailsHipErrorResponse(Status.BAD_REQUEST, errorResponseBody.toString)

  val internalServerErrorFailureResponse = CreateBusinessDetailsHipErrorResponse(Status.INTERNAL_SERVER_ERROR, "error")

  val failureHttpResponse =
    HttpResponse(
      Status.BAD_REQUEST,
      errorResponseBody,
      Map.empty
    )
}

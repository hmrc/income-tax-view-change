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

package assets

import models.createIncomeSource.{AddressDetails, BusinessDetails, CreateBusinessIncomeSourceRequest}
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsErrorResponse, IncomeSource}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDate

object CreateBusinessDetailsTestConstants {

  val testDate = LocalDate.of(2022, 5, 1).toString

  val testMtdRef = "123456789012345"

  val validCreateSelfEmploymentRequest =
    CreateBusinessIncomeSourceRequest(
      List(
        BusinessDetails(
          accountingPeriodStartDate = testDate,
          accountingPeriodEndDate = testDate,
          tradingName = "Big Business",
          addressDetails = AddressDetails("10 FooBar Street", None, None, None, None, None),
          typeOfBusiness = None,
          tradingStartDate = testDate,
          cashOrAccrualsFlag = "CASH",
          cessationDate = None,
          cessationReason = None
        )
      )
    )

  val successResponse = List(IncomeSource("AAIS12345678901"))

  val successHttpResponse =
    HttpResponse(
      Status.OK,
      Json.toJson(successResponse),
      Map.empty
    )

  val invalidRequest = validCreateSelfEmploymentRequest.copy(businessDetails = Nil)

  val failureResponse = CreateBusinessDetailsErrorResponse(Status.BAD_REQUEST, "dummy message")

  val failureHttpResponse =
    HttpResponse(
      Status.BAD_REQUEST,
      Json.toJson(failureResponse),
      Map.empty
    )

  val successHttpResponseWithInvalidJson =
    HttpResponse(
      Status.OK,
      Json.toJson(failureResponse),
      Map.empty
    )
}

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

package test.assets

import BaseIntegrationTestConstants.testNino
import models.updateIncomeSource.request.{Cessation, TaxYearSpecific, UpdateIncomeSourceRequestModel}
import models.updateIncomeSource.{UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}
import play.api.libs.json.Json
import play.mvc.Http.Status

import java.time.LocalDate

object UpdateIncomeSourceIntegrationTestConstants {
  val timeStamp = "2022-01-31T09:26:17Z"
  val incomeSourceId = "11111111111"
  val cessationIndicator = true
  val cessationDate = "2022-01-30"
  val successResponse = UpdateIncomeSourceResponseModel(timeStamp)
  val successResponseJson = Json.toJson(successResponse)

  val requestCessation: UpdateIncomeSourceRequestModel = UpdateIncomeSourceRequestModel(
    nino = testNino,
    incomeSourceID = incomeSourceId,
    cessation = Some(Cessation(cessationIndicator, Some(LocalDate.parse(cessationDate))))
  )

  val requestTaxYearSpecific: UpdateIncomeSourceRequestModel = UpdateIncomeSourceRequestModel(
    nino = testNino,
    incomeSourceID = incomeSourceId,
    taxYearSpecific = Some(TaxYearSpecific("2022", true))
  )


  val requestJson = Json.obj(
    "nino" -> testNino,
    "incomeSourceID" -> incomeSourceId,
    "cessation" -> Json.obj(
      "cessationIndicator" -> cessationIndicator,
      "cessationDate" -> cessationDate)
  )

  val invalidRequestJson = Json.obj()
  val failureResponse = UpdateIncomeSourceResponseError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")
}

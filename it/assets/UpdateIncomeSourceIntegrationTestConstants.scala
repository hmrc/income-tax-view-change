package assets


import assets.BaseIntegrationTestConstants.testNino
import models.updateIncomeSource.request.{Cessation, TaxYearSpecific, UpdateIncomeSourceRequestModel}
import models.updateIncomeSource.{UpdateIncomeSourceListResponseError, UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}
import play.api.libs.json.Json

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
  val failureResponse = UpdateIncomeSourceListResponseError(Seq(UpdateIncomeSourceResponseError("INTERNAL_SERVER_ERROR", s"Unexpected failed future, error")))
}

package assets

import models.itsaStatus._
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.libs.json.Json

object ITSAStatusIntegrationTestConstants {
  val taxableEntityId = "AA000000A"
  val taxYear = "2020"
  val statusDetail = StatusDetail("2023-06-15T15:38:33.960Z", "No Status", "Sign up - return available", Some(8000.25))
  val successITSAStatusResponseModel = ITSAStatusResponseModel("2019-20", Some(List(statusDetail)))
  val errorITSAStatusNotFoundError = ITSAStatusResponseNotFound(NOT_FOUND, "Dummy message")
  val failedFutureITSAStatusError = ITSAStatusResponseError(INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")

  val failureInvalidRequestResponse =
    """{
      |  "failures": [
      |    {
      |      "code": "INVALID_TAX_YEAR",
      |      "reason": "Submission has not passed validation. Invalid parameter taxYear."
      |    }
      |  ]
      |}""".stripMargin

  val errorITSAStatusError = ITSAStatusResponseError(BAD_REQUEST, failureInvalidRequestResponse)

  val successITSAStatusListResponseJson = Json.parse(
    """
      |[{
      |    "taxYear": "2019-20",
      |    "itsaStatusDetails": [
      |      {
      |        "submittedOn": "2023-06-15T15:38:33.960Z",
      |        "status": "No Status",
      |        "statusReason": "Sign up - return available",
      |        "businessIncomePriorTo2Years": 8000.25
      |      }
      |    ]
      |  }]
      |""".stripMargin)
}

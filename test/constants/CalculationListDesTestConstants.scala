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

import models.calculationList.{CalculationListModel, CalculationListResponseModel}
import models.errors.{Error, ErrorResponse, MultiError}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse

object CalculationListDesTestConstants {

  val jsonResponseFull: JsValue = Json.parse(
    """
      |[
      | {
      |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
      |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
      |   "calculationType":"finalDeclaration",
      |   "crystallised": true
      | }
      |]
      |""".stripMargin)


  val calculationListFull: CalculationListResponseModel = {
    CalculationListResponseModel(
      calculations = Seq(CalculationListModel(
        calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        calculationTimestamp = "2023-10-31T12:55:51.159Z",
        calculationType = "finalDeclaration",
        crystallised = Some(true)
      ))
    )
  }

  val jsonResponseMin: JsValue = Json.parse(
    """
      |[
      | {
      |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
      |   "calculationTimestamp":"2023-10-31T12:55:51.159Z",
      |   "calculationType":"finalDeclaration"
      | }
      |]
      |""".stripMargin)

  val calculationListMin: CalculationListResponseModel = {
    CalculationListResponseModel(
      calculations = Seq(CalculationListModel(
        calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        calculationTimestamp = "2023-10-31T12:55:51.159Z",
        calculationType = "finalDeclaration",
        crystallised = None
      ))
    )
  }

  val singleError: Error = Error(code = "CODE", reason = "ERROR MESSAGE")

  val jsonSingleError: JsValue = Json.obj("code" -> "CODE", "reason" -> "ERROR MESSAGE")

  val unexpectedJsonError: Error = Error(code = "UNEXPECTED_JSON_FORMAT", reason = "The downstream service responded with json which did not match the expected format.")

  val badRequestSingleError: Either[ErrorResponse, Nothing] =
    Left(ErrorResponse(Status.BAD_REQUEST, singleError))

  val unexpectedJsonFormat: Either[ErrorResponse, Nothing] =
    Left(ErrorResponse(Status.INTERNAL_SERVER_ERROR, unexpectedJsonError))

  val multiError: MultiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val badRequestMultiError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
    Status.BAD_REQUEST,
    multiError
  ))

  val successResponse: HttpResponse = HttpResponse(Status.OK, jsonResponseFull, Map.empty)
  val badJson = HttpResponse(Status.OK, Json.toJson("{}"), Map.empty)
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "Dummy error message")

}

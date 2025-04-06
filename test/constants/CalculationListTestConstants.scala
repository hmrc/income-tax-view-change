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
import play.api.libs.json.{JsArray, JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse

object CalculationListTestConstants {

  val jsonResponseFull: JsValue = Json.parse(
    """
      |[
      | {
      |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
      |   "calculationTimestamp":"2023-10-31T12:55:51Z",
      |   "calculationType":"crystallisation",
      |   "crystallised": true
      | }
      |]
      |""".stripMargin)


  val calculationListFull: CalculationListResponseModel = {
    CalculationListResponseModel(
      calculations = Seq(CalculationListModel(
        calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        calculationTimestamp = "2023-10-31T12:55:51Z",
        calculationType = "crystallisation",
        crystallised = Some(true)
      ))
    )
  }

  val jsonResponseMin: JsValue = Json.parse(
    """
      |[
      | {
      |   "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
      |   "calculationTimestamp":"2023-10-31T12:55:51Z",
      |   "calculationType":"crystallisation"
      | }
      |]
      |""".stripMargin)

  val calculationListMin: CalculationListResponseModel = {
    CalculationListResponseModel(
      calculations = Seq(CalculationListModel(
        calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
        calculationTimestamp = "2023-10-31T12:55:51Z",
        calculationType = "crystallisation",
        crystallised = None
      ))
    )
  }

  private val invalidNino: JsArray = Json.arr(
    Json.obj("errorCode" -> "1215", "errorDescription" -> "Invalid taxable entity id")
  )

  private val invalidTaxYear: JsArray = Json.arr(
    Json.obj("errorCode" -> "1117", "errorDescription" -> "The tax year provided is invalid")
  )

  private val responseWithTypeReason: JsValue = Json.obj(
    "failures" -> Json.arr(
    Json.obj("type" -> "1117", "reason" -> "The tax year provided is invalid"),
    Json.obj("type" -> "1215", "reason" -> "Invalid taxable entity id")
  ))

  private val responseWithErrorCodeAndDescription: JsArray = Json.arr(
    Json.obj("errorCode" -> "1117", "errorDescription" -> "The tax year provided is invalid"),
    Json.obj("errorCode" -> "1215", "errorDescription" -> "Invalid taxable entity id")
  )

  val unAuthorizedErrorResponse: JsArray = Json.arr(
    Json.obj("errorCode" -> "5009", "errorDescription" -> "Unsuccessful authorisation")
  )

  val notFoundErrorResponse: JsArray = Json.arr(
    Json.obj("errorCode" -> "5010", "errorDescription" -> "The Requested resource could not be found")
  )

  val badRequestErrorResponse: JsValue = Json.obj("origin" -> "HIP", "response" -> responseWithTypeReason)
  val invalidNinoResponse: JsValue = Json.obj("origin" -> "HIP", "response" -> invalidNino)
  val invalidTaxYearResponse: JsValue = Json.obj("origin" -> "HIP", "response" -> invalidTaxYear)
  val badRequestErrorResponse2: JsValue = Json.obj("origin" -> "HIP", "response" -> responseWithErrorCodeAndDescription)

  val internalServerErrorResponse: JsValue = Json.obj("origin" -> "HIP", "response" -> responseWithTypeReason)


  val successResponse: HttpResponse = HttpResponse(Status.OK, jsonResponseFull, Map.empty)
  val badRequestUnexpectedJson = HttpResponse(Status.BAD_REQUEST, Json.toJson("{}"), Map.empty)
  val unauthorizedUnexpectedJson = HttpResponse(Status.UNAUTHORIZED, Json.toJson("{}"), Map.empty)
  val notFoundUnexpectedJson = HttpResponse(Status.NOT_FOUND, Json.toJson(
    s"""
      |{
      |"test": "error"
      |}
      |""".stripMargin), Map.empty)
  val badGatewayJson = HttpResponse(Status.BAD_GATEWAY, "", Map.empty)
  val serviceUnavailableResponse = HttpResponse(Status.SERVICE_UNAVAILABLE, "{}", Map.empty)
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.toJson(
    s"""
       |{
       |"test": "error"
       |}
       |""".stripMargin), Map.empty)
  val nonJsonResponse = HttpResponse(Status.BAD_REQUEST, "non json response", Map.empty)
}

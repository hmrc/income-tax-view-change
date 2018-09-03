/*
 * Copyright 2018 HM Revenue & Customs
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

import models.PreviousCalculation._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}

object PreviousCalculationTestConstants {

  val testNino: String = "AA000000B"
  val testYear: String = "2008"

  val responseJsonFull: JsValue = Json.parse(
    """
      |{"calcOutput":{
      |"calcID":"1",
      |"calcAmount":22.56,
      |"calcTimestamp":"2008-05-01",
      |"crystallised":true,
      |"calcResult":{"incomeTaxNicYtd":500.68,"eoyEstimate":{"incomeTaxNicAmount":125.63}}}}
    """.stripMargin
  )

  val previousCalculationFull: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = Some(22.56), calcTimestamp = Some("2008-05-01"), crystallised = Some(true),
        calcResult = Some(CalcResult(incomeTaxNicYtd = 500.68, Some(EoyEstimate(125.63))))))

  val previousCalculationMinimum: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = None, calcTimestamp = None, crystallised = None,
        calcResult = None))

  val responseJsonMinimum: JsValue = Json.parse(
    """{"calcOutput": {"calcID": "1"}}""".stripMargin)

  val testPreviousCalculationNoEoy: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = Some(22.56), calcTimestamp = Some("2008-05-01"), crystallised = Some(true),
        calcResult = Some(CalcResult(incomeTaxNicYtd = 500.68, eoyEstimate = None))))

  val responseJsonNoEoy: JsValue = Json.parse(
    """{
      |	"calcOutput": {
      |		"calcID": "1",
      |		"calcAmount": 22.56,
      |		"calcTimestamp": "2008-05-01",
      |		"crystallised": true,
      |		"calcResult": {
      |			"incomeTaxNicYtd": 500.68
      |		}
      |	}
      |}""".stripMargin)

  val jsonMultipleErrors: JsValue =
  Json.obj(
    "failures" -> Json.arr(
      Json.obj(
        "code" -> "ERROR CODE 1",
        "reason" -> "ERROR MESSAGE 1"
      ),
      Json.obj(
        "code" -> "ERROR CODE 2",
        "reason" -> "ERROR MESSAGE 2"
      )
    )
  )

  val multiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val singleError = Error(code = "CODE", reason = "ERROR MESSAGE")

  val jsonSingleError: JsValue = Json.obj("code"->"CODE","reason"->"ERROR MESSAGE")

  val badRequestMultiError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
    Status.BAD_REQUEST,
    multiError
  ))

  val badRequestSingleError: Either[ErrorResponse, Nothing] =
    Left(ErrorResponse(Status.BAD_REQUEST, singleError))

  val badNino: String = "55F"
}

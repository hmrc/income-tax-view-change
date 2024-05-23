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

package models.claimToAdjustPoa

import models.claimToAdjustPoa.ClaimToAdjustPoaApiResponse.SuccessResponse
import play.api.libs.json.{Format, Json}


object ClaimToAdjustPoaResponse {

  type ClaimToAdjustPoaBody = Either[ErrorResponse, SuccessResponse]

  case class ClaimToAdjustPoaResponse(status: Int, claimToAdjustPoaBody: ClaimToAdjustPoaBody)

  case class ErrorResponse(message: String)

  implicit val errorResponseFormat: Format[ErrorResponse] = Json.format[ErrorResponse]

}

object ClaimToAdjustPoaApiResponse {

  case class ClaimToAdjustPoaApiSuccess(successResponse: SuccessResponse)

  case class SuccessResponse(processingDate: String)

  case class ClaimToAdjustPoaApiFailure(failures: Seq[Failure]) {
    override def toString: String = failures.map(_.code).mkString(", ")
  }

  case class Failure(code: String, reason: String)

  implicit val successFormat: Format[SuccessResponse] = Json.format[SuccessResponse]

  implicit val failureFormat: Format[Failure] = Json.format[Failure]

  implicit val failureResponseFormat: Format[ClaimToAdjustPoaApiFailure] = Json.format[ClaimToAdjustPoaApiFailure]

  implicit val successResponseFormat: Format[ClaimToAdjustPoaApiSuccess] = Json.format[ClaimToAdjustPoaApiSuccess]

}

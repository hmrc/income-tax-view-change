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

package models.hip.updateCustomerFact

import play.api.libs.json.*

sealed trait UpdateCustomerFact

case class UpdateCustomerFactRequest(
                                      idType: String,
                                      idValue: String,
                                      regimeType: String,
                                      factId: String,
                                      value: String
                                    ) extends UpdateCustomerFact

object UpdateCustomerFactRequest {
  implicit val format: Format[UpdateCustomerFactRequest] = Json.format[UpdateCustomerFactRequest]
}

case class ErrorResponse(error: Error)

object ErrorResponse {
  implicit val format: Format[ErrorResponse] = Json.format[ErrorResponse]
}

case class Error(code: String, message: String, logID: String) extends UpdateCustomerFact

object Error {
  implicit val format: Format[Error] = Json.format[Error]
}

case class ErrorsResponse(errors: Errors)

object ErrorsResponse {
  implicit val format: Format[ErrorsResponse] = Json.format[ErrorsResponse]
}

case class Errors(processingDate: String, code: String, text: String) extends UpdateCustomerFact

object Errors {
  implicit val format: Format[Errors] = Json.format[Errors]
}
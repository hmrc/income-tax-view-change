/*
 * Copyright 2025 HM Revenue & Customs
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

package models.hip.chargeHistory

import play.api.libs.json.{Json, OFormat}
import utils.JsonUtils

import java.time.LocalDateTime

sealed trait ChargeHistoryResponseError

case class ChargeHistorySuccessWrapper(success: ChargeHistorySuccess)

case class ChargeHistorySuccess(processingDate: LocalDateTime, chargeHistoryDetails: ChargeHistoryDetails)

case class ChargeHistoryError(status: Int, reason: String) extends ChargeHistoryResponseError

case class ChargeHistoryNotFound(status: Int, reason: String) extends ChargeHistoryResponseError

object ChargeHistorySuccess extends JsonUtils{
  implicit val format: OFormat[ChargeHistorySuccess] = Json.format[ChargeHistorySuccess]
}

object ChargeHistorySuccessWrapper {
  implicit val format: OFormat[ChargeHistorySuccessWrapper] = Json.format[ChargeHistorySuccessWrapper]

  def toChargeHistoryModel(chargeHistory: ChargeHistorySuccessWrapper): ChargeHistoryDetails = {
    ChargeHistoryDetails(
      idType = chargeHistory.success.chargeHistoryDetails.idType,
      idValue = chargeHistory.success.chargeHistoryDetails.idValue,
      regimeType = chargeHistory.success.chargeHistoryDetails.regimeType,
      chargeHistoryDetails = chargeHistory.success.chargeHistoryDetails.chargeHistoryDetails
    )
  }
}

object ChargeHistoryError {
  implicit val format: OFormat[ChargeHistoryError] = Json.format[ChargeHistoryError]
}

object ChargeHistoryNotFound {
  implicit val format: OFormat[ChargeHistoryNotFound] = Json.format[ChargeHistoryNotFound]
}

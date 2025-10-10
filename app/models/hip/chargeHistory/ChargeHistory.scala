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

import play.api.libs.json._
import utils.JsonUtils

import java.time.{LocalDate, LocalDateTime}

case class ChargeHistory(taxYear: String,
                         documentId: String,
                         documentDate: LocalDate,
                         documentDescription: String,
                         totalAmount: BigDecimal,
                         reversalDate: LocalDateTime,
                         reversalReason: String,
                         poaAdjustmentReason: Option[String])

object ChargeHistory extends JsonUtils {
  implicit val format: OFormat[ChargeHistory] = Json.format[ChargeHistory]
}

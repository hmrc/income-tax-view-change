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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, Reads, Writes, __}

case class ChargeHistoryDetails(idType: String,
                                idValue: String,
                                regimeType: String,
                                chargeHistoryDetails: Option[List[ChargeHistory]])

object ChargeHistoryDetails {
  implicit val writes: Writes[ChargeHistoryDetails] = Json.writes[ChargeHistoryDetails]

  implicit val reads: Reads[ChargeHistoryDetails] = (
    (__ \ "idType").read[String] and
      (__ \ "idNumber").read[String] and
      (__ \ "regimeType").read[String] and
      (__ \ "chargeHistory").readNullable[List[ChargeHistory]]
  )(ChargeHistoryDetails.apply _)
}

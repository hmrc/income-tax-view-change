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

package models.itsaStatus

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, Reads, Writes, __}

case class StatusDetail(submittedOn: String,
                        status: String,
                        statusReason: String,
                        businessIncomePriorTo2Years: Option[BigDecimal] = None)

object StatusDetail {

  val statusMapping = Map(
    "00" -> "No Status",
    "01" -> "MTD Mandated",
    "02" -> "MTD Voluntary",
    "03" -> "Annual",
    "04" -> "Non Digital",
    "05" -> "Dormant",
    "99" -> "MTD Exempt"
  )

  val statusReasonMapping = Map(
    "00" -> "Sign up - return available",
    "01" -> "Sign up - no return available",
    "02" -> "ITSA final declaration",
    "03" -> "ITSA Q4 declaration",
    "04" -> "CESA SA return",
    "05" -> "Complex",
    "06" -> "Ceased income source",
    "07" -> "Reinstated income source",
    "08" -> "Rollover",
    "09" -> "Income Source Latency Changes",
    "10" -> "MTD ITSA Opt-Out",
    "11" -> "MTD ITSA Opt-In",
    "12" -> "Digitally Exempt"
  )

  val convertStatusKeyToStatus: Reads[String] = {
    implicitly[Reads[String]].map(inputStatus => statusMapping.getOrElse(inputStatus, inputStatus))
  }
  val convertStatusReasonKeyToStatusReason: Reads[String] = {
    implicitly[Reads[String]].map(inputStatusReason => statusReasonMapping.getOrElse(inputStatusReason, inputStatusReason))
  }
  implicit val readsStatusDetail: Reads[StatusDetail] = {
    ((__ \ "submittedOn").read[String] and
      (__ \ "status").read[String](convertStatusKeyToStatus) and
      (__ \ "statusReason").read[String](convertStatusReasonKeyToStatusReason) and
      (__ \ "businessIncomePriorTo2Years").readNullable[BigDecimal]
      )(StatusDetail.apply _)
  }

  implicit val writesStatusDetail: Writes[StatusDetail] = Json.writes[StatusDetail]
}

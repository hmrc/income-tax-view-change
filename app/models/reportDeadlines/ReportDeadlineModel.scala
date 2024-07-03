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
package models.reportDeadlines

import models.reportDeadlines.ObligationStatus.{Fulfilled, Open}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class ObligationStatus(code: String, name: String)

object ObligationStatus {
  val Open = ObligationStatus("O", "Open")
  val Fulfilled = ObligationStatus("F", "Fulfilled")
}

case class ReportDeadlineModel(start: LocalDate,
                               end: LocalDate,
                               due: LocalDate,
                               periodKey: String,
                               dateReceived: Option[LocalDate],
                               obligationType: String,
                               status: String
                              )

object ReportDeadlineModel {

  def desReadsApi(incomeSourceType: String): Reads[ReportDeadlineModel] = (
    (__ \ "inboundCorrespondenceFromDate").read[LocalDate] and
      (__ \ "inboundCorrespondenceToDate").read[LocalDate] and
      (__ \ "inboundCorrespondenceDueDate").read[LocalDate] and
      (__ \ "periodKey").read[String] and
      (__ \ "inboundCorrespondenceDateReceived").readNullable[LocalDate] and
      (incomeSourceType match {
        case "ITSA" => Reads.pure("Crystallised")
        case _ => (__ \ "periodKey").read[String].map {
          case "EOPS" => "EOPS"
          case _ => "Quarterly"
        }
      }) and
      (__ \ "status").read[String].map {
        case Open.code => Open.name
        case Fulfilled.code => Fulfilled.name
        case v => v
      }
    )(ReportDeadlineModel.apply _)

  implicit val format: Format[ReportDeadlineModel] = Json.format[ReportDeadlineModel]
}

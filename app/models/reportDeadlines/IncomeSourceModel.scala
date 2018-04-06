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

package models.reportDeadlines

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, Json, Reads, __}

case class IncomeSourceModel(incomeSourceId: String,
                             propOrBiz: String,
                             obligationDetails: Seq[ReportDeadlineModel])

object IncomeSourceModel {

  val desReadsApi1330: Reads[IncomeSourceModel] = (
    (__ \ "identification" \\ "referenceNumber").read[String] and
      (__ \ "identification" \\ "incomeSourceType").read[String] and
      (__ \ "obligationDetails").read(Reads.seq(ReportDeadlineModel.desReadsApi1330)).map(Seq[ReportDeadlineModel])
    )(IncomeSourceModel.applyWithFields _)

  def applyWithFields(incomeSourceId: String,
                      propOrBiz: String,
                      obligationDetails: Seq[ReportDeadlineModel]): IncomeSourceModel = {
    propOrBiz match {
      case prop if prop == "ITSP" => IncomeSourceModel(incomeSourceId, "Prop", obligationDetails)
      case biz if biz == "ITSB" => IncomeSourceModel(incomeSourceId, "Biz", obligationDetails)
      case _ => IncomeSourceModel("", "not_required", Seq()) //Find better way to ignore this
    }
  }

  implicit val format: Format[IncomeSourceModel] = Json.format[IncomeSourceModel]
}

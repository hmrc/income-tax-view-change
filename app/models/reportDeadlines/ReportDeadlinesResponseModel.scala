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

import play.api.libs.json.{Format, Json, Reads, __}

sealed trait ReportDeadlinesResponseModel

case class ReportDeadlinesModel(obligations: Seq[IncomeSourceModel]) extends ReportDeadlinesResponseModel

case class ReportDeadlinesErrorModel(status: Int, reason: String) extends ReportDeadlinesResponseModel

object ReportDeadlinesModel {

  val desReadsApi1330: Reads[ReportDeadlinesModel] =
    (__ \ "obligations").read(Reads.seq(IncomeSourceModel.desReadsApi1330)).map(ReportDeadlinesModel(_))

  implicit val format: Format[ReportDeadlinesModel] = Json.format[ReportDeadlinesModel]
}

object ReportDeadlinesErrorModel {
  implicit val format: Format[ReportDeadlinesErrorModel] = Json.format[ReportDeadlinesErrorModel]
}

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

package models.reportDeadlines

import play.api.libs.json.{Format, Json, Reads, __}
import play.api.libs.functional.syntax._

case class ReportDeadlinesModel(identification: String, obligations: Seq[ReportDeadlineModel])

object ReportDeadlinesModel {

  val desReadsApi1330: Reads[ReportDeadlinesModel] = (

    (__ \\ "identification" \\ "referenceNumber").read[String] and
      (__ \\ "identification" \\ "incomeSourceType").read[String].flatMap { incomeSourceType =>
        (__ \\ "obligationDetails").read(
          Reads.seq(
            ReportDeadlineModel.desReadsApi(incomeSourceType)
          )
        )
      }
    ) (ReportDeadlinesModel.apply _)

  implicit val format: Format[ReportDeadlinesModel] = Json.format[ReportDeadlinesModel]
}

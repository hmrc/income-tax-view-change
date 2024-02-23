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

package models.repaymentHistory

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate
case class RepaymentHistory(amountApprovedforRepayment: Option[BigDecimal],
                            amountRequested: BigDecimal,
                            repaymentMethod: Option[String],
                            totalRepaymentAmount: Option[BigDecimal],
                            repaymentItems: Option[Seq[RepaymentItem]],
                            estimatedRepaymentDate: Option[LocalDate],
                            creationDate: Option[LocalDate],
                            repaymentRequestNumber: String,
                            status: String
                           )

object RepaymentHistory {

  implicit val reads: Reads[RepaymentHistory] = (
    (__ \ "amountApprovedforRepayment").readNullable[BigDecimal] and
      (__ \ "amountRequested").read[BigDecimal] and
      (__ \ "repaymentMethod").readNullable[String] and
      (__ \ "totalRepaymentAmount").readNullable[BigDecimal] and
      (__ \ "repaymentItems").readNullable[Seq[RepaymentItem]].map {
        case Some(seq) => Some(seq.filter(_.repaymentSupplementItem.nonEmpty)).filter(_.nonEmpty) // repaymentSupplementItem(s) or None
        case None => None
      } and
      (__ \ "estimatedRepaymentDate").readNullable[LocalDate] and
      (__ \ "creationDate").readNullable[LocalDate] and
      (__ \ "repaymentRequestNumber").read[String] and
      (__ \ "status").read[String]
    ) (RepaymentHistory.apply _)

  implicit val writes: OWrites[RepaymentHistory] = Json.writes[RepaymentHistory]

}
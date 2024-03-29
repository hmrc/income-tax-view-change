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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

import java.time.LocalDate

case class RepaymentSupplementItem(parentCreditReference: Option[String],
                                   amount: Option[BigDecimal],
                                   fromDate: Option[LocalDate],
                                   toDate: Option[LocalDate],
                                   rate: Option[BigDecimal]
                                  )

object RepaymentSupplementItem {

  implicit val reads: Reads[RepaymentSupplementItem] = (
    (JsPath \ "parentCreditReference").readNullable[String] and
      (JsPath \ "amount").readNullable[BigDecimal] and
      (JsPath \ "fromDate").readNullable[LocalDate] and
      (JsPath \ "toDate").readNullable[LocalDate] and
      (JsPath \ "rate").readNullable[BigDecimal]
    )(RepaymentSupplementItem.apply _)

  implicit val writes: OWrites[RepaymentSupplementItem] = Json.writes[RepaymentSupplementItem]
}

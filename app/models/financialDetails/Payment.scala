/*
 * Copyright 2022 HM Revenue & Customs
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

package models.financialDetails

import play.api.libs.json._

import java.time.LocalDate

case class Payment(reference: Option[String],
                   amount: Option[BigDecimal],
                   outstandingAmount: Option[BigDecimal],
                   documentDescription: Option[String],
                   method: Option[String],
                   lot: Option[String],
                   lotItem: Option[String],
                   date: Option[LocalDate],
                   transactionId: String)

object Payment {
  implicit val format: Format[Payment] = Json.format[Payment]
}

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

package models.paymentAllocations

import models.readNullable
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OWrites, Reads, __}

import java.time.LocalDate

case class AllocationDetail(transactionId: Option[String],
                            from: Option[LocalDate],
                            to: Option[LocalDate],
                            chargeType: Option[String],
                            mainType: Option[String],
                            amount: Option[BigDecimal],
                            clearedAmount: Option[BigDecimal],
                            chargeReference: Option[String]
                           )

object AllocationDetail {

  val emptyAllocation: AllocationDetail = AllocationDetail(None, None, None, None, None, None, None, None)

  implicit val reads: Reads[AllocationDetail] = (
    readNullable[String](__ \ "sapDocNumber") and
      readNullable[LocalDate](__ \ "taxPeriodStartDate") and
      readNullable[LocalDate](__ \ "taxPeriodEndDate") and
      readNullable[String](__ \ "chargeType") and
      readNullable[String](__ \ "mainType") and
      readNullable[BigDecimal](__ \ "amount") and
      readNullable[BigDecimal](__ \ "clearedAmount") and
      readNullable[String](__ \ "chargeReference")
    ) (AllocationDetail.apply _)

  implicit val writes: OWrites[AllocationDetail] = Json.writes[AllocationDetail]
}

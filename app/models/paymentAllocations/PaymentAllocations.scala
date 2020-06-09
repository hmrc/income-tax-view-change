/*
 * Copyright 2020 HM Revenue & Customs
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

import models.{readNullable, readNullableSeq}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OWrites, Reads, __}

case class PaymentAllocations(amount: Option[BigDecimal],
                              method: Option[String],
                              transactionDate: Option[String],
                              allocations: Seq[AllocationDetail])


object PaymentAllocations {

  implicit val reads: Reads[PaymentAllocations] = (
    readNullable[BigDecimal](__ \ "paymentDetails" \\ "paymentAmount") and
      readNullable[String](__ \ "paymentDetails" \\ "paymentMethod") and
      readNullable[String](__ \ "paymentDetails" \\ "valueDate") and
      readNullableSeq[AllocationDetail](__ \ "paymentDetails" \\ "sapClearingDocsDetails")
        .map(_.filterNot(_ == AllocationDetail.emptyAllocation))
    ) (PaymentAllocations.apply _)

  implicit val writes: OWrites[PaymentAllocations] = Json.writes[PaymentAllocations]

}

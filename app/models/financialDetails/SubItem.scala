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

package models.financialDetails

import play.api.Logging
import play.api.libs.json._

import java.time.LocalDate

case class SubItem(subItemId: Option[String],
                   amount: Option[BigDecimal],
                   clearingDate: Option[LocalDate],
                   clearingReason: Option[String],
                   outgoingPaymentMethod: Option[String],
                   interestLock: Option[String],
                   dunningLock: Option[String],
                   paymentReference: Option[String] = None,
                   paymentAmount: Option[BigDecimal],
                   dueDate: Option[LocalDate],
                   paymentMethod: Option[String],
                   paymentLot: Option[String],
                   paymentLotItem: Option[String],
                   paymentId: Option[String])

object SubItem extends Logging {

  val empty: SubItem = SubItem(None, None, None, None, None, None, None, None, None, None, None, None, None, None)

  implicit val writes: OWrites[SubItem] = Json.writes[SubItem]

  implicit val reads: Reads[SubItem] = for {
    subItemId <- (JsPath \ "subItem").readNullable[String](Reads.of[String].filter(subItemJsonError)(isIntString))
    amount <- (JsPath \ "amount").readNullable[BigDecimal]
    clearingDate <- (JsPath \ "clearingDate").readNullable[LocalDate]
    clearingReason <- (JsPath \ "clearingReason").readNullable[String]
    outgoingPaymentMethod <- (JsPath \ "outgoingPaymentMethod").readNullable[String]
    interestLock <- (JsPath \ "interestLock").readNullable[String]
    dunningLock <- (JsPath \ "dunningLock").readNullable[String]
    paymentReference <- (JsPath \ "paymentReference").readNullable[String]
    paymentAmount <- (JsPath \ "paymentAmount").readNullable[BigDecimal]
    dueDate <- (JsPath \ "dueDate").readNullable[LocalDate]
    paymentMethod <- (JsPath \ "paymentMethod").readNullable[String]
    paymentLot <- (JsPath \ "paymentLot").readNullable[String]
    paymentLotItem <- (JsPath \ "paymentLotItem").readNullable[String]

  } yield {
    val id: Option[String] = for {
      pl <- paymentLot
      pli <- paymentLotItem
    } yield s"$pl-$pli"
    SubItem(
      subItemId,
      amount,
      clearingDate,
      clearingReason,
      outgoingPaymentMethod,
      interestLock,
      dunningLock,
      paymentReference,
      paymentAmount,
      dueDate,
      paymentMethod,
      paymentLot,
      paymentLotItem,
      id
    )
  }

  private def isIntString(s: String): Boolean = {
    try {
      s.toInt
      true
    } catch {
      case _: Exception =>
        logger.warn(s"[SubItem][reads] The returned 'subItem' field <$s> could not be parsed as an integer")
        false
    }
  }

  private def subItemJsonError: JsonValidationError = JsonValidationError(
    message = "The field 'subItem' should be parsable as an integer"
  )
}

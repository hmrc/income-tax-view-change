/*
 * Copyright 2025 HM Revenue & Customs
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

package models.financialDetails.hip.model

import play.api.Logging
import play.api.libs.json.{JsPath, Json, JsonValidationError, OWrites, Reads}

import java.time.LocalDate


case class SubItemHip(
                       /* Custom identifier attribute */
                       subItemId: Option[String] = None, // renamed
                       amount: Option[BigDecimal] = None,
                       clearingDate: Option[LocalDate] = None,
                       /* Clearing Reason */
                       clearingReason: Option[String] = None,
                       /* Clearing SAP Document */
                       clearingSAPDocument: Option[String] = None,
                       /* Outgoing payment method */
                       outgoingPaymentMethod: Option[String] = None,
                       /* Interest Lock */
                       interestLock: Option[String] = None,
                       /* Dunning Lock */
                       dunningLock: Option[String] = None,
                       /* Payment Reference */
                       paymentReference: Option[String] = None,
                       /* Currency amount. 13-digits total with 2 decimal places */
                       paymentAmount: Option[BigDecimal] = None,
                       dueDate: Option[LocalDate] = None,
                       /* Payment Method */
                       paymentMethod: Option[String] = None,
                       /* Payment Lot */
                       paymentLot: Option[String] = None,
                       /* Payment Lot Item */
                       paymentLotItem: Option[String] = None,

                       /* Sub Item */
                       subItem: Option[String] = None,

                       /* Currency amount. 13-digits total with 2 decimal places */
                       /* Payment Lock */
                       //paymentLock: Option[String] = None,
                       /* Clearing Lock */
                       //clearingLock: Option[String] = None,
                       /* Return Lock */
                       //returnFlag: Option[String] = None,
                       //codingInitiationDate: Option[LocalDate] = None,
                       /* Statistical Document */
                       //statisticalDocument: Option[String] = None,
                       /* Return reason */
                       //returnReason: Option[String] = None,
                       /* Promise to Pay */
                       //promisetoPay: Option[String] = None,
                       /* Coded Out Status */
                       //codedOutStatus: Option[String] = None
                       paymentId: Option[String] = None, // custom field
                     )

object SubItemHip extends Logging {

  val empty: SubItemHip = SubItemHip(None, None, None, None, None, None, None,
    None, None, None, None, None, None, None, None)

  implicit val writes: OWrites[SubItemHip] = Json.writes[SubItemHip]

  implicit val reads: Reads[SubItemHip] = for {
    subItemId <- (JsPath \ "subItem").readNullable[String](Reads.of[String].filter(subItemJsonError)(isIntString))
    amount <- (JsPath \ "amount").readNullable[BigDecimal]
    clearingDate <- (JsPath \ "clearingDate").readNullable[LocalDate]
    clearingReason <- (JsPath \ "clearingReason").readNullable[String]
    clearingSAPDocument <- (JsPath \ "clearingSAPDocument").readNullable[String]
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
    val paymentId: Option[String] = for {
      pl <- paymentLot
      pli <- paymentLotItem
    } yield s"$pl-$pli"
    SubItemHip(
      subItem = subItemId,
      dueDate = dueDate,
      amount = amount,
      clearingDate = clearingDate,
      clearingReason = clearingReason,
      clearingSAPDocument = clearingSAPDocument,
      outgoingPaymentMethod = outgoingPaymentMethod,
      interestLock = interestLock,
      dunningLock = dunningLock,
      paymentReference = paymentReference,
      paymentAmount = paymentAmount,
      paymentMethod = paymentMethod,
      subItemId = subItemId,
      paymentId = paymentId
    )
  }

  private def isIntString(s: String): Boolean = {
    try {
      s.toInt
      true
    } catch {
      case _: Exception =>
        logger.warn(s"The returned 'subItem' field <$s> could not be parsed as an integer")
        false
    }
  }

  private def subItemJsonError: JsonValidationError = JsonValidationError(
    message = "The field 'subItem' should be parsable as an integer"
  )
}


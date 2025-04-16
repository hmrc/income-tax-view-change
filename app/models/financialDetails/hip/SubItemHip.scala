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

import java.math.BigDecimal

import java.time.LocalDate


case class SubItemHip(
  /* Sub Item */
  subItem: Option[String] = None,
  dueDate: Option[LocalDate] = None,
  /* Currency amount. 13-digits total with 2 decimal places */
  amount: Option[BigDecimal] = None,
  clearingDate: Option[LocalDate] = None,
  /* Clearing Reason */
  clearingReason: Option[String] = None,
  /* Outgoing payment method */
  outgoingPaymentMethod: Option[String] = None,
  /* Payment Lock */
  paymentLock: Option[String] = None,
  /* Clearing Lock */
  clearingLock: Option[String] = None,
  /* Interest Lock */
  interestLock: Option[String] = None,
  /* Dunning Lock */
  dunningLock: Option[String] = None,
  /* Return Lock */
  returnFlag: Option[String] = None,
  /* Payment Reference */
  paymentReference: Option[String] = None,
  /* Currency amount. 13-digits total with 2 decimal places */
  paymentAmount: Option[BigDecimal] = None,
  /* Payment Method */
  paymentMethod: Option[String] = None,
  /* Payment Lot */
  paymentLot: Option[String] = None,
  /* Payment Lot Item */
  paymentLotItem: Option[String] = None,
  /* Clearing SAP Document */
  clearingSAPDocument: Option[String] = None,
  codingInitiationDate: Option[LocalDate] = None,
  /* Statistical Document */
  statisticalDocument: Option[String] = None,
  /* Return reason */
  returnReason: Option[String] = None,
  /* Promise to Pay */
  promisetoPay: Option[String] = None,
  /* Coded Out Status */
  codedOutStatus: Option[String] = None
)


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


import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, Reads, Writes, __}

import java.time.LocalDate


case class BalanceDetailsHip(
                              /* Currency amount. 13-digits total with 2 decimal places */
                              balanceDueWithin30Days: BigDecimal, // => renamed to fit FE
                              nxtPymntDateChrgsDueIn30Days: Option[LocalDate] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              balanceNotDuein30Days: BigDecimal,
                              nextPaymntDateBalnceNotDue: Option[LocalDate] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              overDueAmount: BigDecimal,
                              earlistPymntDateOverDue: Option[LocalDate] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              totalBalance: BigDecimal,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              amountCodedOut: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              totalBCDBalance: Option[BigDecimal] = None,
                              /* BCD balance per year */
                              //bcdBalancePerYear: Option[Seq[Any]] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              unallocatedCredit: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              allocatedCredit: Option[BigDecimal] = None, //not needed anymore after R18, will use allocatedCreditForFutureCharges instead
                              /* Currency amount. 13-digits total with 2 decimal places */
                              totalCredit: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              firstPendingAmountRequested: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              secondPendingAmountRequested: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              totalCreditAvailableForRepayment: Option[BigDecimal] = None,
                              /* Currency amount. 13-digits total with 2 decimal places */
                              allocatedCreditForFutureCharges: Option[BigDecimal] = None //renamed from allocatedCreditForChargesBecomingDueIn30Days
                            )

object BalanceDetailsHip {
  implicit val writes: Writes[BalanceDetailsHip] = Json.writes[BalanceDetailsHip]
  implicit val reads: Reads[BalanceDetailsHip] = (
    (__ \ "balanceDueWithin30days").read[BigDecimal] and
      (__ \ "nxtPymntDateChrgsDueIn30Days").readNullable[LocalDate] and
      (__ \ "balanceNotDuein30Days").read[BigDecimal] and
      (__ \ "nextPaymntDateBalnceNotDue").readNullable[LocalDate] and
      (__ \ "overDueAmount").read[BigDecimal] and
      (__ \ "earlistPymntDateOverDue").readNullable[LocalDate] and
      (__ \ "totalBalance").read[BigDecimal] and
      (__ \ "amountCodedOut").readNullable[BigDecimal] and
      (__ \ "totalBCDBalance").readNullable[BigDecimal] and
      (__ \ "unallocatedCredit").readNullable[BigDecimal] and
      (__ \ "allocatedCredit").readNullable[BigDecimal] and
      (__ \ "totalCredit").readNullable[BigDecimal] and
      (__ \ "firstPendingAmountRequested").readNullable[BigDecimal] and
      (__ \ "secondPendingAmountRequested").readNullable[BigDecimal] and
      (__ \ "totalCreditAvailableForRepayment").readNullable[BigDecimal] and
      (__ \ "allocatedCreditForChargesBecomingDueIn30Days").readNullable[BigDecimal]
    )(BalanceDetailsHip.apply _)

}
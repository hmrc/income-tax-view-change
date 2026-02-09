/*
 * Copyright 2026 HM Revenue & Customs
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

package models.hip.repayments

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

import java.time.{LocalDate, LocalDateTime}

case class SuccessfulRepaymentResponse(
                              transactionHeader: TransactionHeader,
                              responseDetails: ResponseDetails
                            )

object SuccessfulRepaymentResponse:
  given Format[SuccessfulRepaymentResponse] = (
    (__ \ "etmp_transaction_header").format[TransactionHeader] and
      (__ \ "etmp_Response_Details").format[ResponseDetails]
    )(SuccessfulRepaymentResponse.apply, res => (res.transactionHeader, res.responseDetails))

case class TransactionHeader(
                              status: String,
                              processingDate: LocalDateTime
                            )

object TransactionHeader:
  given Format[TransactionHeader] = Json.format

case class ResponseDetails(
                            repaymentsViewerDetails: Seq[RepaymentViewerDetail]
                          )

object ResponseDetails:
  given Format[ResponseDetails] = Json.format

case class RepaymentViewerDetail(
                                  repaymentRequestNumber: String,
                                  actor: String,
                                  channel: String,
                                  status: String,
                                  amountRequested: BigDecimal,
                                  amountApprovedforRepayment: Option[BigDecimal],
                                  totalAmountforRepaymentSupplement: Option[BigDecimal],
                                  totalRepaymentAmount: Option[BigDecimal],
                                  repaymentMethod: Option[String],
                                  creationDate: Option[LocalDate],
                                  estimatedRepaymentDate: Option[LocalDate],
                                  repaymentItems: Option[Seq[RepaymentItem]]
                                )

object RepaymentViewerDetail:
  given Format[RepaymentViewerDetail] = Json.format

case class RepaymentItem(
                          creditItems: Option[Seq[CreditItem]],
                          paymentItems: Option[Seq[PaymentItem]],
                          creditReasons: Option[Seq[CreditReason]],
                          repaymentSupplementItem: Option[Seq[RepaymentSupplementItem]]
                        )

object RepaymentItem:
  given Format[RepaymentItem] = Json.format

case class CreditItem(
                       creditReference: String,
                       creditChargeName: String,
                       amount: BigDecimal,
                       creationDate: LocalDate,
                       taxYear: String
                     )

object CreditItem:
  given Format[CreditItem] = Json.format

case class PaymentItem(
                        paymentReference: String,
                        amount: BigDecimal,
                        paymentSource: String,
                        edp: LocalDate
                      )

object PaymentItem:
  given Format[PaymentItem] = Json.format

case class CreditReason(
                         creditReference: Option[String],
                         creditReason: String,
                         receivedDate: Option[LocalDate],
                         edp: Option[LocalDate],
                         amount: Option[BigDecimal],
                         originalChargeReduced: Option[String],
                         amendmentDate: Option[LocalDate],
                         taxYear: Option[String]
                       )

object CreditReason:
  given Format[CreditReason] = Json.format

case class RepaymentSupplementItem(
                                    creditReference: Option[String],
                                    parentCreditReference: Option[String],
                                    amount: Option[BigDecimal],
                                    fromDate: Option[LocalDate],
                                    toDate: Option[LocalDate],
                                    rate: Option[BigDecimal]
                                  )

object RepaymentSupplementItem:
  given Format[RepaymentSupplementItem] = Json.format
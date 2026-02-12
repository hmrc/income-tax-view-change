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

object SuccessfulRepaymentResponse {
  given Format[SuccessfulRepaymentResponse] with {
    private val baseFormat: Format[SuccessfulRepaymentResponse] = (
      (__ \ "etmp_transaction_header").format[TransactionHeader] and
        (__ \ "etmp_Response_Details").format[ResponseDetails]
      )(SuccessfulRepaymentResponse.apply, res => (res.transactionHeader, res.responseDetails))

    def reads(json: JsValue): JsResult[SuccessfulRepaymentResponse] = baseFormat.reads(json)
    def writes(o: SuccessfulRepaymentResponse): JsValue = baseFormat.writes(o)
  }
}

case class TransactionHeader(
                              status: String,
                              processingDate: LocalDateTime
                            )

object TransactionHeader {
  given Format[TransactionHeader] with {
    def reads(json: JsValue): JsResult[TransactionHeader] = Json.reads[TransactionHeader].reads(json)
    def writes(o: TransactionHeader): JsValue = Json.writes[TransactionHeader].writes(o)
  }
}

case class ResponseDetails(
                            repaymentsViewerDetails: Seq[RepaymentViewerDetail]
                          )

object ResponseDetails {
  given Format[ResponseDetails] with {
    def reads(json: JsValue): JsResult[ResponseDetails] = Json.reads[ResponseDetails].reads(json)
    def writes(o: ResponseDetails): JsValue = Json.writes[ResponseDetails].writes(o)
  }
}

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

object RepaymentViewerDetail {
  given Format[RepaymentViewerDetail] with {
    def reads(json: JsValue): JsResult[RepaymentViewerDetail] = Json.reads[RepaymentViewerDetail].reads(json)
    def writes(o: RepaymentViewerDetail): JsValue = Json.writes[RepaymentViewerDetail].writes(o)
  }
}

case class RepaymentItem(
                          creditItems: Option[Seq[CreditItem]],
                          paymentItems: Option[Seq[PaymentItem]],
                          creditReasons: Option[Seq[CreditReason]],
                          repaymentSupplementItem: Option[Seq[RepaymentSupplementItem]]
                        )

object RepaymentItem {
  given Format[RepaymentItem] with {
    def reads(json: JsValue): JsResult[RepaymentItem] = Json.reads[RepaymentItem].reads(json)
    def writes(o: RepaymentItem): JsValue = Json.writes[RepaymentItem].writes(o)
  }
}

case class CreditItem(
                       creditReference: String,
                       creditChargeName: String,
                       amount: BigDecimal,
                       creationDate: LocalDate,
                       taxYear: String
                     )

object CreditItem {
  given Format[CreditItem] with {
    def reads(json: JsValue): JsResult[CreditItem] = Json.reads[CreditItem].reads(json)
    def writes(o: CreditItem): JsValue = Json.writes[CreditItem].writes(o)
  }
}

case class PaymentItem(
                        paymentReference: String,
                        amount: BigDecimal,
                        paymentSource: String,
                        edp: LocalDate
                      )

object PaymentItem {
  given Format[PaymentItem] with {
    def reads(json: JsValue): JsResult[PaymentItem] = Json.reads[PaymentItem].reads(json)
    def writes(o: PaymentItem): JsValue = Json.writes[PaymentItem].writes(o)
  }
}

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

object CreditReason {
  given Format[CreditReason] with {
    def reads(json: JsValue): JsResult[CreditReason] = Json.reads[CreditReason].reads(json)
    def writes(o: CreditReason): JsValue = Json.writes[CreditReason].writes(o)
  }
}

case class RepaymentSupplementItem(
                                    creditReference: Option[String],
                                    parentCreditReference: Option[String],
                                    amount: Option[BigDecimal],
                                    fromDate: Option[LocalDate],
                                    toDate: Option[LocalDate],
                                    rate: Option[BigDecimal]
                                  )

object RepaymentSupplementItem {
  given Format[RepaymentSupplementItem] with {
    def reads(json: JsValue): JsResult[RepaymentSupplementItem] = Json.reads[RepaymentSupplementItem].reads(json)
    def writes(o: RepaymentSupplementItem): JsValue = Json.writes[RepaymentSupplementItem].writes(o)
  }
}
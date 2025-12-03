/*
 * Copyright 2024 HM Revenue & Customs
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

package models.credits

import play.api.libs.json._

sealed trait TransactionType {
  val key: String
}

case object MfaCreditType extends TransactionType {
  override val key = "mfa"
}

case object CutOverCreditType extends TransactionType {
  override val key = "cutOver"
}

case object BalancingChargeCreditType extends TransactionType {
  override val key = "balancingCharge"
}

case object RepaymentInterest extends TransactionType {
  override val key = "repaymentInterest"
}

case object PaymentType extends TransactionType {
  override val key = "payment"
}

case object Repayment extends TransactionType {
  override val key = "refund"
}

case object PoaOneReconciliationCredit extends TransactionType {
  override val key = "POA1RR-credit"
}

case object PoaTwoReconciliationCredit extends TransactionType {
  override val key = "POA2RR-credit"
}

case object ITSAReturnAmendmentCredit extends TransactionType {
  override val key = "IRA-credit"
}

object TransactionType {

  implicit val write: Writes[TransactionType] = new Writes[TransactionType] {
    def writes(transactionType: TransactionType): JsValue = {
      JsString(transactionType.key)
    }
  }

  val read: Reads[TransactionType] = JsPath.read[String].collect(JsonValidationError("Could not parse transactionType")) {
    case MfaCreditType.key => MfaCreditType
    case CutOverCreditType.key => CutOverCreditType
    case BalancingChargeCreditType.key => BalancingChargeCreditType
    case RepaymentInterest.key => RepaymentInterest
    case PaymentType.key => PaymentType
    case Repayment.key => Repayment
    case PoaOneReconciliationCredit.key => PoaOneReconciliationCredit
    case PoaTwoReconciliationCredit.key => PoaTwoReconciliationCredit
    case ITSAReturnAmendmentCredit.key => ITSAReturnAmendmentCredit
  }

  implicit val format: Format[TransactionType] = Format( read, write)

  // values come from EPID #1138
  private val cutOver = "6110"
  private val balancingCharge = "4905"
  private val repaymentInterest = "6020"
  private val poaOneReconciliationCredit = "4912"
  private val poaTwoReconciliationCredit = "4914"
  private val returnAmendmentCredit = "4916"
  private val mfaCredit = Range.inclusive(4004, 4025)
    .filterNot(_ == 4010).filterNot(_ == 4020).map(_.toString)
    .toList
  private val payment = List("0060")

  def fromCode(mainTransaction: String): Option[TransactionType] = {
    mainTransaction match {
      case TransactionType.cutOver =>
        Some(CutOverCreditType)
      case TransactionType.balancingCharge =>
        Some(BalancingChargeCreditType)
      case TransactionType.repaymentInterest =>
        Some(RepaymentInterest)
      case TransactionType.poaOneReconciliationCredit =>
        Some(PoaOneReconciliationCredit)
      case TransactionType.poaTwoReconciliationCredit =>
        Some(PoaTwoReconciliationCredit)
      case TransactionType.returnAmendmentCredit =>
        Some(ITSAReturnAmendmentCredit)
      case x if mfaCredit.contains(x) =>
        Some(MfaCreditType)
      case x if payment.contains(x) =>
        Some(PaymentType)
      case _ => None
    }
  }
}

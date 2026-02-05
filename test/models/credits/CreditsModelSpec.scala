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

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsArray, JsNumber, JsString, Json}
import utils.{AChargesResponse, TestSupport}

import java.time.LocalDate

class CreditsModelSpec extends TestSupport with Matchers {

  "format" should {

    "serialize to JSON" in {

      val chargesResponse = AChargesResponse()
        .withAvailableCredit(200.0)
        .withAllocatedFutureCredit(300.00)
        .withUnallocatedCredit(250.0)
        .withTotalCredit(150.0)
        .withFirstRefundRequest(200.0)
        .withSecondRefundRequest(100.0)
        .withCutoverCredit("CUTOVER01", LocalDate.of(2024, 6, 20), -100.0)
        .withBalancingChargeCredit("BALANCING01", LocalDate.of(2024, 6, 19), -200)
        .withMfaCredit("MFA01", LocalDate.of(2024, 6, 18), -300)
        .withPayment("PAYMENT01", LocalDate.of(2024, 6, 17), LocalDate.of(2024, 6, 17), -400)
        .withRepaymentInterest("INTEREST01", LocalDate.of(2024, 6, 16), -500)
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      val json = Json.toJson(creditsModel)

      (json \ "availableCreditForRepayment").get shouldBe JsNumber(200.0)
      (json \ "allocatedCreditForFutureCharges").get shouldBe JsNumber(300.0)
      (json \ "unallocatedCredit").get shouldBe JsNumber(250.0)
      (json \ "totalCredit").get shouldBe JsNumber(150.0)
      (json \ "firstPendingAmountRequested").get shouldBe JsNumber(200)
      (json \ "secondPendingAmountRequested").get shouldBe JsNumber(100)

      val transactions = (json \ "transactions").get
      transactions match {
        case r: JsArray =>
          r.value.size shouldBe 7
        case _ => fail("transactions should be JsArray")
      }

      (json \ "transactions" \ 0 \ "transactionType").get shouldBe JsString("cutOver")
      (json \ "transactions" \ 1 \ "transactionType").get shouldBe JsString("balancingCharge")
      (json \ "transactions" \ 2 \ "transactionType").get shouldBe JsString("mfa")
      (json \ "transactions" \ 3 \ "transactionType").get shouldBe JsString("payment")
      (json \ "transactions" \ 4 \ "transactionType").get shouldBe JsString("repaymentInterest")
      (json \ "transactions" \ 5 \ "transactionType").get shouldBe JsString("refund")
      (json \ "transactions" \ 6 \ "transactionType").get shouldBe JsString("refund")
    }
  }

  "fromChargesResponse" should {

    "parse document details and financial documents as transactions" in {

      val chargesResponse = AChargesResponse()
        .withCutoverCredit("CUTOVER01", LocalDate.of(2024, 6, 20), -100.0)
        .withBalancingChargeCredit("BALANCING01", LocalDate.of(2024, 6, 19), -200)
        .withMfaCredit("MFA01", LocalDate.of(2024, 6, 18), -300)
        .withPayment("PAYMENT01", LocalDate.of(2024, 6, 17), LocalDate.of(2024, 6, 17), -400)
        .withRepaymentInterest("INTEREST01", LocalDate.of(2024, 6, 16), -500)
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      creditsModel.transactions.size shouldBe 5
      creditsModel.transactions.head shouldBe Transaction(CutOverCreditType, 100.0, Some(2024), Some(LocalDate.of(2024, 6, 20)), None, "CUTOVER01")
      creditsModel.transactions(1) shouldBe Transaction(BalancingChargeCreditType, 200.0, Some(2024), Some(LocalDate.of(2024, 6, 19)), None, "BALANCING01")
      creditsModel.transactions(2) shouldBe Transaction(MfaCreditType, 300.0, Some(2024), Some(LocalDate.of(2024, 6, 18)), None, "MFA01")
      creditsModel.transactions(3) shouldBe Transaction(PaymentType, 400.0, Some(2024), Some(LocalDate.of(2024, 6, 17)), Some(LocalDate.of(2024, 6, 17)), "PAYMENT01")
      creditsModel.transactions(4) shouldBe Transaction(RepaymentInterest, 500.0, Some(2024), Some(LocalDate.of(2024, 6, 16)), None, "INTEREST01")
    }

    "parse refund requests as transactions" in {
      val chargesResponse = AChargesResponse()
        .withFirstRefundRequest(200.0)
        .withSecondRefundRequest(100.0)
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      creditsModel.transactions.size shouldBe 2
      creditsModel.transactions.foreach(t => t.transactionType shouldBe Repayment)
      creditsModel.transactions.head.amount shouldBe 200.0
      creditsModel.transactions(1).amount shouldBe 100.0
      creditsModel.transactions.foreach(t => t.transactionType shouldBe Repayment)
    }

    "convert negative amounts of outstanding amount (i.e. credit) to absolute values" in {

      val chargesResponse = AChargesResponse()
        .withCutoverCredit("CUTOVER01", LocalDate.of(2024, 6, 20), -100.0)
        .withBalancingChargeCredit("BALANCING01", LocalDate.of(2024, 6, 19), -200)
        .withMfaCredit("MFA01", LocalDate.of(2024, 6, 18), -300)
        .withPayment("PAYMENT01", LocalDate.of(2024, 6, 17), LocalDate.of(2024, 6, 17), -400)
        .withRepaymentInterest("INTEREST01", LocalDate.of(2024, 6, 16), -500)
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      creditsModel.transactions.size shouldBe 5
      creditsModel.transactions.forall(t => t.amount > 0) shouldBe true
    }

    // existing behaviour moved from frontend
    "clamp positive amounts of outstanding amount to 0" in {

      val chargesResponse = AChargesResponse()
        .withCutoverCredit("CUTOVER01", LocalDate.of(2024, 6, 20), 100.0)
        .withBalancingChargeCredit("BALANCING01", LocalDate.of(2024, 6, 19), 200)
        .withMfaCredit("MFA01", LocalDate.of(2024, 6, 18), 300)
        .withPayment("PAYMENT01", LocalDate.of(2024, 6, 17), LocalDate.of(2024, 6, 17), 400)
        .withRepaymentInterest("INTEREST01", LocalDate.of(2024, 6, 16), 500)
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      creditsModel.transactions.size shouldBe 5
      creditsModel.transactions.forall(t => t.amount == 0) shouldBe true
    }

    "ignore transactions when mainTransaction cannot be converted to creditType" in {
      val chargesResponse = AChargesResponse()
        .withCustomMainTransaction(
          transactionId = "ID1",
          dueDate = LocalDate.of(2024, 6, 20),
          outstandingAmount = -100.0,
          mainType = Some("ITSA Cutover Credits"),
          mainTransaction = Some("!!!!"))
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      creditsModel.transactions.isEmpty shouldBe true
    }

    "ignore transactions without a credit mainTransaction" in {
      val chargesResponse = AChargesResponse()
        .withCustomMainTransaction(
          transactionId = "ID1",
          dueDate = LocalDate.of(2024, 6, 20),
          outstandingAmount = -100.0,
          mainType = None,
          mainTransaction = None)
        .get()

      val creditsModel = CreditsModel.fromHipChargesResponse(chargesResponse)

      creditsModel.transactions.isEmpty shouldBe true
    }
  }
}

/*
 * Copyright 2021 HM Revenue & Customs
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

package models.financialDetails.responses

import models.financialDetails.{BalanceDetails, DocumentDetail, FinancialDetail, Payment, SubItem}
import org.scalatest.{Matchers, WordSpec}

class ChargesResponseSpec extends WordSpec with Matchers {

	val balanceDetails: BalanceDetails = BalanceDetails(100.00, 200.00, 300.00)

	def document(documentId: String = "DOCID01",
							 paymentLot: Option[String] = Some("lot01"),
							 paymentLotItem: Option[String] = Some("item01")): DocumentDetail = {
		DocumentDetail(
			taxYear = "2018",
			documentDescription = None,
			originalAmount = Some(1000),
			outstandingAmount = Some(700),
			documentDate = "date",
			interestRate = None,
			interestFromDate = None,
			interestEndDate = None,
			latePaymentInterestAmount = None,
			interestOutstandingAmount = None,
			transactionId = documentId,
			paymentLot = paymentLot,
			paymentLotItem = paymentLotItem
		)
	}

	def financial(documentId: String = "DOCID01", items: Option[List[SubItem]] = None): FinancialDetail = {
		FinancialDetail(
			taxYear = "2018",
			transactionId = documentId,
			transactionDate = None,
			`type` = None,
			totalAmount = None,
			originalAmount = None,
			outstandingAmount = None,
			clearedAmount = None,
			chargeType = None,
			mainType = None,
			accruedInterest = None,
			items = items
		)
	}


	def subItem(paymentReference: Option[String] = Some("ref"),
							paymentLot: Option[String] = Some("lot01"),
							paymentLotItem: Option[String] = Some("item01")): SubItem = {
		SubItem(
			subItemId = None,
			amount = Some(1000.0),
			clearingDate = None,
			clearingReason = None,
			outgoingPaymentMethod = None,
			interestLock = Some("interestLock"),
			dunningLock = Some("dunningLock"),
			paymentReference = paymentReference,
			paymentAmount = Some(400.0),
			dueDate = Some("dueDate"),
			paymentMethod = Some("method"),
			paymentLot = paymentLot,
			paymentLotItem = paymentLotItem,
			paymentId = Some("paymentId")
		)
	}

	"ChargesResponse" when {

		"calling .payments" should {

			"return no payments" when {

				"no documents exist with a paymentLot and paymentLotId" in {
					ChargesResponse(balanceDetails, List(document(paymentLot = None, paymentLotItem = None)), List(financial())).payments shouldBe List()
				}

				"a payment document exists with no matching financial details" in {
					ChargesResponse(balanceDetails, List(document()), List(financial(documentId = "DOCID02"))).payments shouldBe List()
				}

				"a payment document exists with a matching financial details but no matching items" in {
					ChargesResponse(balanceDetails, List(document()), List(financial(items = Some(List(subItem(paymentLot = Some("lot02"))))))).payments shouldBe List()
				}

				"a payment document exists with matching financial details but missing data" in {
					ChargesResponse(balanceDetails, List(document()), List(financial(items = Some(List(subItem(paymentReference = None)))))).payments shouldBe List()
				}
			}

			"return payments" when {

				"a single payment exists" in {
					ChargesResponse(balanceDetails, List(document()), List(financial(items = Some(List(subItem()))))).payments shouldBe List(
						Payment(Some("ref"), Some(1000.0), Some("method"), Some("lot01"), Some("item01"), Some("dueDate"), "DOCID01")
					)
				}

				"multiple payments exist" in {
					ChargesResponse(balanceDetails, List(document(), document("DOCID02", paymentLot = Some("lot02"))),
						List(financial(items = Some(List(subItem()))), financial("DOCID02", items = Some(List(subItem(paymentLot = Some("lot02"))))))).payments shouldBe List(
						Payment(Some("ref"), Some(1000.0), Some("method"), Some("lot01"), Some("item01"), Some("dueDate"), "DOCID01"),
						Payment(Some("ref"), Some(1000.0), Some("method"), Some("lot02"), Some("item01"), Some("dueDate"), "DOCID02")
					)
				}
			}
		}
	}
}

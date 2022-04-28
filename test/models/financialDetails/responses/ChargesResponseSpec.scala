/*
 * Copyright 2022 HM Revenue & Customs
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

import assets.FinancialDataTestConstants.{documentDetail, financialDetail}
import models.financialDetails.{BalanceDetails, CodingDetails, DocumentDetail, FinancialDetail, Payment, SubItem}
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsSuccess, Json}

class ChargesResponseSpec extends WordSpec with Matchers {

	val balanceDetails: BalanceDetails = BalanceDetails(100.00, 200.00, 300.00, None, None, None, Some(400.00))

  val codingDetails: CodingDetails = CodingDetails(
    taxYearReturn = "2018",
    totalReturnAmount = None,
    amountNotCoded = None,
    amountNotCodedDueDate = None,
    amountCodedOut = 100.00,
    taxYearCoding = "2019"
  )

  val chargeResponseMinWrite = ChargesResponse(balanceDetails, None, List(), List())

  val chargeResponseMinWriteJson = Json.obj(
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30Days" -> 100.00,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00,
      "unallocatedCredit" -> 400.00
    ),
    "documentDetails" -> Json.arr(),
    "financialDetails" -> Json.arr()
  )

  val chargeResponseMinRead = ChargesResponse(balanceDetails, None, List(), List())

  val chargeResponseMinReadJson = Json.obj(
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30Days" -> 100.00,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00,
      "unallocatedCredit" -> 400.00
    )
  )

  val chargeResponseFull = ChargesResponse(balanceDetails = balanceDetails,
    codingDetails = Some(List(CodingDetails(
      taxYearReturn = "2018",
      totalReturnAmount = Some(100.00),
      amountNotCoded = Some(200.00),
      amountNotCodedDueDate = Some("2018-01-01"),
      amountCodedOut = 300.00,
      taxYearCoding = "2019"))),
    documentDetails = List(documentDetail),
    financialDetails = List(financialDetail))

  val chargeResponseFullJson = Json.obj(
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30Days" -> 100.00,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00,
      "unallocatedCredit" -> 400.00
    ),
    "codingDetails" -> Json.arr(Json.obj(
      "taxYearReturn" -> "2018",
      "totalReturnAmount" -> 100.00,
      "amountNotCoded" -> 200.00,
      "amountNotCodedDueDate" -> "2018-01-01",
      "amountCodedOut" -> 300.00,
      "taxYearCoding" -> "2019"
    )),
    "documentDetails" -> Json.arr(Json.obj(
      "taxYear" -> "2018",
      "transactionId" -> "id",
      "documentDescription" -> "documentDescription",
      "documentText" -> "documentText",
      "originalAmount" -> 300.00,
      "outstandingAmount" -> 200.00,
      "documentDate" -> "2018-03-29",
      "interestRate" -> 2.60,
      "interestFromDate" -> "2018-08-01",
      "interestEndDate" -> "2019-01-15",
      "latePaymentInterestId" -> "latePaymentInterestID",
      "latePaymentInterestAmount" -> 12.34,
      "interestOutstandingAmount" -> 31.00,
      "paymentLotItem" -> "paymentLotItem",
      "paymentLot" -> "paymentLot",
      "lpiWithDunningBlock" -> 12.50
    )),
    "financialDetails" -> Json.arr(Json.parse(
      """
			|{
			|     "taxYear": "2018",
			|     "transactionId": "id",
			|     "transactionDate": "transactionDate",
			|     "type": "type",
			|     "totalAmount": 1000.00,
			|     "originalAmount": 500.00,
			|     "outstandingAmount": 500.00,
			|     "clearedAmount": 500.00,
			|     "chargeType": "POA1",
			|     "mainType": "4920",
|     "accruedInterest": 1000,
			|     "items": [{
			|       "subItemId": "1",
			|       "amount": 100.00,
			|       "clearingDate": "clearingDate",
			|       "clearingReason": "clearingReason",
			|       "outgoingPaymentMethod": "outgoingPaymentMethod",
|       "interestLock": "interestLock",
|       "dunningLock": "dunningLock",
			|       "paymentReference": "paymentReference",
			|       "paymentAmount": 2000.00,
			|       "dueDate": "dueDate",
			|       "paymentMethod": "paymentMethod",
			|       "paymentLot": "paymentLot",
			|       "paymentLotItem": "paymentLotItem",
			|       "paymentId": "paymentLot-paymentLotItem"
			|       }
			|     ]
			|}
			|""".stripMargin))
  )

  def document(documentId: String = "DOCID01",
               paymentLot: Option[String] = Some("lot01"),
               paymentLotItem: Option[String] = Some("item01")): DocumentDetail = {
    DocumentDetail(
      taxYear = "2018",
      documentDescription = None,
      documentText = None,
      originalAmount = Some(1000),
      outstandingAmount = Some(700),
      documentDate = "date",
      interestRate = None,
      interestFromDate = None,
      interestEndDate = None,
      latePaymentInterestId = None,
      latePaymentInterestAmount = None,
      interestOutstandingAmount = None,
      transactionId = documentId,
      paymentLot = paymentLot,
      paymentLotItem = paymentLotItem,
      lpiWithDunningBlock = None
    )
  }

  def document2(documentId: String = "DOCID01"): DocumentDetail = {
    DocumentDetail(
      taxYear = "2018",
      documentDescription = None,
      documentText = None,
      originalAmount = Some(-1000),
      outstandingAmount = Some(700),
      documentDate = "date",
      interestRate = None,
      interestFromDate = None,
      interestEndDate = None,
      latePaymentInterestId = None,
      latePaymentInterestAmount = None,
      interestOutstandingAmount = None,
      transactionId = documentId,
      paymentLot = None,
      paymentLotItem = None,
      lpiWithDunningBlock = None
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
          ChargesResponse(balanceDetails = balanceDetails,
            codingDetails = Some(List(codingDetails)),
            documentDetails = List(document(paymentLot = None, paymentLotItem = None)),
            financialDetails = List(financial())
          ).payments shouldBe List()
        }

        "a payment document exists with no matching financial details" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            codingDetails = Some(List(codingDetails)),
            documentDetails = List(document()),
            financialDetails = List(financial(documentId = "DOCID02"))
          ).payments shouldBe List()
        }

        "a payment document exists with a matching financial details but no matching items" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            codingDetails = Some(List(codingDetails)),
            documentDetails = List(document()),
            financialDetails = List(financial(items = Some(List(subItem(paymentLot = Some("lot02"))))))
          ).payments shouldBe List()
        }

        "a payment document exists with matching financial details but missing data" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            codingDetails = Some(List(codingDetails)),
            documentDetails = List(document()),
            financialDetails = List(financial(items = Some(List(subItem(paymentReference = None)))))
          ).payments shouldBe List()
        }
      }

      "return payments" when {

        "a single payment exists" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            codingDetails = Some(List(codingDetails)),
            documentDetails = List(document2()),
            financialDetails = List(financial(items = Some(List(subItem()))))
          ).payments shouldBe List(
            Payment(Some("ref"), Some(-1000.0), Some("method"), None, None, Some("dueDate"), "DOCID01")
          )
        }

        "multiple payments exist" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            codingDetails = Some(List(codingDetails)),
            documentDetails = List(
              document2(),
              document2("DOCID02")),
            financialDetails = List(
              financial(items = Some(List(subItem()))),
              financial("DOCID02", items = Some(List(subItem(paymentLot = Some("lot02"))))))
          ).payments shouldBe List(
            Payment(Some("ref"), Some(-1000.0), Some("method"), None, None, Some("dueDate"), "DOCID01"),
            Payment(Some("ref"), Some(-1000.0), Some("method"), None, None, Some("dueDate"), "DOCID02")
          )
        }
      }
    }

    "read from Json" when {
      "the model has the minimal details" in {
        Json.fromJson[ChargesResponse](chargeResponseMinReadJson) shouldBe JsSuccess(chargeResponseMinRead)
      }
    }
    "write to Json" when {
      "the model has the minimal details" in {
        Json.toJson(chargeResponseMinWrite) shouldBe chargeResponseMinWriteJson
      }
      "the model has the full details" in {
        Json.toJson(chargeResponseFull) shouldBe chargeResponseFullJson
      }
    }
  }
}

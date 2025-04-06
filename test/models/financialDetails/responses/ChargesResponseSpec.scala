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

package models.financialDetails.responses

import constants.FinancialDataTestConstants.{documentDetail, financialDetail}
import models.financialDetails._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json._

import java.time.LocalDate

class ChargesResponseSpec extends AnyWordSpec with Matchers {

  val balanceDetails: BalanceDetails = BalanceDetails(100.00, 200.00, 300.00, None, None, None, None, Some(400.00))

  val chargeResponseMinWrite = ChargesResponse(balanceDetails, List(), List())

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

  val chargeResponseMinRead = ChargesResponse(balanceDetails, List(), List())

  val chargeResponseBadJson = Json.obj(
    "qwer" -> Json.obj(
      "asdf" -> 100.00,
      "qwer2" -> 200.00
    )
  )

  val chargeResponseMinReadJson = Json.obj(
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30Days" -> 100.00,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00,
      "unallocatedCredit" -> 400.00
    )
  )

  val chargeResponseFull = ChargesResponse(balanceDetails = balanceDetails,
    documentDetails = List(documentDetail),
    financialDetails = List(financialDetail))

  val chargeResponseFullJson = Json.obj(
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30Days" -> 100.00,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00,
      "unallocatedCredit" -> 400.00
    ),
    "documentDetails" -> Json.arr(Json.obj(
      "taxYear" -> 2018,
      "transactionId" -> "id",
      "documentDescription" -> "documentDescription",
      "documentText" -> "documentText",
      "originalAmount" -> 300.00,
      "outstandingAmount" -> 200.00,
      "documentDate" -> LocalDate.parse("2018-03-29"),
      "interestRate" -> 2.60,
      "interestFromDate" -> LocalDate.parse("2018-08-01"),
      "interestEndDate" -> LocalDate.parse("2019-01-15"),
      "latePaymentInterestId" -> "latePaymentInterestID",
      "latePaymentInterestAmount" -> 12.34,
      "interestOutstandingAmount" -> 31.00,
      "paymentLotItem" -> "paymentLotItem",
      "paymentLot" -> "paymentLot",
      "lpiWithDunningBlock" -> 12.50,
      "amountCodedOut" -> 3.21,
      "effectiveDateOfPayment" -> LocalDate.parse("2018-03-29"),
      "documentDueDate" -> LocalDate.parse("2019-03-29"),
      "poaRelevantAmount" -> Some(1000.00)
    )),
    "financialDetails" -> Json.arr(Json.parse(
      """{
        |     "taxYear": "2018",
        |     "transactionId": "id",
        |     "transactionDate": "2022-06-23",
        |     "chargeReference": "chargeRef",
        |     "type": "type",
        |     "totalAmount": 1000.00,
        |     "originalAmount": 500.00,
        |     "outstandingAmount": 500.00,
        |     "clearedAmount": 500.00,
        |     "chargeType": "POA1",
        |     "mainType": "4920",
        |     "mainTransaction": "4920",
        |     "accruedInterest": 1000,
        |     "items": [{
        |       "subItemId": "1",
        |       "amount": 100.00,
        |       "clearingDate": "2022-06-23",
        |       "clearingReason": "clearingReason",
        |       "clearingSAPDocument": "012345678912",
        |       "outgoingPaymentMethod": "outgoingPaymentMethod",
        |       "interestLock": "interestLock",
        |       "dunningLock": "dunningLock",
        |       "paymentReference": "paymentReference",
        |       "paymentAmount": 2000.00,
        |       "dueDate": "2022-06-23",
        |       "paymentMethod": "paymentMethod",
        |       "paymentLot": "paymentLot",
        |       "paymentLotItem": "paymentLotItem",
        |       "paymentId": "paymentLot-paymentLotItem"
        |       }
        |     ]
        |}""".stripMargin))
  )

  def document(documentId: String = "DOCID01",
               paymentLot: Option[String] = Some("lot01"),
               paymentLotItem: Option[String] = Some("item01")): DocumentDetail = {
    DocumentDetail(
      taxYear = 2018,
      documentDescription = None,
      documentText = None,
      originalAmount = 1000.00,
      outstandingAmount = 700.00,
      documentDate = LocalDate.parse("2022-06-23"),
      interestRate = None,
      interestFromDate = None,
      interestEndDate = None,
      latePaymentInterestId = None,
      latePaymentInterestAmount = None,
      interestOutstandingAmount = None,
      transactionId = documentId,
      paymentLot = paymentLot,
      paymentLotItem = paymentLotItem,
      lpiWithDunningBlock = None,
      amountCodedOut = Some(3.21),
      effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
      poaRelevantAmount = Some(1000.00)
    )
  }

  def document2(documentId: String = "DOCID01"): DocumentDetail = {
    DocumentDetail(
      taxYear = 2018,
      documentDescription = None,
      documentText = None,
      originalAmount = -1000.00,
      outstandingAmount = 700.00,
      documentDate = LocalDate.parse("2022-06-23"),
      interestRate = None,
      interestFromDate = None,
      interestEndDate = None,
      latePaymentInterestId = None,
      latePaymentInterestAmount = None,
      interestOutstandingAmount = None,
      transactionId = documentId,
      paymentLot = None,
      paymentLotItem = None,
      lpiWithDunningBlock = None,
      amountCodedOut = Some(3.21),
      effectiveDateOfPayment = Some(LocalDate.parse("2018-03-29")),
      poaRelevantAmount = Some(1000.00)
    )
  }

  def financial(documentId: String = "DOCID01", items: Option[List[SubItem]] = None): FinancialDetail = {
    FinancialDetail(
      taxYear = "2018",
      transactionId = documentId,
      transactionDate = None,
      chargeReference = Some("chargeRef"),
      `type` = None,
      totalAmount = None,
      originalAmount = None,
      outstandingAmount = None,
      clearedAmount = None,
      chargeType = None,
      mainType = Some("mainType1"),
      mainTransaction = Some("4920"),
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
      dueDate = Some(LocalDate.parse("2022-06-23")),
      paymentMethod = Some("method"),
      paymentLot = paymentLot,
      paymentLotItem = paymentLotItem,
      paymentId = Some("paymentId"),
      clearingSAPDocument = Some("012345678912")
    )
  }

  "ChargesResponse" when {

    "calling .payments" should {

      "return no payments" when {

        "no documents exist with a paymentLot and paymentLotId" in {
          ChargesResponse(balanceDetails = balanceDetails,
            documentDetails = List(document(paymentLot = None, paymentLotItem = None)),
            financialDetails = List(financial())
          ).payments shouldBe List()
        }

        "a payment document exists with no matching financial details" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            documentDetails = List(document()),
            financialDetails = List(financial(documentId = "DOCID02"))
          ).payments shouldBe List()
        }

        "a payment document exists with a matching financial details but no matching items" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            documentDetails = List(document()),
            financialDetails = List(financial(items = Some(List(subItem(paymentLot = Some("lot02"))))))
          ).payments shouldBe List()
        }

        "a payment document exists with matching financial details but missing data" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            documentDetails = List(document()),
            financialDetails = List(financial(items = Some(List(subItem(paymentReference = None)))))
          ).payments shouldBe List()
        }
      }

      "return payments" when {

        "a single payment exists" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            documentDetails = List(document2()),
            financialDetails = List(financial(items = Some(List(subItem()))))
          ).payments shouldBe List(
            Payment(reference = Some("ref"), amount = -1000.00, outstandingAmount = 700.00,
              documentDescription = None, method = Some("method"), lot = Some("lot01"), lotItem = Some("item01"),
              dueDate = Some(LocalDate.parse("2018-03-29")), documentDate = LocalDate.parse("2022-06-23"),
              transactionId = "DOCID01", mainType = Some("mainType1"), mainTransaction = Some("4920"))
          )
        }

        "multiple payments exist" in {
          ChargesResponse(
            balanceDetails = balanceDetails,
            documentDetails = List(
              document2(),
              document2("DOCID02")),
            financialDetails = List(
              financial(items = Some(List(subItem()))),
              financial("DOCID02", items = Some(List(subItem(paymentLot = Some("lot02"), paymentLotItem = Some("item02"))))))
          ).payments shouldBe List(
            Payment(reference = Some("ref"), amount = -1000.00, outstandingAmount = 700.00,
              documentDescription = None, method = Some("method"), lot = Some("lot01"), lotItem = Some("item01"),
              dueDate = Some(LocalDate.parse("2018-03-29")), documentDate = LocalDate.parse("2022-06-23"),
              transactionId = "DOCID01", mainType = Some("mainType1"), mainTransaction = Some("4920")),
            Payment(reference = Some("ref"), amount = -1000.00, outstandingAmount = 700.00,
              documentDescription = None, method = Some("method"), lot = Some("lot02"), lotItem = Some("item02"),
              dueDate = Some(LocalDate.parse("2018-03-29")), documentDate = LocalDate.parse("2022-06-23"),
              transactionId = "DOCID02", mainType = Some("mainType1"), mainTransaction = Some("4920"))
          )
        }
      }
    }

    "read from Json" when {
      "the model has the minimal details" in {
        Json.fromJson[ChargesResponse](chargeResponseMinReadJson) shouldBe JsSuccess(chargeResponseMinRead)
      }
    }
    "read from bad Json" when {
      "a parse error is generated" in {
        Json.fromJson[ChargesResponse](chargeResponseBadJson) shouldBe JsError(
          List((JsPath \ "balanceDetails", List(JsonValidationError(List("error.path.missing")))
          )))
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

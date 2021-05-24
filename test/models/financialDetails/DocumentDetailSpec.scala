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

package models.financialDetails

import assets.FinancialDataTestConstants._
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, JsSuccess, JsValue, Json}

class DocumentDetailSpec extends WordSpec with Matchers {

	val documentDetailMin: DocumentDetail = DocumentDetail("2019", "id", None, None, None, "2018-03-29")

	val documentDetailMinJsonRead: JsObject = Json.obj("taxYear" -> "2019", "documentId" -> "id", "documentDate" -> "2018-03-29")
	val documentDetailMinJsonWrite: JsObject = Json.obj("taxYear" -> "2019", "transactionId" -> "id", "documentDate" -> "2018-03-29")

	val documentDetailFullJsonRead: JsValue = Json.obj(
		"taxYear" -> "2018",
		"documentId" -> "id",
		"documentDescription" -> "documentDescription",
		"totalAmount" -> 300.00,
		"documentOutstandingAmount" -> 200.00,
		"documentDate" -> "2018-03-29"
	)

	val documentDetailFullJsonWrite: JsValue = Json.obj(
		"taxYear" -> "2018",
		"transactionId" -> "id",
		"documentDescription" -> "documentDescription",
		"originalAmount" -> 300.00,
		"outstandingAmount" -> 200.00,
		"documentDate" -> "2018-03-29"
	)

	"FinancialDetail" should {
		"read from json" when {
			"the json is complete" in {
				Json.fromJson[DocumentDetail](documentDetailFullJsonRead) shouldBe JsSuccess(documentDetail)
			}
			"the json is minimal" in {
				Json.fromJson[DocumentDetail](documentDetailMinJsonRead) shouldBe JsSuccess(documentDetailMin)
			}
		}
		"write to json" when {
			"the model is complete" in {
				Json.toJson(documentDetail) shouldBe documentDetailFullJsonWrite
			}
			"the model is empty" in {
				Json.toJson(documentDetailMin) shouldBe documentDetailMinJsonWrite
			}
		}
	}
}

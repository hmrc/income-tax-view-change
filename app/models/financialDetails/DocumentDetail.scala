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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, Reads, Writes, __}

case class DocumentDetail(taxYear: String,
													transactionId: String,
													documentDescription: Option[String],
													originalAmount: Option[BigDecimal],
													outstandingAmount: Option[BigDecimal],
													documentDate: String,
													interestRate: Option[BigDecimal],
													interestFromDate: Option[String],
													interestEndDate: Option[String],
													latePaymentInterestId: Option[String],
													latePaymentInterestAmount: Option[BigDecimal],
													interestOutstandingAmount: Option[BigDecimal],
													paymentLotItem: Option[String],
													paymentLot: Option[String]
												 )

object DocumentDetail {
	implicit val writes: Writes[DocumentDetail] = Json.writes[DocumentDetail]
	implicit val reads: Reads[DocumentDetail] = (
		(__ \ "taxYear").read[String] and
			(__ \ "documentId").read[String] and
			(__ \ "documentDescription").readNullable[String] and
			(__ \ "totalAmount").readNullable[BigDecimal] and
			(__ \ "documentOutstandingAmount").readNullable[BigDecimal] and
			(__ \ "documentDate").read[String] and
			(__ \ "interestRate").readNullable[BigDecimal] and
			(__ \ "interestFromDate").readNullable[String] and
			(__ \ "interestEndDate").readNullable[String] and
			(__ \ "latePaymentInterestID").readNullable[String] and
			(__ \ "latePaymentInterestAmount").readNullable[BigDecimal] and
			(__ \ "interestOutstandingAmount").readNullable[BigDecimal] and
			(__ \ "paymentLotItem").readNullable[String] and
			(__ \ "paymentLot").readNullable[String]
		) (DocumentDetail.apply _)
}

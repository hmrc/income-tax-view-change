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

package models.incomeSourceDetails

import models.core.{AccountingPeriodModel, AddressModel, CessationModel, ContactDetailsModel, CustomReads}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class BusinessDetailsModel(incomeSourceId: String,
                                incomeSource: Option[String],
                                accountingPeriod: AccountingPeriodModel,
                                tradingName: Option[String],
                                address: Option[AddressModel],
                                contactDetails: Option[ContactDetailsModel],
                                contextualTaxYear: Option[String],
                                tradingStartDate: Option[LocalDate],
                                cashOrAccruals: Boolean,
                                seasonal: Option[Boolean],
                                cessation: Option[CessationModel],
                                paperless: Option[Boolean],
                                firstAccountingPeriodEndDate: Option[LocalDate],
                                latencyDetails: Option[LatencyDetails],
                                quarterTypeElection: Option[QuarterTypeElection])


object BusinessDetailsModel extends CustomReads {

  def applyWithFields(incomeSourceId: String,
                      incomeSource: Option[String],
                      accountingPeriodModel: AccountingPeriodModel,
                      tradingName: Option[String],
                      address: Option[AddressModel],
                      contactDetails: Option[ContactDetailsModel],
                      contextualTaxYear: Option[String],
                      tradingStartDate: Option[LocalDate],
                      cashOrAccruals: Boolean,
                      seasonal: Option[Boolean],
                      cessationDate: Option[LocalDate],
                      cessationReason: Option[String],
                      paperless: Option[Boolean],
                      firstAccountingPeriodEndDate: Option[LocalDate],
                      latencyDetails: Option[LatencyDetails],
                      quarterTypeElection: Option[QuarterTypeElection]): BusinessDetailsModel =
    BusinessDetailsModel(
      incomeSourceId,
      incomeSource,
      accountingPeriodModel,
      tradingName,
      address,
      contactDetails,
      contextualTaxYear,
      tradingStartDate,
      cashOrAccruals,
      seasonal,
      CessationModel.cessation(cessationDate, cessationReason),
      paperless,
      firstAccountingPeriodEndDate,
      latencyDetails,
      quarterTypeElection
    )

  implicit val reads: Reads[BusinessDetailsModel] = (
    (__ \ "incomeSourceId").read[String] and
      (__ \ "incomeSource").readNullable[String] and
      __.read(AccountingPeriodModel.reads) and
      (__ \ "tradingName").readNullable[String] and
      (__ \ "businessAddressDetails").readNullable(AddressModel.reads) and
      (__ \ "businessContactDetails").readNullable(ContactDetailsModel.reads) and
      (__ \ "contextualTaxYear").readNullable[String] and
      (__ \ "tradingStartDate").readNullable[LocalDate] and
      (__ \ "cashOrAccruals").read[Boolean] and
      (__ \ "seasonal").readNullable[Boolean] and
      (__ \ "cessationDate").readNullable[LocalDate] and
      (__ \ "cessationReason").readNullable[String] and
      (__ \ "paperLess").readNullable[Boolean] and
      (__ \ "firstAccountingPeriodEndDate").readNullable[LocalDate] and
      (__ \ "latencyDetails").readNullable[LatencyDetails] and
      (__ \ "quarterTypeElection").readNullable[QuarterTypeElection]
    )(BusinessDetailsModel.applyWithFields _)


  implicit val writes: Writes[BusinessDetailsModel] = Json.writes[BusinessDetailsModel]
}

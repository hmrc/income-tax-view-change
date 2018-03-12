/*
 * Copyright 2018 HM Revenue & Customs
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

package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}

case class BusinessDetailsModel(incomeSourceId: String,
                                accountingPeriod: AccountingPeriodModel,
                                tradingName: Option[String],
                                address: Option[AddressModel],
                                contactDetails: Option[ContactDetailsModel],
                                tradingStartDate: Option[LocalDate],
                                cashOrAccruals: Option[String],
                                seasonal: Option[Boolean],
                                cessation: Option[CessationModel],
                                paperless: Option[Boolean])


object BusinessDetailsModel {

  implicit val reads: Reads[BusinessDetailsModel] = (
    (__ \ "incomeSourceId").read[String] and
      __.read[AccountingPeriodModel] and
      (__ \ "tradingName").readNullable[String] and
      (__ \ "businessAddressDetails").readNullable[AddressModel] and
      (__ \ "businessContactDetails").readNullable[ContactDetailsModel] and
      (__ \ "tradingStartDate").readNullable[LocalDate] and
      (__ \ "cashOrAccruals").readNullable[String] and
      (__ \ "seasonal").readNullable[Boolean] and
      (__ \ "cessationDate").readNullable[LocalDate] and
      (__ \ "cessationReason").readNullable[String] and
      (__ \ "paperless").readNullable[Boolean]
    )(BusinessDetailsModel.applyWithFields _)

  def applyWithFields(incomeSourceId: String,
                    accountingPeriodModel: AccountingPeriodModel,
                    tradingName: Option[String],
                    address: Option[AddressModel],
                    contactDetails: Option[ContactDetailsModel],
                    tradingStartDate: Option[LocalDate],
                    cashOrAccruals: Option[String],
                    seasonal: Option[Boolean],
                    cessationDate: Option[LocalDate],
                    cessationReason: Option[String],
                    paperless: Option[Boolean]): BusinessDetailsModel =
    BusinessDetailsModel(
      incomeSourceId,
      accountingPeriodModel,
      tradingName,
      address,
      contactDetails,
      tradingStartDate,
      cashOrAccruals,
      seasonal,
      CessationModel.cessation(cessationDate, cessationReason),
      paperless
    )

  implicit val writes: Writes[BusinessDetailsModel] = Json.writes[BusinessDetailsModel]


}

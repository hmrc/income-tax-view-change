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

import java.time.LocalDate

import models.core.{AccountingPeriodModel, AddressModel, CessationModel, ContactDetailsModel}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}

case class BusinessDetailsModel(incomeSourceId: String,
                                accountingPeriod: AccountingPeriodModel,
                                tradingName: Option[String],
                                address: Option[AddressModel],
                                contactDetails: Option[ContactDetailsModel],
                                tradingStartDate: Option[LocalDate],
                                cashOrAccruals: Option[Boolean],
                                seasonal: Option[Boolean],
                                cessation: Option[CessationModel],
                                paperless: Option[Boolean],
                                firstAccountingPeriodEndDate: Option[LocalDate],
                                latencyDetails: Option[LatencyDetails])


object BusinessDetailsModel {

  val desReads: Reads[BusinessDetailsModel] = (
    (__ \ "incomeSourceId").read[String] and
      __.read(AccountingPeriodModel.desReads) and
      (__ \ "tradingName").readNullable[String] and
      (__ \ "businessAddressDetails").readNullable(AddressModel.desReads) and
      (__ \ "businessContactDetails").readNullable(ContactDetailsModel.desReads) and
      (__ \ "tradingStartDate").readNullable[LocalDate] and
      (__ \ "cashOrAccruals").readNullable[Boolean].orElse(
        (__ \ "cashOrAccruals").read[String].map {
          case "cash" => Some(false)
          case "accruals" => Some(true)
          case _ => throw new RuntimeException("Invalid value for cashOrAccruals")
        }
      ) and
      (__ \ "seasonal").readNullable[Boolean] and
      (__ \ "cessationDate").readNullable[LocalDate] and
      (__ \ "cessationReason").readNullable[String] and
      (__ \ "paperLess").readNullable[Boolean] and
      (__ \ "firstAccountingPeriodEndDate").readNullable[LocalDate] and
      (__ \ "latencyDetails").readNullable[LatencyDetails]
    )(BusinessDetailsModel.applyWithFields _)

  def applyWithFields(incomeSourceId: String,
                      accountingPeriodModel: AccountingPeriodModel,
                      tradingName: Option[String],
                      address: Option[AddressModel],
                      contactDetails: Option[ContactDetailsModel],
                      tradingStartDate: Option[LocalDate],
                      cashOrAccruals: Option[Boolean],
                      seasonal: Option[Boolean],
                      cessationDate: Option[LocalDate],
                      cessationReason: Option[String],
                      paperless: Option[Boolean],
                      firstAccountingPeriodEndDate: Option[LocalDate],
                      latencyDetails: Option[LatencyDetails]): BusinessDetailsModel =
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
      paperless,
      firstAccountingPeriodEndDate,
      latencyDetails
    )

  implicit val format: Format[BusinessDetailsModel] = Json.format[BusinessDetailsModel]


}

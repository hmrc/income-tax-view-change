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

package models.hip.incomeSourceDetails

import models.hip.core.{AccountingPeriodModel, ContactDetailsModel, CustomReads, CessationModel}
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

case class PropertyDetailsModel(incomeSourceId: String,
                                accountingPeriod: AccountingPeriodModel,
                                contactDetails: Option[ContactDetailsModel],
                                propertiesRented: Option[PropertiesRentedModel],
                                cessation: Option[CessationModel],
                                paperless: Option[Boolean],
                                firstAccountingPeriodEndDate: Option[LocalDate],
                                incomeSourceType: Option[String],
                                contextualTaxYear: Option[String],
                                tradingStartDate: Option[LocalDate],
                                latencyDetails: Option[LatencyDetails],
                                quarterTypeElection: Option[QuarterTypeElection])

object PropertyDetailsModel extends CustomReads {

  def applyWithFields(incomeSourceId: String,
                      accountingPeriod: AccountingPeriodModel,
                      email: Option[String],
                      uk: Option[Int],
                      eea: Option[Int],
                      nonEea: Option[Int],
                      total: Option[Int],
                      cessationDate: Option[LocalDate],
                      paperless: Option[Boolean],
                      firstAccountingPeriodEndDate: Option[LocalDate],
                      incomeSourceType: Option[String],
                      contextualTaxYear: Option[String],
                      tradingStartDate: Option[LocalDate],
                      latencyDetails: Option[LatencyDetails],
                      quarterTypeElection: Option[QuarterTypeElection]): PropertyDetailsModel =
    PropertyDetailsModel(
      incomeSourceId,
      accountingPeriod,
      ContactDetailsModel.propertyContactDetails(email),
      PropertiesRentedModel.propertiesRented(uk, eea, nonEea, total),
      CessationModel.cessation(cessationDate),
      paperless,
      firstAccountingPeriodEndDate,
      if(incomeSourceType.contains("02")) Some("uk-property") else if(incomeSourceType.contains("03")) Some("foreign-property") else None,
      contextualTaxYear,
      tradingStartDate,
      latencyDetails,
      quarterTypeElection
    )

  implicit val reads: Reads[PropertyDetailsModel] = (
    (__ \ "incomeSourceId").read[String] and
      __.read(AccountingPeriodModel.reads) and
      (__ \ "email").readNullable[String] and
      (__ \ "numPropRentedUK").readNullable[Int](readInt) and
      (__ \ "numPropRentedEEA").readNullable[Int](readInt) and
      (__ \ "numPropRentedNONEEA").readNullable[Int](readInt) and
      (__ \ "numPropRented").readNullable[Int](readInt) and
      (__ \ "cessationDate").readNullable[LocalDate] and
      (__ \ "paperLessFlag").readNullable[Boolean] and
      (__ \ "firstAccountingPeriodEndDate").readNullable[LocalDate] and
      (__ \ "incomeSourceType").readNullable[String] and
      (__ \ "contextualTaxYear").readNullable[String] and
      (__ \ "tradingSDate").readNullable[LocalDate] and
      (__ \ "latencyDetails").readNullable[LatencyDetails] and
      (__ \ "quarterTypeElection").readNullable[QuarterTypeElection]
    )(PropertyDetailsModel.applyWithFields _)

  implicit val writes: OWrites[PropertyDetailsModel] = Json.writes[PropertyDetailsModel]

}

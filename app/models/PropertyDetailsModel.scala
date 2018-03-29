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

import models.core.{AccountingPeriodModel, CessationModel, ContactDetailsModel, CustomReads}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}

case class PropertyDetailsModel(incomeSourceId: String,
                                accountingPeriod: AccountingPeriodModel,
                                contactDetails: Option[ContactDetailsModel],
                                propertiesRented: Option[PropertiesRentedModel],
                                cessation: Option[CessationModel],
                                paperless: Option[Boolean])

object PropertyDetailsModel extends CustomReads {

  val desReads: Reads[PropertyDetailsModel] = (
    (__ \ "incomeSourceId").read[String] and
      __.read(AccountingPeriodModel.desReads) and
      (__ \ "emailAddress").readNullable[String] and
      (__ \ "numPropRentedUK").readNullable[Int](readInt) and
      (__ \ "numPropRentedEEA").readNullable[Int](readInt) and
      (__ \ "numPropRentedNONEEA").readNullable[Int](readInt) and
      (__ \ "numPropRented").readNullable[Int](readInt) and
      (__ \ "cessationDate").readNullable[LocalDate] and
      (__ \ "cessationReason").readNullable[String] and
      (__ \ "paperLess").readNullable[Boolean]
    )(PropertyDetailsModel.applyWithFields _)

  def applyWithFields(incomeSourceId: String,
                 accountingPeriod: AccountingPeriodModel,
                 email: Option[String],
                 uk: Option[Int],
                 eea: Option[Int],
                 nonEea: Option[Int],
                 total: Option[Int],
                 cessationDate: Option[LocalDate],
                 cessationReason: Option[String],
                 paperless: Option[Boolean]): PropertyDetailsModel = PropertyDetailsModel(
    incomeSourceId,
    accountingPeriod,
    ContactDetailsModel.propertyContactDetails(email),
    PropertiesRentedModel.propertiesRented(uk,eea,nonEea,total),
    CessationModel.cessation(cessationDate, cessationReason),
    paperless
  )

  implicit val format: Format[PropertyDetailsModel] = Json.format[PropertyDetailsModel]

}

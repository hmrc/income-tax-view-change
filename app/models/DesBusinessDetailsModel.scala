/*
 * Copyright 2017 HM Revenue & Customs
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

import play.api.libs.json.{Json, OFormat}

sealed trait DesResponseModel

case class DesBusinessDetailsModel(
                                  safeId: String,
                                  nino: String,
                                  mtdbsa: String,
                                  propertyIncome: Option[Boolean],
                                  businessData: Option[List[BusinessDataModel]],
                                  propertyData: Option[PropertyDataModel]
                                  ) extends DesResponseModel

case class BusinessDataModel(
                            incomeSourceId: String,
                            accountingPeriodStartDate: String,
                            accountingPeriodEndDate: String,
                            tradingName: Option[String],
                            businessAddressDetails: Option[BusinessAddressModel],
                            businessContactDetails: Option[BusinessContactModel],
                            tradingStartDate: Option[String],
                            cashOrAccruals: Option[String],
                            seasonal: Option[Boolean],
                            cessationDate: Option[String],
                            cessationReason: Option[String],
                            paperless: Option[Boolean]
                            )

case class BusinessAddressModel(
                               addressLine1: String,
                               addressLine2: Option[String],
                               addressLine3: Option[String],
                               addressLine4: Option[String],
                               postalCode: String,
                               countryCode: String
                               )

case class BusinessContactModel(
                               phoneNumber: Option[String],
                               mobileNumber: Option[String],
                               faxNumber: Option[String],
                               emailAddress: Option[String]
                               )

case class PropertyDataModel(
                            incomeSourceId: String,
                            accountingPeriodStartDate: String,
                            accountingPeriodEndDate: String,
                            numPropRented: Option[String],
                            numPropRentedUK: Option[String],
                            numPropRentedEEA: Option[String],
                            numPropRentedNONEEA: Option[String],
                            emailAddress: Option[String],
                            cessationDate: Option[String],
                            cessationReason: Option[String],
                            paperless: Option[Boolean]
                            )

case class DesBusinessDetailsErrorModel(status: Int, reason: String)

object DesBusinessDetailsModel {
  implicit val format: OFormat[DesBusinessDetailsModel] = Json.format[DesBusinessDetailsModel]
  implicit val bFormat: OFormat[BusinessDataModel] = BusinessDataModel.format
  implicit val pFormat: OFormat[PropertyDataModel] = PropertyDataModel.format
}
object BusinessDataModel {
  implicit val format: OFormat[BusinessDataModel] = Json.format[BusinessDataModel]
  implicit val contactFormat: OFormat[BusinessContactModel] = BusinessContactModel.format
  implicit val addressFormat: OFormat[BusinessAddressModel] = BusinessAddressModel.format
}
object BusinessContactModel {
  implicit val format: OFormat[BusinessContactModel] = Json.format[BusinessContactModel]
}
object BusinessAddressModel {
  implicit val format: OFormat[BusinessAddressModel] = Json.format[BusinessAddressModel]
}
object PropertyDataModel {
  implicit val format: OFormat[PropertyDataModel] = Json.format[PropertyDataModel]
}

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

case class Nino(nino: String) extends DesResponseModel

case class DesBusinessDetails(
                               safeId: String,
                               nino: String,
                               mtdbsa: String,
                               propertyIncome: Option[Boolean],
                               businessData: Option[List[BusinessData]],
                               propertyData: Option[PropertyData]
                                  ) extends DesResponseModel

case class BusinessData(
                         incomeSourceId: String,
                         accountingPeriodStartDate: String,
                         accountingPeriodEndDate: String,
                         tradingName: Option[String],
                         businessAddressDetails: Option[BusinessAddress],
                         businessContactDetails: Option[BusinessContact],
                         tradingStartDate: Option[String],
                         cashOrAccruals: Option[String],
                         seasonal: Option[Boolean],
                         cessationDate: Option[String],
                         cessationReason: Option[String],
                         paperless: Option[Boolean]
                            )

case class BusinessAddress(
                               addressLine1: String,
                               addressLine2: Option[String],
                               addressLine3: Option[String],
                               addressLine4: Option[String],
                               postalCode: String,
                               countryCode: String
                               )

case class BusinessContact(
                               phoneNumber: Option[String],
                               mobileNumber: Option[String],
                               faxNumber: Option[String],
                               emailAddress: Option[String]
                               )

case class PropertyData(
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

case class DesBusinessDetailsError(status: Int, reason: String) extends DesResponseModel

object Nino {
  implicit val format: OFormat[Nino] = Json.format[Nino]
}
object DesBusinessDetails {
  implicit val bFormat: OFormat[BusinessData] = BusinessData.format
  implicit val pFormat: OFormat[PropertyData] = PropertyData.format
  implicit val format: OFormat[DesBusinessDetails] = Json.format[DesBusinessDetails]
}
object BusinessData {
  implicit val contactFormat: OFormat[BusinessContact] = BusinessContact.format
  implicit val addressFormat: OFormat[BusinessAddress] = BusinessAddress.format
  implicit val format: OFormat[BusinessData] = Json.format[BusinessData]
}
object BusinessContact {
  implicit val format: OFormat[BusinessContact] = Json.format[BusinessContact]
}
object BusinessAddress {
  implicit val format: OFormat[BusinessAddress] = Json.format[BusinessAddress]
}
object PropertyData {
  implicit val format: OFormat[PropertyData] = Json.format[PropertyData]
}
object DesBusinessDetailsError {
  implicit val format: OFormat[DesBusinessDetailsError] = Json.format[DesBusinessDetailsError]
}

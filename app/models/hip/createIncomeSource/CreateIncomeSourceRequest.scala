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

package models.hip.createIncomeSource

import play.api.libs.json._

sealed trait CreateIncomeSourceHipRequest

object CreateIncomeSourceHipRequest {

  implicit val reads: Reads[CreateIncomeSourceHipRequest] = {
    __.read[CreateBusinessIncomeSourceHipRequest]        .map(_.asInstanceOf[CreateIncomeSourceHipRequest]) orElse
    __.read[CreateForeignPropertyIncomeSourceHipRequest] .map(_.asInstanceOf[CreateIncomeSourceHipRequest]) orElse
    __.read[CreateUKPropertyIncomeSourceHipRequest]      .map(_.asInstanceOf[CreateIncomeSourceHipRequest])
  }

  implicit val writes: Writes[CreateIncomeSourceHipRequest] = Writes[CreateIncomeSourceHipRequest]{
    case x: CreateBusinessIncomeSourceHipRequest         => CreateBusinessIncomeSourceHipRequest         .format.writes(x)
    case x: CreateUKPropertyIncomeSourceHipRequest       => CreateUKPropertyIncomeSourceHipRequest       .format.writes(x)
    case x: CreateForeignPropertyIncomeSourceHipRequest  => CreateForeignPropertyIncomeSourceHipRequest  .format.writes(x)
    case x: CreateBusinessDetailsRequestError         => CreateBusinessDetailsRequestError         .format.writes(x)
  }
}

// *********************************************************************************************************************
// *                                                   Self-employment                                                 *
// *********************************************************************************************************************

final case class CreateBusinessIncomeSourceHipRequest(mtdbsa: String, businessDetails: List[BusinessDetails]) extends CreateIncomeSourceHipRequest {
  require(mtdbsa.matches("^[A-Z]{4}[0-9]{11}$"), "MTDBSA ID should be of 11 characters and a specific format")
  require(businessDetails.length == 1, "Only single business can be created at a time")
}

case class BusinessDetails(accountingPeriodStartDate: String,
                           accountingPeriodEndDate: String,
                           tradingName: String,
                           address: AddressDetails,
                           typeOfBusiness: String,
                           tradingStartDate: String,
                           cessationDate: Option[String],
                           cessationReason: Option[String]
                          )

case class AddressDetails(addressLine1: String,
                          addressLine2: Option[String],
                          addressLine3: Option[String],
                          addressLine4: Option[String],
                          countryCode: String,
                          postcode: Option[String]
                         )

object CreateBusinessIncomeSourceHipRequest {
  implicit val format: Format[CreateBusinessIncomeSourceHipRequest] = Json.format[CreateBusinessIncomeSourceHipRequest]
}

object BusinessDetails {
  implicit val format: Format[BusinessDetails] = Json.format[BusinessDetails]
}

object AddressDetails {
  implicit val format: Format[AddressDetails] = Json.format[AddressDetails]
}



// *********************************************************************************************************************
// *                                                   Property                                                        *
// *********************************************************************************************************************

final case class PropertyDetails(tradingStartDate: Option[String],
                                 startDate: String
                                ) {
  require(tradingStartDate.nonEmpty, "Trading start date must be provided")
  require(tradingStartDate.contains(startDate), "Trading start date and start date must be the same")
}

final case class CreateForeignPropertyIncomeSourceHipRequest(mtdbsa: String, foreignPropertyDetails: PropertyDetails) extends CreateIncomeSourceHipRequest {
  require(mtdbsa.matches("^[A-Z]{4}[0-9]{11}$"), "MTDBSA ID should be of 11 characters and a specific format")
}

final case class CreateUKPropertyIncomeSourceHipRequest(mtdbsa: String, ukPropertyDetails: PropertyDetails) extends CreateIncomeSourceHipRequest{
  require(mtdbsa.matches("^[A-Z]{4}[0-9]{11}$"), "MTDBSA ID should be of 11 characters and a specific format")
}

object PropertyDetails {
  implicit val format: Format[PropertyDetails] = Json.format[PropertyDetails]
}

object CreateForeignPropertyIncomeSourceHipRequest {
  implicit val format: Format[CreateForeignPropertyIncomeSourceHipRequest] = Json.format[CreateForeignPropertyIncomeSourceHipRequest]
}

object CreateUKPropertyIncomeSourceHipRequest {
  implicit val format: Format[CreateUKPropertyIncomeSourceHipRequest] = Json.format[CreateUKPropertyIncomeSourceHipRequest]
}

case class CreateBusinessDetailsRequestError(reason: String) extends CreateIncomeSourceHipRequest

object CreateBusinessDetailsRequestError {
  implicit val format: Format[CreateBusinessDetailsRequestError] = Json.format
}

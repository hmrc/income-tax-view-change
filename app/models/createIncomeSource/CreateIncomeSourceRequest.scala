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

package models.createIncomeSource
import play.api.libs.json._

sealed trait CreateIncomeSourceRequest

object CreateIncomeSourceRequest {

  implicit val reads: Reads[CreateIncomeSourceRequest] = {
    __.read[CreateBusinessIncomeSourceRequest]        .map(_.asInstanceOf[CreateIncomeSourceRequest]) orElse
    __.read[CreateForeignPropertyIncomeSourceRequest] .map(_.asInstanceOf[CreateIncomeSourceRequest]) orElse
    __.read[CreateUKPropertyIncomeSourceRequest]      .map(_.asInstanceOf[CreateIncomeSourceRequest])
  }

  implicit val writes: Writes[CreateIncomeSourceRequest] = Writes[CreateIncomeSourceRequest]{
    case x: CreateBusinessIncomeSourceRequest         => CreateBusinessIncomeSourceRequest         .format.writes(x)
    case x: CreateUKPropertyIncomeSourceRequest       => CreateUKPropertyIncomeSourceRequest       .format.writes(x)
    case x: CreateForeignPropertyIncomeSourceRequest  => CreateForeignPropertyIncomeSourceRequest  .format.writes(x)
    case x: CreateBusinessDetailsRequestError         => CreateBusinessDetailsRequestError         .format.writes(x)
  }
}

// *********************************************************************************************************************
// *                                                   Self-employment                                                 *
// *********************************************************************************************************************

final case class CreateBusinessIncomeSourceRequest(businessDetails: List[BusinessDetails]) extends CreateIncomeSourceRequest {
  require(businessDetails.length == 1, "Only single business can be created at a time")
  require(businessDetails.head.cashOrAccrualsFlag.forall(_.matches("^[A-Z]+$")), "Accounting method must be capitalised")
}

case class BusinessDetails(accountingPeriodStartDate: String,
                           accountingPeriodEndDate: String,
                           tradingName: String,
                           addressDetails: AddressDetails,
                           typeOfBusiness: Option[String],
                           tradingStartDate: String,
                           cashOrAccrualsFlag: Option[String],
                           cessationDate: Option[String],
                           cessationReason: Option[String]
                          ) {
  def toHipModel: models.hip.createIncomeSource.BusinessDetails = {
    models.hip.createIncomeSource.BusinessDetails(
      accountingPeriodStartDate,
      accountingPeriodEndDate,
      tradingName,
      addressDetails.toHipModel,
      typeOfBusiness.getOrElse(throw new Exception("TypeOfBusiness does not exists")),
      tradingStartDate,
      cashOrAccrualsFlag.map(_.head.toString).getOrElse(throw new Exception("CashOrAccrual flag does not exists")),
      cessationDate,
      cessationReason
    )
  }
}

case class AddressDetails(addressLine1: String,
                          addressLine2: Option[String],
                          addressLine3: Option[String],
                          addressLine4: Option[String],
                          countryCode: Option[String],
                          postalCode: Option[String]
                         ) {
  def toHipModel: models.hip.createIncomeSource.AddressDetails = {
    models.hip.createIncomeSource.AddressDetails(
      addressLine1, addressLine2, addressLine3, addressLine4, countryCode.getOrElse("GB"), postalCode
    )
  }
}

object CreateBusinessIncomeSourceRequest {
  implicit val format: Format[CreateBusinessIncomeSourceRequest] = Json.format[CreateBusinessIncomeSourceRequest]
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

final case class PropertyDetails(tradingStartDate: String,
                                 cashOrAccrualsFlag: Option[String],
                                 startDate: String
                                ) {
  require(cashOrAccrualsFlag.forall(_.matches("^[A-Z]+$")), "Accounting method must be capitalised")
  require(tradingStartDate.nonEmpty, "Trading start date must be provided")
  require(tradingStartDate == startDate, "Trading start date and start date must be the same")

  def toHipModel: models.hip.createIncomeSource.PropertyDetails = {
    models.hip.createIncomeSource.PropertyDetails(
      Some(tradingStartDate),
      cashOrAccrualsFlag.map(_.head.toString).getOrElse(throw new Exception("CashOrAccrual flag does not exists")),
      startDate
    )
  }
}

final case class CreateForeignPropertyIncomeSourceRequest(foreignPropertyDetails: PropertyDetails) extends CreateIncomeSourceRequest

final case class CreateUKPropertyIncomeSourceRequest(ukPropertyDetails: PropertyDetails) extends CreateIncomeSourceRequest

object PropertyDetails {
  implicit val format: Format[PropertyDetails] = Json.format[PropertyDetails]
}

object CreateForeignPropertyIncomeSourceRequest {
  implicit val format: Format[CreateForeignPropertyIncomeSourceRequest] = Json.format[CreateForeignPropertyIncomeSourceRequest]
}

object CreateUKPropertyIncomeSourceRequest {
  implicit val format: Format[CreateUKPropertyIncomeSourceRequest] = Json.format[CreateUKPropertyIncomeSourceRequest]
}

case class CreateBusinessDetailsRequestError(reason: String) extends CreateIncomeSourceRequest

object CreateBusinessDetailsRequestError {
  implicit val format: Format[CreateBusinessDetailsRequestError] = Json.format
}

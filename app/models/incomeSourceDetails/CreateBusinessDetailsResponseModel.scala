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

import play.api.libs.json._
import java.time.LocalDate

trait CreateBusinessDetailsResponseModel

object CreateBusinessDetailsResponseModel {

  final case class CreateBusinessDetailsModel(tradingName: Option[String],
                                              tradingStartDate: Option[LocalDate],
                                              typeOfBusiness: Option[String],
                                              addressLine1: Option[String],
                                              addressLine2: Option[String],
                                              addressLine3: Option[String],
                                              addressLine4: Option[String],
                                              postalCode: Option[String],
                                              countryCode: Option[String],
                                              cashOrAccrualsFlag: Boolean,
                                              accountingPeriodStartDate: Option[LocalDate],
                                              accountingPeriodEndDate: Option[LocalDate]) extends CreateBusinessDetailsResponseModel

  object CreateBusinessDetailsModel {
    implicit val formats: OFormat[CreateBusinessDetailsModel] = Json.format[CreateBusinessDetailsModel]
  }

  final case class CreateBusinessDetailsErrorResponse(status: Int, message: String) extends CreateBusinessDetailsResponseModel
}

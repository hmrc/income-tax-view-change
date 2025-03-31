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

package constants

import models.core.AddressModel
import play.api.libs.json.Json

object AddressDetailsTestConstants {

  val testAddressModel = AddressModel(
    addressLine1 = "Test Lane",
    addressLine2 = Some("Test Unit"),
    addressLine3 = Some("Test Town"),
    addressLine4 = Some("Test City"),
    postCode = Some("TE5 7TE"),
    countryCode = "GB")

  val testMinimumAddressModel = AddressModel(
    addressLine1 = "Test Lane",
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    postCode = None,
    countryCode = "GB")

  val testAddressJson = Json.obj(
    "addressLine1" -> "Test Lane",
    "addressLine2" -> "Test Unit",
    "addressLine3" -> "Test Town",
    "addressLine4" -> "Test City",
    "postalCode" -> "TE5 7TE",
    "countryCode" -> "GB"
  )

  val testAddressToJson = Json.obj(
    "addressLine1" -> "Test Lane",
    "addressLine2" -> "Test Unit",
    "addressLine3" -> "Test Town",
    "addressLine4" -> "Test City",
    "postCode" -> "TE5 7TE",
    "countryCode" -> "GB"
  )

  val testMinimumAddressJson = Json.obj(
    "addressLine1" -> "Test Lane",
    "countryCode" -> "GB"
  )


}
